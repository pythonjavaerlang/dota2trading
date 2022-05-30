package in.dota2.model;

import in.dota2.entity.BaseEntity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DotaItem extends BaseEntity {

	// Levels are purely cosmetic and do not modify the effectiveness of any item
	private String name;
	private Integer uniqueIndex;
	private String typeDisplay;
	private String description;
	private Boolean properName;
	private Integer quality;
	private String qualityDisplay;
	private Set<DotaItemCapability> capabilities = new HashSet<DotaItemCapability>();
	private Set<DotaItemAttribute> attributes = new HashSet<DotaItemAttribute>();
	private DotaImage image;
	
	public DotaItem(){}

	public String getName(){
		return this.name;
	}

	public String getFullName(){
		if(properName) return String.format("The %s", name);
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Integer getQuality(){
		return this.quality;
	}

	public void setQuality(Integer quality){
		this.quality = quality;
	}

	public String getQualityDisplay(){
		return this.qualityDisplay;
	}
	
	public void setQualityDisplay(String qualityDisplay){
		this.qualityDisplay = qualityDisplay;
	}

	public Integer getUniqueIndex(){
		return this.uniqueIndex;
	}
	
	public void setUniqueIndex(Integer uniqueIndex){
		this.uniqueIndex = uniqueIndex;
	}
	
	public String getTypeDisplay(){
		return this.typeDisplay;
	}
	
	public void setTypeDisplay(String typeDisplay){
		this.typeDisplay = typeDisplay;
	}

	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}

	public Boolean getProperName(){
		return this.properName;
	}

	public void setProperName(Boolean properName){
		this.properName = properName;
	}

	public DotaImage getImage(){
		return image;
	}

	public void setImage(DotaImage image){
		this.image = image;
	}

    public Set<DotaItemCapability> getCapabilities() {
        return this.capabilities;
    }

    public void setCapabilities(Set<DotaItemCapability> capabilities) {
        this.capabilities = capabilities;
    }

    public void addCapability(DotaItemCapability capability){
        if(capabilities == null)
            capabilities = new HashSet<DotaItemCapability>();
        capabilities.add(capability);
    }

    public Boolean hasCapability(String name){
    	Iterator<DotaItemCapability> iter = capabilities.iterator();
    	while(iter.hasNext()){
    		if(iter.next().getName().equals(name)) return true;
    	}
    	return false;
    }

    public Set<DotaItemAttribute> getAttributes() {
        return attributes;
    }

    public DotaItemAttribute getAttribute(Integer uniqueIndex) {
    	Iterator<DotaItemAttribute> iter = attributes.iterator();
    	while(iter.hasNext()){
    		DotaItemAttribute attr = iter.next();
    		if(attr.getUniqueIndex().equals(uniqueIndex)) return attr;
    	}
    	return null;
    }

    public Boolean hasAttribute(Integer uniqueIndex){
    	Iterator<DotaItemAttribute> iter = attributes.iterator();
    	while(iter.hasNext()){
    		if(iter.next().getUniqueIndex().equals(uniqueIndex)) return true;
    	}
    	return false;
    }

    public void setAttributes(Set<DotaItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(DotaItemAttribute attribute){
        if(attributes == null)
            attributes = new HashSet<DotaItemAttribute>();
        attributes.add(attribute);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Name: ").append(this.name).append("; ");		
		return sb.toString();
	}
    
}