package in.dota2.model;

import java.util.Collection;

import org.springframework.dao.DataAccessException;


public interface DotaItemCatalogDAOInterface {

	Collection<DotaItem> getItems(Integer offset, Integer count) throws DataAccessException;
	DotaItem getItemById(Integer uid) throws DataAccessException;
	DotaItem getItemByUniqueIndex(Integer uniqueIndex) throws DataAccessException;
	DotaItem saveItem(DotaItem item) throws DataAccessException;

	Collection<DotaPlayerItem> getPlayerItems(DotaPlayer player, Integer offset, Integer count) throws DataAccessException;
	DotaPlayerItem getPlayerItemById(Integer id) throws DataAccessException;
	DotaPlayerItem getPlayerItemByUniqueIndex(DotaPlayer player, Integer uniqueIndex) throws DataAccessException;
	DotaPlayerItem savePlayerItem(DotaPlayerItem item) throws DataAccessException;

	DotaItemCapability getItemCapability(Integer itemId, String name) throws DataAccessException;
	DotaItemCapability saveItemCapability(DotaItemCapability itemCapability) throws DataAccessException;
	DotaItemAttribute getItemAttribute(Integer itemId, Integer uniqueIndex) throws DataAccessException;
	DotaItemAttribute saveItemAttribute(DotaItemAttribute itemAttribute) throws DataAccessException;
	void deleteItem(DotaItem item) throws DataAccessException;
	void deleteItemAttribute(DotaItemAttribute attribute) throws DataAccessException;
	DotaPlayerItemAttribute getPlayerItemAttribute(DotaPlayerItem playerItem, Integer uniqueIndex) throws DataAccessException;
	DotaPlayerItemAttribute savePlayerItemAttribute(DotaPlayerItemAttribute itemAttribute) throws DataAccessException;
	void deletePlayerItem(DotaPlayerItem item) throws DataAccessException;
	void deletePlayerItemAttribute(DotaPlayerItemAttribute attribute) throws DataAccessException;

	DotaItemQuality getItemQuality(Integer key) throws DataAccessException;
	DotaItemQuality saveItemQuality(DotaItemQuality itemQuality) throws DataAccessException;
	void deleteItemQuality(DotaItemQuality itemQuality) throws DataAccessException;

	DotaAttribute getAttributeByUniqueIndex(Integer uniqueIndex) throws DataAccessException;
	DotaAttribute getAttributeByName(String name) throws DataAccessException;
	DotaAttribute saveAttribute(DotaAttribute item) throws DataAccessException;

}
