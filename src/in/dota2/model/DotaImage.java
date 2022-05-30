package in.dota2.model;

import in.dota2.entity.BaseEntity;
import in.dota2.util.ImageResponse;
import in.dota2.util.SpringPropertiesUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**     
 *  Allows to store files in the following form:
 *  Convert object id to 9-digit filename,
 *  then split this string to 3 parts and store
 *  file like 000/000/001/000000001
 *  
 */

public class DotaImage extends BaseEntity {

	private static final Logger logger = Logger.getLogger(DotaImage.class);

	private String fn; // original filename
	private String sha1; // store sha1 of original images
	private DotaImage image;

	private String storeRoot;

	public DotaImage(){
		this.storeRoot = SpringPropertiesUtil.getProperty("global.store_root");
	}
	
	public String getFn(){
		return fn;
	}

	public void setFn(String fn){
		this.fn = fn;
	}

	public String getSha1(){
		return sha1;
	}

	public void setSha1(String sha1){
		this.sha1 = sha1;
	}

	public DotaImage getImage(){
		return image;
	}

	public void setImage(DotaImage image){
		this.image = image;
	}

	public String getFullPath(){
		if(getId()==null) return "";
		String root = this.storeRoot;
		if(!root.endsWith(File.separator)){
			root = String.format("%s%s", root, File.separator);
		}
		String fn = String.format("%09d", getId());
		File nextPath = new File(root, fn.substring(0, 3));
		nextPath = new File(nextPath, fn.substring(3, 6));
		return new File(nextPath, fn).getAbsolutePath().toString();
	}

	public String getRelativePath(){
		String path = getFullPath();
		if(path.equals("")){
			return "";
		}
		return path.substring(path.lastIndexOf(storeRoot)+path.length());
	}

	public String getBasename(){
		return String.format("%09d", getId());
	}

	public Boolean canRead(){
		String path = getFullPath();
		File f = new File(path);
		if(!f.exists()) return false;
		if(!f.canRead()) return false;
		return true;
	}

	public Boolean canWrite(){
		String path = getFullPath();
		File f = new File(path);
		if(!f.exists()) return false;
		if(!f.canWrite()) return false;
		return true;
	}

	public String getExtension(){
		if(fn==null) return "";
		return fn.substring(fn.lastIndexOf("."));
	}

	public BufferedImage read(){
		try{
			return ImageIO.read(new File(getFullPath()));
		} catch (IOException e){
			logger.info(e.toString());
		}
		return null;
	}

	public void write(ImageResponse imageResponse){
		String path = getFullPath();
		File f = new File(path);
		if(f.mkdirs()){
			try{
				ImageIO.write(imageResponse.getData(), imageResponse.getExt(), f);
				setFn(imageResponse.getFn());
				setSha1(sha1(path));
			} catch (IOException e){
				logger.info(e.toString());
			} catch (NoSuchAlgorithmException e) {
				logger.info(e.toString());
			}
		} else {
			logger.info("Failed to create directory for storing image.");
		}
	}

	public Integer getFileSize(){
		// I do not expect files larger than 2 GB
		return Long.valueOf(new File(getFullPath()).length()).intValue();
	}

	public static String sha1(String filename) throws NoSuchAlgorithmException, IOException {
	    MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

	    InputStream fis = new FileInputStream(filename);
	    int numRead;
	    byte[] buffer = new byte[1024];
	    do {
	    	numRead = fis.read(buffer);
	    	if (numRead > 0) {
	    		messageDigest.update(buffer, 0, numRead);
	    	}
	    } while (numRead != -1);
	    fis.close();

	    messageDigest.digest();

	    Formatter formatter = new Formatter();
	    for (final byte b : messageDigest.digest()) {
	    	formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	  }

}
