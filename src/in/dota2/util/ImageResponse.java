package in.dota2.util;

import java.awt.image.BufferedImage;

public class ImageResponse{

	Integer height;
	Integer width;
	String fn;
	String ext;
	BufferedImage data;

	public Integer getHeight(){
		return height;
	}

	public void setHeight(Integer height){
		this.height = height;
	}

	public Integer getWidth(){
		return width;
	}

	public void setWidth(Integer width){
		this.width = width;
	}

	public String getFn(){
		return fn;
	}

	public void setFn(String fn){
		this.fn = fn;
	}

	public String getExt(){
		return ext;
	}

	public void setExt(String ext){
		this.ext = ext;
	}

	public BufferedImage getData(){
		return data;
	}

	public void setData(BufferedImage data){
		this.data = data; 
	}

}