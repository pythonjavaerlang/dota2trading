package in.dota2.util;

public class SteamResponse {
	public String response;
	public Integer responseCode;
	
	public Integer getResponseCode(){
		return this.responseCode;
	}
	public void setResponseCode(Integer code){
		this.responseCode = code;
	}
	
	public String getResponse(){
		return this.response;
	}
	public void setResponse(String response){
		this.response = response;
	}

}