package in.dota2.model;

import in.dota2.entity.BaseEntity;

public class DotaPlayerDemandedItem extends BaseEntity {

	private DotaPlayerOffer offer;
	private DotaItem demandedItem;

	public DotaPlayerDemandedItem(){}
	
	public DotaPlayerOffer getOffer(){
		return offer;
	}
	
	public void setOffer(DotaPlayerOffer offer){
		this.offer = offer;
	}
	
	public DotaItem getDemandedItem(){
		return demandedItem;
	}

	public void setDemandedItem(DotaItem item){
		this.demandedItem = item; 
	}

}
