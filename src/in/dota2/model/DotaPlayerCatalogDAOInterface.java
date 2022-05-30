package in.dota2.model;

import java.util.Collection;
import org.springframework.dao.DataAccessException;

public interface DotaPlayerCatalogDAOInterface {

	Collection<DotaPlayer> getPlayers(Integer offset, Integer count) throws DataAccessException;
	DotaPlayer getPlayerById(Integer uid) throws DataAccessException;
	DotaPlayer getPlayerBySteamId64(Long steamId64) throws DataAccessException;
	DotaPlayer getPlayerByUsername(String username) throws DataAccessException;
	DotaPlayer savePlayer(DotaPlayer player) throws DataAccessException;
	void deletePlayer(DotaPlayer player) throws DataAccessException;
}
