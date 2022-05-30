package in.dota2.model;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;

@Repository("dotaItemCatalogDAO")
public class DotaItemCatalogDAO implements DotaItemCatalogDAOInterface {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	public DotaItemCatalogDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private static final Logger logger = Logger.getLogger(DotaPlayerCatalogDAO.class);

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ArrayList<DotaItem> getItems(Integer offset, Integer count)  throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getItems called");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(
			"select i "+
			"from DotaItem i");

		if(count!=null){
			query.setMaxResults(count);
		}
		if(offset!=null){
			query.setFirstResult(offset);
		}
		return (ArrayList<DotaItem>)query.list();
	}

	@Transactional(readOnly = true)
	public DotaItem getItemById(Integer id) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getItemById called");
		}

		Assert.notNull(id);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaItem i "+
				"where i.id = :id");
		query.setParameter("id", id);
		Object item = query.uniqueResult();
		if(item==null){
			return null;
		}
		return (DotaItem)item;
	}

	@Transactional(readOnly = true)
	public DotaItem getItemByUniqueIndex(Integer uniqueIndex) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getItemByUniqueIndex called");
		}

		Assert.notNull(uniqueIndex);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaItem i "+
				"where i.uniqueIndex = :uniqueIndex");
		query.setParameter("uniqueIndex", uniqueIndex);
		Object item = query.uniqueResult();
		if(item==null){
			return null;
		}
		return (DotaItem)item;
	}

	@Transactional
	public DotaItem saveItem(DotaItem item) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("saveItem called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(item);
		return item;
	}

	@Transactional
	public void deleteItem(DotaItem item) throws DataAccessException {
		sessionFactory.getCurrentSession().delete(item);
	}

	@Transactional
	public void deleteItemAttribute(DotaItemAttribute attribute) throws DataAccessException {
		sessionFactory.getCurrentSession().delete(attribute);
	}

	@Transactional
	public DotaItemAttribute saveItemAttribute(DotaItemAttribute itemAttribute) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("saveItemAttribute called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(itemAttribute);
		return itemAttribute;
	}

	@Transactional(readOnly = true)
	public DotaItemAttribute getItemAttribute(Integer itemUniqueIndex, Integer attributeUniqueIndex){
		if(logger.isDebugEnabled()){
			logger.debug("getItemAttribute called");
		}

		Assert.notNull(itemUniqueIndex);
		Assert.notNull(attributeUniqueIndex);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select a "+
				"from DotaItem i "+
				"join i.attributes a "+
				"where a.uniqueIndex = :attributeUniqueIndex and i.uniqueIndex = :itemUniqueIndex");
		query.setParameter("itemUniqueIndex", itemUniqueIndex);
		query.setParameter("attributeUniqueIndex", attributeUniqueIndex);
		Object attr = query.uniqueResult();
		if(attr==null){
			return null;
		}
		return (DotaItemAttribute)attr;				
	}
	
	@Transactional(readOnly = true)
	public DotaItemCapability getItemCapability(Integer itemId, String name){
		if(logger.isDebugEnabled()){
			logger.debug("getItemCapability called");
		}

		if(itemId==null) return null;

		Assert.notNull(name);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select c "+
				"from DotaItem i "+
				"join i.capabilities c "+
				"where c.name = :name and i.id = :itemId");
		query.setParameter("itemId", itemId);
		query.setParameter("name", name);
		Object capability = query.uniqueResult();
		if(capability==null){
			return null;
		}
		return (DotaItemCapability)capability;		
	}

	@Transactional
	public DotaItemCapability saveItemCapability(DotaItemCapability itemCapability) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("saveItemCapability called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(itemCapability);
		return itemCapability;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ArrayList<DotaPlayerItem> getPlayerItems(DotaPlayer player, Integer offset, Integer count)  throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerItems called");
		}

		Assert.notNull(player);
		Query query = sessionFactory.getCurrentSession().createQuery(
			"select i "+
			"from DotaPlayerItem i "+
			"where i.dotaPlayer = :dotaPlayer");
		query.setParameter("dotaPlayer", player);

		if(count!=null){
			query.setMaxResults(count);
		}
		if(offset!=null){
			query.setFirstResult(offset);
		}
		return (ArrayList<DotaPlayerItem>)query.list();
	}

	@Transactional(readOnly = true)
	public DotaPlayerItem getPlayerItemById(Integer id) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerItemById called");
		}

		Assert.notNull(id);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaPlayerItem i "+
				"where i.id = :id");
		query.setParameter("id", id);
		Object item = query.uniqueResult();
		if(item==null){
			return null;
		}
		return (DotaPlayerItem)item;
	};

	@Transactional(readOnly = true)
	public DotaPlayerItem getPlayerItemByUniqueIndex(DotaPlayer player, Integer uniqueIndex) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerItemByUniqueIndex called");
		}

		Assert.notNull(uniqueIndex);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaPlayerItem i "+
				"where i.uniqueIndex = :uniqueIndex and i.dotaPlayer = :player");
		query.setParameter("uniqueIndex", uniqueIndex);
		query.setParameter("player", player);
		Object item = query.uniqueResult();
		if(item==null){
			return null;
		}
		return (DotaPlayerItem)item;	
	}

	@Transactional(readOnly = true)
	public DotaPlayerItemAttribute getPlayerItemAttribute(DotaPlayerItem playerItem, Integer uniqueIndex) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerItemAttribute called");
		}

		Assert.notNull(playerItem);
		Assert.notNull(uniqueIndex);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaPlayerItemAttribute i "+
				"where i.playerItem = :playerItem and i.uniqueIndex = :uniqueIndex");
		query.setParameter("playerItem", playerItem);
		query.setParameter("uniqueIndex", uniqueIndex);
		Object itemAttribute = query.uniqueResult();
		if(itemAttribute==null){
			return null;
		}
		return (DotaPlayerItemAttribute)itemAttribute;
	}

	@Transactional
	public DotaPlayerItem savePlayerItem(DotaPlayerItem item) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("savePlayerItem called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(item);
		return item;
	}

	@Transactional
	public DotaPlayerItemAttribute savePlayerItemAttribute(DotaPlayerItemAttribute itemAttribute) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("savePlayerItemAttribute called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(itemAttribute);
		return itemAttribute;	
	}

	@Transactional
	public void deletePlayerItemAttribute(DotaPlayerItemAttribute attribute) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("deletePlayerItemAttribute called");
		}
		sessionFactory.getCurrentSession().delete(attribute);
	}

	@Transactional	
	public void deletePlayerItem(DotaPlayerItem item) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("deletePlayerItem called");
		}
		sessionFactory.getCurrentSession().delete(item);		
	}

	@Transactional(readOnly = true)
	public DotaItemQuality getItemQuality(Integer key) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getItemQuality called");
		}

		Assert.notNull(key);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select q "+
				"from DotaItemQuality q "+
				"where q.key = :key");
		query.setParameter("key", key);
		Object itemQuality = query.uniqueResult();
		if(itemQuality==null){
			return null;
		}
		return (DotaItemQuality)itemQuality;		
	}

	@Transactional
	public DotaItemQuality saveItemQuality(DotaItemQuality itemQuality) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("saveItemQuality called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(itemQuality);
		return itemQuality;		
	}

	@Transactional
	public void deleteItemQuality(DotaItemQuality itemQuality) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("deleteItemQuality called");
		}
		sessionFactory.getCurrentSession().delete(itemQuality);		
	}

	@Transactional(readOnly = true)
	public DotaAttribute getAttributeByUniqueIndex(Integer uniqueIndex) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getAttributeByUniqueIndex called");
		}

		Assert.notNull(uniqueIndex);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select a "+
				"from DotaAttribute a "+
				"where a.uniqueIndex = :uniqueIndex");
		query.setParameter("uniqueIndex", uniqueIndex);
		Object itemAttribute = query.uniqueResult();
		if(itemAttribute==null){
			return null;
		}
		return (DotaAttribute)itemAttribute;	
	}

	@Transactional(readOnly = true)
	public DotaAttribute getAttributeByName(String name) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getAttributeByName called");
		}

		Assert.notNull(name);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select a "+
				"from DotaAttribute a "+
				"where a.name = :name");
		query.setParameter("name", name);
		Object itemAttribute = query.uniqueResult();
		if(itemAttribute==null){
			return null;
		}
		return (DotaAttribute)itemAttribute;	
	}

	@Transactional
	public DotaAttribute saveAttribute(DotaAttribute attribute) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("saveAttribute called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(attribute);
		return attribute;
	}

}
