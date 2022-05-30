package in.dota2.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import in.dota2.entity.PlayerItemEntity;
import in.dota2.model.DotaImage;
import in.dota2.model.DotaItem;
import in.dota2.model.DotaItemCatalogDAOInterface;
import in.dota2.model.DotaPlayer;
import in.dota2.model.DotaPlayerCatalogDAOInterface;
import in.dota2.model.DotaPlayerItem;
import in.dota2.service.DotaItemService;
import in.dota2.util.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	private DotaItemService dotaItemService;

	@Autowired
	private DotaItemCatalogDAOInterface dotaItemCatalogDAO;

	@Autowired
	private DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO;

	@Autowired
	public ItemController(DotaItemCatalogDAOInterface dotaItemCatalog) {
		this.dotaItemCatalogDAO = dotaItemCatalog;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public Collection<PlayerItemEntity> getPlayerItems(
			@RequestParam(value = "steamId64", required = false) Long steamId64,
			@RequestParam(value = "offset", required = false) Integer offset,
			@RequestParam(value = "count", required = false) Integer count,
			Model model) {
		//dotaItemService.retrieveAndStoreItems();
		//playerSummaryService.retrieveAndStorePlayer(steamId64);
		//dotaItemService.retrieveAndStorePlayerItems(steamId64);

		DotaPlayer player = dotaPlayerCatalogDAO.getPlayerBySteamId64(steamId64);
		if(player==null){
			throw new ResourceNotFoundException();
		}
		Iterator<DotaPlayerItem> iter = dotaItemCatalogDAO.getPlayerItems(player, offset, count).iterator();
		ArrayList<PlayerItemEntity> result = new ArrayList<PlayerItemEntity>();

		while(iter.hasNext()){
			DotaPlayerItem item = iter.next();
			DotaItem dotaItem = item.getDotaItem();
			PlayerItemEntity entity = new PlayerItemEntity();
			entity.setName(dotaItem.getFullName());
			entity.setUniqueIndex(item.getUniqueIndex());
			entity.setTypeDisplay(dotaItem.getTypeDisplay());
			entity.setDescription(dotaItem.getDescription());
			entity.setLevel(item.getLevel());
			entity.setCannotTrade(item.getCannotTrade());
			entity.setCannotCraft(item.getCannotCraft());
			entity.setQuality(item.getQuality());
			entity.setQualityDisplay(item.getQualityDisplay());
			entity.setCustomName(item.getCustomName());
			entity.setCustomDescription(item.getCustomDescription());
			entity.setImageId(dotaItem.getImage().getId());
			if(dotaItem.getImage()!=null){
				DotaImage image = dotaItem.getImage().getImage();
				if(image!=null){
					entity.setLargeImageId(image.getId());
				}
			}
			result.add(entity);
		}
		return result;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public PlayerItemEntity getPlayerItemById(
			@RequestParam(value = "id", required = false) Integer id,
			Model model) {

		DotaPlayerItem item = dotaItemCatalogDAO.getPlayerItemById(id);
		if(item==null){
			throw new ResourceNotFoundException();
		}

		DotaItem dotaItem = item.getDotaItem();
		PlayerItemEntity entity = new PlayerItemEntity();
		entity.setName(dotaItem.getFullName());
		entity.setUniqueIndex(dotaItem.getUniqueIndex());
		entity.setTypeDisplay(dotaItem.getTypeDisplay());
		entity.setDescription(dotaItem.getDescription());
		entity.setLevel(item.getLevel());
		entity.setCannotTrade(item.getCannotTrade());
		entity.setCannotCraft(item.getCannotCraft());
		entity.setQuality(item.getQuality());
		entity.setQualityDisplay(item.getQualityDisplay());
		entity.setCustomName(item.getCustomName());
		entity.setCustomDescription(item.getCustomDescription());
		entity.setImageId(dotaItem.getImage().getId());
		if(dotaItem.getImage()!=null){
			DotaImage image = dotaItem.getImage().getImage();
			if(image!=null){
				entity.setLargeImageId(image.getId());
			}
		}

		return entity;
	}

}
