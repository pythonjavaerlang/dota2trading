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


@Repository("dotaPlayerCatalogDAO")
public class DotaPlayerCatalogDAO implements DotaPlayerCatalogDAOInterface {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	public DotaPlayerCatalogDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private static final Logger logger = Logger.getLogger(DotaPlayerCatalogDAO.class);

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ArrayList<DotaPlayer> getPlayers(Integer offset, Integer count)  throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayers called");
		}

		Query query = sessionFactory.getCurrentSession().createQuery(
			"select p "+
			"from DotaPlayer p");

		if(count!=null){
			query.setMaxResults(count);
		}
		if(offset!=null){
			query.setFirstResult(offset);
		}
		return (ArrayList<DotaPlayer>)query.list();
	}

	@Transactional(readOnly = true)
	public DotaPlayer getPlayerBySteamId64(Long steamId64)  throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerBySteamId64 called");
		}

		Assert.notNull(steamId64);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select p "+
				"from DotaPlayer p "+
				"where p.steamId64 = :steamId64");
		query.setParameter("steamId64", steamId64);
		Object player = query.uniqueResult();
		if(player==null){
			return null;
		}
		return (DotaPlayer)player;
	}

	@Transactional(readOnly = true)
	public DotaPlayer getPlayerById(Integer uid) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerById called");
		}
		Assert.notNull(uid);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select p "+
				"from DotaPlayer p "+
				"where p.id = :id");
		query.setParameter("id", uid);
		Object player = query.uniqueResult();
		if(player==null){
			return null;
		}
		return (DotaPlayer)player;
	}

	@Transactional(readOnly = true)
	public DotaPlayer getPlayerByUsername(String username)  throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerByUsername called");
		}
		Assert.notNull(username);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select p "+
				"from DotaPlayer p "+
				"where p.username = :username");
		query.setParameter("username", username);
		Object player = query.uniqueResult();
		if(player==null){
			return null;
		}
		return (DotaPlayer)player;
	}

	@Transactional
	public DotaPlayer savePlayer(DotaPlayer player) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("savePlayer called");
		}
		Assert.notNull(player.getSteamId64());
		Assert.notNull(player.getUsername());
		Assert.notNull(player.getPersonName());
		Assert.notNull(player.getProfileUrl());
		sessionFactory.getCurrentSession().saveOrUpdate(player);
		return player;
	}

	public void deletePlayer(DotaPlayer player) throws DataAccessException {
		if(logger.isDebugEnabled()){
			logger.debug("deletePlayer called");
		}
		sessionFactory.getCurrentSession().delete(player);
	}

}
