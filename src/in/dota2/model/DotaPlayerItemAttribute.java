package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaPlayerItemAttribute extends BaseEntity {

	private DotaPlayerItem playerItem;
	private String value;
	private Integer uniqueIndex;

	public DotaPlayerItemAttribute(){}

	public DotaPlayerItem getPlayerItem(){
		return playerItem;
	}
	
	public void setPlayerItem(DotaPlayerItem playerItem){
		this.playerItem = playerItem;
	}

	public String getValue(){
		return this.value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public Integer getUniqueIndex(){
		return this.uniqueIndex;
	}

	public void setUniqueIndex(Integer uniqueIndex){
		this.uniqueIndex = uniqueIndex;
	}
}
