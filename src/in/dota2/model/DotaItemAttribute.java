package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaItemAttribute extends BaseEntity {

	private String name;
	private String value;
	private String effectTypeDisplay;
	private Boolean hidden;
	private String descriptionFormat;
	private String descriptionString;
	private Integer uniqueIndex;
	private DotaItem item;

	public DotaItemAttribute(){}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getValue(){
		return this.value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getEffectTypeDisplay(){
		return this.effectTypeDisplay;
	}

	public void setEffectTypeDisplay(String effectTypeDisplay){
		this.effectTypeDisplay = effectTypeDisplay;
	}

	public Boolean getHidden(){
		return this.hidden;
	}

	public void setHidden(Boolean hidden){
		this.hidden = hidden;
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

	public DotaItem getItem(){
		return item;
	}
	
	public void setItem(DotaItem item){
		this.item = item;
	}

	public Integer getUniqueIndex(){
		return this.uniqueIndex;
	}
	
	public void setUniqueIndex(Integer uniqueIndex){
		this.uniqueIndex = uniqueIndex;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof DotaItemAttribute){
			return name.equals(((DotaItemAttribute) o).name);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Name: ").append(this.name).append("; ");		
		return sb.toString();
	}

}