package in.dota2.model;

import in.dota2.entity.BaseEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/*
 * Represents Player's trading offer
 */
public class DotaPlayerOffer extends BaseEntity {

	private String message;
	private Date dateAdded;
	private Set<DotaPlayerOfferItem> playerItems = new HashSet<DotaPlayerOfferItem>();
	private Set<DotaPlayerDemandedItem> demandedItems = new HashSet<DotaPlayerDemandedItem>();

	public DotaPlayerOffer(){}

	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}

	public Date getDateAdded(){
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded){
		this.dateAdded = dateAdded;
	}

    public Set<DotaPlayerOfferItem> getPlayerItems() {
        return playerItems;
    }

    public void setPlayerItems(Set<DotaPlayerOfferItem> items) {
        this.playerItems = items;
    }

    public void addPlayerItem(DotaPlayerOfferItem item){
        if(playerItems == null)
            playerItems = new HashSet<DotaPlayerOfferItem>();
        playerItems.add(item);
    }

    public Set<DotaPlayerDemandedItem> getDemandedItems() {
        return demandedItems;
    }

    public void setDemandedItems(Set<DotaPlayerDemandedItem> items) {
        this.demandedItems = items;
    }

    public void addDemandedItem(DotaPlayerDemandedItem item){
        if(demandedItems == null)
            demandedItems = new HashSet<DotaPlayerDemandedItem>();
        demandedItems.add(item);
    }
    
}