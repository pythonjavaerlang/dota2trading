package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaPlayerOfferItem extends BaseEntity {

	private DotaPlayerOffer offer;
	private DotaPlayerItem playerItem;

	public DotaPlayerOfferItem(){}
	
	public DotaPlayerOffer getOffer(){
		return offer;
	}
	
	public void setOffer(DotaPlayerOffer offer){
		this.offer = offer;
	}
	
	public DotaPlayerItem getPlayerItem(){
		return playerItem;
	}

	public void setPlayerItem(DotaPlayerItem item){
		this.playerItem = item; 
	}

}
