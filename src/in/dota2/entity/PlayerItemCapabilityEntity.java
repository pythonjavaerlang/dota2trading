package in.dota2.entity;

public class PlayerItemCapabilityEntity {
	private String name;
	private String description;

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return this.description;
	}

	public void setDescription(String description){
		this.description = description;
	}

}