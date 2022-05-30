package in.dota2.service;

import in.dota2.model.DotaUser;
import in.dota2.model.DotaUserCatalogDAO;
import in.dota2.model.DotaUserCatalogDAOInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("dotaUserService")
public class DotaUserService implements UserDetailsService, AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

	@Autowired
	private DotaUserCatalogDAOInterface dotaUserCatalogDAO;

	//@Autowired
	public void setDotaUserCatalogDAO(DotaUserCatalogDAO dotaUserCatalogDAO){
		this.dotaUserCatalogDAO = dotaUserCatalogDAO;
	}

	//@Autowired
	//public DotaUserService(DotaUserCatalogDAO localUserCatalog){
	//	this.localUserCatalog = localUserCatalog;
	//}

	public DotaUserCatalogDAOInterface getDotaUserCatalogDAO() {
		return dotaUserCatalogDAO;
	}

	public DotaUser loadUserDetails(OpenIDAuthenticationToken token) {

		String username = getClaimedId(token);
		return loadUserByUsername(username);
	}

	@Transactional(readOnly = true)
	public DotaUser loadUserByUsername(String username) {

		return dotaUserCatalogDAO.getOrCreateByUsername(username);

	}

	private String getClaimedId(OpenIDAuthenticationToken token) {

		// Steam does not respond with correct openid response
		// See https://code.google.com/p/openid4java/issues/detail?id=192
		// So I just read SteamID and Create/Get user by that id

		String identityUrl = token.getIdentityUrl().toString();
		return identityUrl;
	}
}
