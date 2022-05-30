package in.dota2.model;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;

@Repository("dotaUserCatalogDAO")
public class DotaUserCatalogDAO implements DotaUserCatalogDAOInterface {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	public DotaUserCatalogDAO(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ArrayList<DotaUser> getUsers(Integer offset, Integer count) {
		//ArrayList<DotaUser> result = new ArrayList<DotaUser>();

		Query query = sessionFactory.getCurrentSession().createQuery(
			"select u "+
			"from DotaUser u");

		if(count!=null){
			query.setMaxResults(count);
		}
		if(offset!=null){
			query.setFirstResult(offset);
		}
		return (ArrayList<DotaUser>)query.list();
		/*
		List<Object[]> rows = query.list();

		for (Object[] row : rows) {
			Integer uid = (Integer)row[0]; 
			String username = (String) row[1];
			Boolean enabled = (Boolean)row[2];
			Boolean accountNonLocked = !(Boolean) row[3];
			Boolean accountNonExpired = !(Boolean) row[4];
			Date lastLogin = (Date)row[5];
			Date dateJoined = (Date)row[6];

			List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

		    DotaUser user = new DotaUser(uid, username, "", enabled, accountNonExpired, 
		    		true, accountNonLocked, authorities, lastLogin, dateJoined);
		    result.add(user);
		}
		return result;
		*/
	}

	@Transactional(readOnly = true)
	public DotaUser getUserByUsername(String username) {
		Assert.notNull(username);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select u "+
				"from DotaUser u "+
				"where u.username = :username");
		query.setParameter("username", username);
		Object user = query.uniqueResult();
		if(user==null){
			return null;
		}
		return (DotaUser)user;
	}

	@Transactional(readOnly = true)
	public DotaUser getUserById(Integer uid) {
		Assert.notNull(uid);
		Query query = sessionFactory.getCurrentSession().createQuery(
				"select u "+
				"from DotaUser u "+
				"where u.id = :id");
		query.setParameter("id", uid);
		Object user = query.uniqueResult();
		if(user==null){
			return null;
		}
		return (DotaUser)user;
	}

	@Transactional
	public DotaUser getOrCreateByUsername(String username){
		Assert.notNull(username);
		DotaUser user = getUserByUsername(username);
		if(user==null){
			Date lastLogin = new Date();
			Date dateJoined = new Date();
			user = new DotaUser(username, lastLogin, dateJoined);
		}
		user.setLastLogin(new Date());
		storeUser(user);
		return user;
	}
	
	@Transactional
	public void storeUser(DotaUser user) throws DataAccessException {
		sessionFactory.getCurrentSession().save(user);
	};

	public void deleteUser(String username) throws DataAccessException {
		
	};
}
