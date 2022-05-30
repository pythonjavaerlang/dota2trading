package in.dota2.model;

import java.util.Collection;
import org.springframework.dao.DataAccessException;

public interface DotaUserCatalogDAOInterface {

	Collection<DotaUser> getUsers(Integer offset, Integer count) throws DataAccessException;
	DotaUser getUserByUsername(String username) throws DataAccessException;
	DotaUser getUserById(Integer uid) throws DataAccessException;
	DotaUser getOrCreateByUsername(String username) throws DataAccessException;
	void storeUser(DotaUser user) throws DataAccessException;
	void deleteUser(String username) throws DataAccessException;
}
