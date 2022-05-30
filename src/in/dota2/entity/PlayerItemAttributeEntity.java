package in.dota2.entity;

public class PlayerItemAttributeEntity{

	private String value;
	private Integer uniqueIndex;

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