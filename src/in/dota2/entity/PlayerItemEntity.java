package in.dota2.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * This entity will be returned by JSON controllers
 */
public class PlayerItemEntity {

	private String name;
	private Integer uniqueIndex;
	private String typeDisplay;
	private String description;
	private Integer level;
	private Boolean cannotTrade;
	private Boolean cannotCraft;
	private Integer quality;
	private String qualityDisplay;
	private String customName;
	private String customDescription;
	private Set<PlayerItemCapabilityEntity> capabilities = new HashSet<PlayerItemCapabilityEntity>();
	private Set<PlayerItemAttributeEntity> attributes = new HashSet<PlayerItemAttributeEntity>();
	private Integer imageId;
	private Integer largeImageId;

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

	public Integer getLevel(){
		return this.level;
	}

	public void setLevel(Integer level){
		this.level = level;
	}

	public Integer getUniqueIndex(){
		return this.uniqueIndex;
	}
	
	public void setUniqueIndex(Integer uniqueIndex){
		this.uniqueIndex = uniqueIndex;
	}
	
	public Boolean getCannotTrade(){
		return this.cannotTrade;
	}

	public void setCannotTrade(Boolean cannotTrade){
		this.cannotTrade = cannotTrade;
	}

	public Boolean getCannotCraft(){
		return this.cannotCraft;
	}

	public void setCannotCraft(Boolean cannotCraft){
		this.cannotCraft = cannotCraft;
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

	public String getCustomName(){
		return this.customName;
	}

	public void setCustomName(String customName){
		this.customName = customName;
	}

	public String getCustomDescription(){
		return this.customDescription;
	}

	public void setCustomDescription(String customDescription){
		this.customDescription = customDescription;
	}

	public String getTypeDisplay(){
		return typeDisplay;
	}

	public void setTypeDisplay(String typeDisplay){
		this.typeDisplay = typeDisplay;
	}

	public Integer getImageId(){
		return imageId;
	}
	
	public void setImageId(Integer imageId){
		this.imageId = imageId;
	}
	
	public Integer getLargeImageId(){
		return largeImageId;
	}
	
	public void setLargeImageId(Integer imageId){
		this.largeImageId = imageId;
	}

	public Set<PlayerItemCapabilityEntity> getCapabilities() {
        return this.capabilities;
    }

    public void setCapabilities(Set<PlayerItemCapabilityEntity> capabilities) {
        this.capabilities = capabilities;
    }

    public void addCapability(PlayerItemCapabilityEntity capability){
        if(capabilities == null)
            capabilities = new HashSet<PlayerItemCapabilityEntity>();
        capabilities.add(capability);
    }

    public Boolean hasCapability(String name){
    	Iterator<PlayerItemCapabilityEntity> iter = capabilities.iterator();
    	while(iter.hasNext()){
    		if(iter.next().getName().equals(name)) return true;
    	}
    	return false;
    }

    public Set<PlayerItemAttributeEntity> getAttributes() {
        return attributes;
    }

    public PlayerItemAttributeEntity getAttribute(Integer uniqueIndex) {
    	Iterator<PlayerItemAttributeEntity> iter = attributes.iterator();
    	while(iter.hasNext()){
    		PlayerItemAttributeEntity attr = iter.next();
    		if(attr.getUniqueIndex().equals(uniqueIndex)) return attr;
    	}
    	return null;
    }

    public Boolean hasAttribute(Integer uniqueIndex){
    	Iterator<PlayerItemAttributeEntity> iter = attributes.iterator();
    	while(iter.hasNext()){
    		if(iter.next().getUniqueIndex().equals(uniqueIndex)) return true;
    	}
    	return false;
    }

    public void setAttributes(Set<PlayerItemAttributeEntity> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(PlayerItemAttributeEntity attribute){
        if(attributes == null)
            attributes = new HashSet<PlayerItemAttributeEntity>();
        attributes.add(attribute);
    }

}
