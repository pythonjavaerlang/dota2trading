package in.dota2.model;

import in.dota2.entity.BaseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DotaAttribute extends BaseEntity {

	private String name;
	private String descriptionFormat;
	private String descriptionString;
	private Integer uniqueIndex;
	private Boolean hidden;
	private DotaPlayerItem playerItem;

	public DotaAttribute(){}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Integer getUniqueIndex(){
		return this.uniqueIndex;
	}
	
	public void setUniqueIndex(Integer uniqueIndex){
		this.uniqueIndex = uniqueIndex;
	}

	public String getDescriptionFormat(){
		return this.descriptionFormat;
	}

	public void setDescriptionFormat(String descriptionFormat){
		this.descriptionFormat = descriptionFormat;
	}

	public String getDescriptionString(){
		return this.descriptionString;
	}

	public void setDescriptionString(String descriptionString){
		this.descriptionString = descriptionString;
	}

	public Boolean getHidden(){
		return this.hidden;
	}

	public void setHidden(Boolean hidden){
		this.hidden = hidden;
	}

	public DotaPlayerItem getPlayerItem(){
		return playerItem;
	}
	
	public void setPlayerItem(DotaPlayerItem playerItem){
		this.playerItem = playerItem; 
	}
	
	/* 
	 * Formats description
	 */
	public String descriptionToString(String arg){
		if(descriptionFormat.equals("value_is_percentage")){
			return String.format("%d%%", arg);
		} else if(descriptionFormat.equals("value_is_inverted_percentage")){
			Integer value = 100-Integer.parseInt(arg);
			return String.format("%d%%", value);
		} else if(descriptionFormat.equals("value_is_additive")){
			return descriptionString.replace("%s1", arg);
		} else if(descriptionFormat.equals("value_is_color")){
			return descriptionString.replace("%s1", arg);
		} else if(descriptionFormat.equals("value_is_date")){
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
			return descriptionString.replace("%s1", dateFormat.format(new Date(Long.parseLong(arg))));
		} else if(descriptionFormat.equals("value_is_account_id")){
			return descriptionString;
		}else if(descriptionFormat.equals("value_is_game_time")){
			return descriptionString;
		}
		return descriptionString;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Name: ").append(this.name).append("; ");		
		return sb.toString();
	}

}