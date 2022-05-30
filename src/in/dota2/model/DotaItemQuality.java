package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaItemQuality extends BaseEntity{

	private Integer key;
	private String value;
	private String name; // translation of internal quality name

	public DotaItemQuality(){}

	public Integer getKey(){
		return key;
	}

	public void setKey(Integer key){
		this.key = key;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
}