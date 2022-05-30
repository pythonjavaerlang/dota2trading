package in.dota2.model;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class DotaImageCatalogDAO implements DotaImageCatalogDAOInterface {

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger logger = Logger.getLogger(DotaPlayerCatalogDAO.class);

	@Autowired
	public DotaImageCatalogDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public DotaImage getImageById(Integer imageId) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getImageById called");
		}

		Assert.notNull(imageId);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaImage i "+
				"where i.id = :id");
		query.setParameter("id", imageId);
		Object image = query.uniqueResult();
		if(image==null){
			return null;
		}
		return (DotaImage)image;
	}

	@Transactional(readOnly = true)
	public DotaImage getImageBySha1(String sha1) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("getImageBySha1 called");
		}

		Assert.notNull(sha1);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select i "+
				"from DotaImage i "+
				"where i.sha1 = :sha1");
		query.setParameter("sha1", sha1);
		Object image = query.uniqueResult();
		if(image==null){
			return null;
		}
		return (DotaImage)image;
	}

	@Transactional
	public DotaImage saveImage(DotaImage dotaImage) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("saveImage called");
		}
		sessionFactory.getCurrentSession().saveOrUpdate(dotaImage);
		return dotaImage;		
	}

	@Transactional
	public void deleteImage(DotaImage dotaImage) throws DataAccessException{
		if(logger.isDebugEnabled()){
			logger.debug("dotaImage called");
		}
		sessionFactory.getCurrentSession().delete(dotaImage);
	}
	
}