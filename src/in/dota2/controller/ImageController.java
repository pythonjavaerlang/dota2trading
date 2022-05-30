package in.dota2.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import in.dota2.model.DotaImage;
import in.dota2.model.DotaImageCatalogDAOInterface;
import in.dota2.service.DotaPlayerSummaryService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/image")
public class ImageController {

	private static final Logger logger = Logger.getLogger(DotaPlayerSummaryService.class);

	@Autowired
	private DotaImageCatalogDAOInterface dotaImageCatalogDAO;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	private ResponseEntity<byte[]> downloadImage(
			@RequestParam(value = "id", required = true) Integer imageId, 
			HttpServletResponse response){
		logger.debug("downloadImage called");

		final HttpHeaders headers = new HttpHeaders();
		String errorMessage = "ERROR: Could not find the file with specified id.";

		DotaImage image = dotaImageCatalogDAO.getImageById(imageId);
		if(image==null){
			logger.debug(String.format("Image with id %d not found", imageId));
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(errorMessage.getBytes(), headers, HttpStatus.NOT_FOUND);	
		}
		if(!image.canRead()){
			logger.debug(String.format("Image with id %d cannot be read", imageId));
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(errorMessage.getBytes(), headers, HttpStatus.NOT_FOUND);	
		}
		
		try {
	        BufferedImage img = image.read();
	        ByteArrayOutputStream bao = new ByteArrayOutputStream();

	        String ext = image.getExtension();
	        if(ext.equals("")) ext = "jpg";

	        ImageIO.write(img, ext, bao);

	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", image.getFn()));
	        response.setContentLength(image.getFileSize());

	        if(ext.equalsIgnoreCase("png")){
	        	headers.setContentType(MediaType.IMAGE_PNG);
	        } else if(ext.toLowerCase().startsWith("jp")){
	        	headers.setContentType(MediaType.IMAGE_JPEG);
	        } else if(ext.equalsIgnoreCase("gif")){
	        	headers.setContentType(MediaType.IMAGE_GIF);
	        }

	        return new ResponseEntity<byte[]>(bao.toByteArray(), headers, HttpStatus.OK);
	    } catch (IOException e) {
	    	logger.error(e);
			headers.setContentType(MediaType.TEXT_PLAIN);
			return new ResponseEntity<byte[]>(errorMessage.getBytes(), headers, HttpStatus.NOT_FOUND);	
	    }
		
	}

}
