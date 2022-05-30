package in.dota2.controller;

import in.dota2.model.DotaPlayer;
import in.dota2.model.DotaPlayerCatalogDAOInterface;
import in.dota2.model.DotaUserCatalogDAOInterface;
import in.dota2.util.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class IndexController {

	@Autowired
	private final DotaUserCatalogDAOInterface dotaUserCatalog;
	
	@Autowired
	private DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO;

	@Autowired
	public IndexController(DotaUserCatalogDAOInterface dotaUserCatalog) {
		this.dotaUserCatalog = dotaUserCatalog;
	}

	@Value("${global.root_path}")
	private String rootPath;

	@Value("${global.media_url}")
	private String mediaUrl;

	@RequestMapping("/")
	public String index(Model model) {
		setAuthAttributes(model);
		model.addAttribute("rootPath", rootPath);
		return "index";
	}

	@RequestMapping("/profile")
	public String profile(@RequestParam(value = "steamId64", required = false) Long steamId64,
			Model model) {
		if(steamId64==null){
			throw new ResourceNotFoundException();
		}
		model.addAttribute("steamId64", steamId64.toString());
		setAuthAttributes(model);
		DotaPlayer player = dotaPlayerCatalogDAO.getPlayerBySteamId64(steamId64);
		if(player!=null) {
			model.addAttribute("player", player);
			model.addAttribute("personStateDisplay", player.getPersonStateDisplay());
			model.addAttribute("visibilityStateDisplay", player.getVisibilityStateDisplay());
			model.addAttribute("profileState", player.getProfileState());
			model.addAttribute("countryName", player.getCountryName());
			model.addAttribute("realName", player.getRealName());
		} else {
			model.addAttribute("profileState", false);
			model.addAttribute("realName", "Anonymous");
			model.addAttribute("personStateDisplay", "Snooze");
		}
		model.addAttribute("rootPath", rootPath);
		return "player_profile";
	}

	@RequestMapping("/users/signin")
	public String signin(Model model) {
		return "signin";		
	}

	@RequestMapping("/users/invalid-session")
	public String invalidSession(Model model) {
		return "redirect:/";		
	}

	private void setAuthAttributes(Model model){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Boolean isAuthenticated = false;

		String username = "anonymousUser";
		DotaPlayer player = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
			isAuthenticated = true;
			player = dotaPlayerCatalogDAO.getPlayerByUsername(username);
			if(player != null) {
				// it could be that async task for retrieving player 
				// from Steam server did not get in time to finish yet
				String name = player.getRealName();
				if(name != null) {
					username = name;
				} else {
					name = player.getPersonName();
				}
				username = name;
			}
		} else {
			username = principal.toString();
		}

		model.addAttribute("isAuthenticated", isAuthenticated);
		model.addAttribute("username", username);
	}
}
