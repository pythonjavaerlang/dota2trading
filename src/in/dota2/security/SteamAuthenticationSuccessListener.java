package in.dota2.security;
import in.dota2.model.DotaUser;
import in.dota2.service.DotaPlayerSummaryService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;

class SteamAuthenticationSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	@Autowired
	private DotaPlayerSummaryService playerSummaryService;
	
	private static final Logger logger = Logger.getLogger(SteamAuthenticationSuccessListener.class);

	@Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		DotaUser user;
		Long steamId64 = null;
		if (principal instanceof UserDetails) {
			// Check just in case
			user = ((DotaUser) principal);
			steamId64 = user.getSteamIdFromUsername();
			if(logger.isDebugEnabled()){
				logger.info(String.format("STEAMID OF AUTHENTICATED USER: %s", steamId64));
			}
			if(steamId64 != null){
				playerSummaryService.retrieveAndStorePlayer(steamId64);
			}
		}
		if(logger.isDebugEnabled()){
			logger.debug(String.format("User with SteamId %s logged in", (steamId64==null?"UNKNOWN":steamId64)));
		}
    }

}