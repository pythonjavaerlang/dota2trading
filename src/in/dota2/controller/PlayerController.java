package in.dota2.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.dota2.model.DotaPlayerCatalogDAOInterface;
import in.dota2.model.DotaPlayer;


@Controller
@RequestMapping("/api/player")
public class PlayerController {

	private final DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO;

	@Autowired
	public PlayerController(DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO) {
		this.dotaPlayerCatalogDAO = dotaPlayerCatalogDAO;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public Collection<DotaPlayer> getPlayers(@RequestParam(value="offset", required=false) Integer offset,
			@RequestParam(value="count", required=false) Integer count, ModelMap model) {

		return this.dotaPlayerCatalogDAO.getPlayers(offset, count);
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public DotaPlayer getlayer(@PathVariable("id") Integer playerId, ModelMap model) {
		return this.dotaPlayerCatalogDAO.getPlayerById(playerId);
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	@Transactional
	public Collection<DotaPlayer> test(@RequestParam(value = "task-id", required = false) String task_id,
			ModelMap model,
			HttpServletRequest request){
/*		WebAPIRequestService requestService = new WebAPIRequestService();

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "38E18104520CAABF988757C031D77E97");
		params.put("vanityurl", "dota2in");

		HttpSession session = request.getSession();

		if(task_id != null && task_id.length()==41){
			StringBuilder full_task_id = new StringBuilder(41);
			full_task_id.append("task-");
			full_task_id.append(task_id);
			Future<WebAPIResult> result = (Future<WebAPIResult>)session.getAttribute(full_task_id.toString());

		} else {
			try {
				//Future<String> response = requestService.sendHttpRequest("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?", params);
				Future<WebAPIResult> response = requestService.sendHttpRequest("http://192.168.110.3:8000/", params);
				UUID uuid = UUID.randomUUID();
				StringBuilder task_uuid = new StringBuilder(41);
				task_uuid.append("task-");
				task_uuid.append(uuid.toString());
				session.setAttribute(task_uuid.toString(), response);
			} catch (IOException e) {
				e.printStackTrace(); 
			}

		}
		*/
		return this.dotaPlayerCatalogDAO.getPlayers(0, 1);
	}
	/*
	public static UserDetails currentUserDetails(){
	    SecurityContext securityContext = SecurityContextHolder.getContext();
	    Authentication authentication = securityContext.getAuthentication();
	    if (authentication != null) {
	        Object principal = authentication.getPrincipal();
	        return (UserDetails) (principal instanceof UserDetails ? principal : null);
	    }
	    return null;
	}
	 */
}
