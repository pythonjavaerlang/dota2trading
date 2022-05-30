package in.dota2.model;

import in.dota2.entity.BaseEntity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DotaPlayerItem extends BaseEntity {

	private DotaPlayer dotaPlayer;
	private DotaItem dotaItem;
	private Integer uniqueIndex;
	private Integer level;
	private Boolean cannotTrade;
	private Boolean cannotCraft;
	private Integer quality;
	private String qualityDisplay;
	private String customName;
	private String customDescription;
	private Set<DotaPlayerItemAttribute> attributes = new HashSet<DotaPlayerItemAttribute>();

	public DotaPlayerItem(){}

	public DotaPlayer getDotaPlayer(){
		return this.dotaPlayer;
	}

	public void setDotaPlayer(DotaPlayer player){
		this.dotaPlayer = player;
	}

	public DotaItem getDotaItem(){
		return dotaItem;
	}

	public void setDotaItem(DotaItem dotaItem){
		this.dotaItem = dotaItem;
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

    public Set<DotaPlayerItemAttribute> getAttributes() {
        return attributes;
    }

    public DotaPlayerItemAttribute getAttribute(Integer uniqueIndex) {
    	Iterator<DotaPlayerItemAttribute> iter = attributes.iterator();
    	while(iter.hasNext()){
    		DotaPlayerItemAttribute attr = iter.next();
    		if(attr.getUniqueIndex().equals(uniqueIndex)) return attr;
    	}
    	return null;
    }

    public Boolean hasAttribute(Integer uniqueIndex){
    	Iterator<DotaPlayerItemAttribute> iter = attributes.iterator();
    	while(iter.hasNext()){
    		if(iter.next().getUniqueIndex().equals(uniqueIndex)) return true;
    	}
    	return false;
    }

    public void setAttributes(Set<DotaPlayerItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(DotaPlayerItemAttribute attribute){
        if(attributes == null)
            attributes = new HashSet<DotaPlayerItemAttribute>();
        attributes.add(attribute);
    }

}
