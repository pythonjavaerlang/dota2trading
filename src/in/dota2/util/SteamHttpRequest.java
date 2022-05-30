package in.dota2.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class SteamHttpRequest {

	/**
	 * Makes a HTTP request to a URL and receive response
	 * 
	 * @param requestUrl
	 *            the URL address
	 * @throws UnsupportedEncodingException 
	 * @throws MalformedURLException 
	 * @throws IOException
	 *                  
	 */

	private static final Logger logger = Logger.getLogger(SteamHttpRequest.class);

	public static SteamResponse sendHttpRequest(String requestUrl, Map<String, String> params) throws UnsupportedEncodingException, MalformedURLException {
		StringBuffer requestParams = new StringBuffer();

		String response = "";
		Integer responseCode = -1;

		if (params != null && params.size() > 0) {
			Iterator<String> paramIterator = params.keySet().iterator();
			while (paramIterator.hasNext()) {
				String key = paramIterator.next();
				String value = params.get(key);
				requestParams.append(URLEncoder.encode(key, "UTF-8"));
				requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
				requestParams.append("&");
			}
		}
		
		if(requestParams.length()>0) {
			requestUrl = new StringBuilder().append(requestUrl).append(requestParams.toString()).toString(); 
		}
		
		if(logger.isDebugEnabled()){
			logger.debug(String.format("Request parameters: %s", requestParams.toString()));
			logger.debug(String.format("Request URL: %s", requestUrl));
		}

		URL url = new URL(requestUrl);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setUseCaches(false);
			// I expect response
			connection.setDoInput(true);
			// It is GET request
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			InputStream input_stream = null;
			int contentLength = connection.getContentLength();

			try {
				input_stream = connection.getInputStream();
			} catch (IOException ioe) {
			    if (connection instanceof HttpURLConnection) {
			        HttpURLConnection httpConn = (HttpURLConnection) connection;
			        responseCode = httpConn.getResponseCode();
			        if (responseCode != 200) {
			        	input_stream = httpConn.getErrorStream();
			        	contentLength = httpConn.getContentLength();
			        }
			    }
			}
			responseCode = connection.getResponseCode();

			// reads response, store line by line in an array of Strings
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));

			// it is possible that web server do not set content-length header 
			if(contentLength < 0) contentLength = 1000;  

			StringBuilder content = new StringBuilder(contentLength);
			String line = "";
			while (line != null){
				 line = reader.readLine();
				 content.append(line);
			}

			response = content.toString();

			reader.close();

		} catch (IOException e) {
			if(logger.isDebugEnabled()){
				logger.debug(e);
			}
		}

		SteamResponse webResult = new SteamResponse();
		webResult.setResponse(response);
		webResult.setResponseCode(responseCode);
		return webResult;
	};
	
	/*
	 * If `expectedContentLength` provided and Content-Length, specified in response headers, 
	 * equals to `expectedContentLength`, then do not download data.
	 */
	public static ImageResponse downloadImage(String requestUrl, Integer expectedContentLength) throws UnsupportedEncodingException, MalformedURLException {

		if(logger.isDebugEnabled()){
			logger.debug(String.format("Request URL: %s", requestUrl));
		}

		URL url = new URL(requestUrl);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setUseCaches(false);
			// I expect response
			connection.setDoInput(true);
			// It is GET request
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			InputStream input_stream = null;
			Integer contentLength = connection.getContentLength();
			Integer responseCode;

			try {
				input_stream = connection.getInputStream();
			} catch (IOException ioe) {
			    if (connection instanceof HttpURLConnection) {
			        HttpURLConnection httpConn = (HttpURLConnection) connection;
			        responseCode = httpConn.getResponseCode();
			        if (responseCode != 200) {
			        	input_stream = httpConn.getErrorStream();
			        	contentLength = httpConn.getContentLength();
			        }
			    }
			}
			responseCode = connection.getResponseCode();

			if(expectedContentLength!=null){
				if(expectedContentLength==contentLength){
					// No need to download file
					return null;
				}
			}

			BufferedInputStream bis = new BufferedInputStream(input_stream);
			BufferedImage image = ImageIO.read(bis);
			ImageResponse response = new ImageResponse();
			response.setHeight(image.getHeight());
			response.setWidth(image.getWidth());
			response.setData(image);

			String fn = requestUrl.substring(requestUrl.lastIndexOf("/")+1);
			response.setFn(fn);
			response.setExt(fn.substring(fn.lastIndexOf(".")+1));
			return response;

		} catch (IOException e) {
			if(logger.isDebugEnabled()){
				logger.debug(e);
			}
		}
		return null;
	};

}
