package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaItemCapability extends BaseEntity {

	private String name;
	private String description;

	public DotaItemCapability(){}

	public String getName(){
		return this.name;
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

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof DotaItemCapability){
			return name.equals(((DotaItemCapability) o).name);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Name: ").append(getName()).append("; ");		
		return sb.toString();
	}
	
}