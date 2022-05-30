package in.dota2.service;

import in.dota2.model.DotaImageCatalogDAOInterface;
import in.dota2.model.DotaPlayer;
import in.dota2.model.DotaPlayerCatalogDAOInterface;
import in.dota2.model.DotaImage;
import in.dota2.util.ImageResponse;
import in.dota2.util.SpringPropertiesUtil;
import in.dota2.util.SteamHttpRequest;
import in.dota2.util.SteamResponse;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;


public class DotaPlayerSummaryService {

	@Autowired
	private DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO;
	
	@Autowired
	private DotaImageCatalogDAOInterface dotaImageCatalogDAO;

	@Autowired
	private DotaItemService dotaItemService;
	
	protected int steamAPIVersion = 2;

	private static final Logger logger = Logger.getLogger(DotaPlayerSummaryService.class);

	@Async
	public Future<DotaPlayer> retrieveAndStorePlayer(Long steamId64){
		/*
		 * Tries to retrieve Player profile from Steam.
		 * On success saves it in DB. 
		 */
		if(logger.isDebugEnabled()){
			logger.debug("retrieveAndStorePlayer called");
		}

		SteamResponse response = getPlayerSummary(steamId64);

		if(response==null){
			logger.info("NULL");
			return null;
		}

		DotaPlayer player = storeJsonPlayer(response);

		if(player==null){
			logger.info("NULL");
			return null;
		}

		dotaItemService.retrieveAndStorePlayerItems(player.getSteamId64());

		// Retrieve Player's friends now
		response = getFriendsList(steamId64);

		if(response!=null){
			// this will be a list of dictionaries [{ steamId64: friendSince }, ...]
			List<Map.Entry<Long, Date>> friends = friendSteamIdsFromJson(response);
			for(Map.Entry<Long, Date> friend: friends){
				Long friendSteamId64 = friend.getKey();
				// Date friendSince = friend.getValue(); // not used atm

				// save friend as DotaPlayer
				response = getPlayerSummary(friendSteamId64);
				if(response==null) continue;

				DotaPlayer friendPlayer = storeJsonPlayer(response);
				if(friendPlayer==null) continue;

				logger.info(friendPlayer.toString());
				// save friend as many-to-many relation
				player.addFriend(friendPlayer);
				dotaPlayerCatalogDAO.savePlayer(player);
			}
		}

		return new AsyncResult<DotaPlayer>(player);
	}

	private SteamResponse getPlayerSummary(Long steamId64) {
		/*
		 * Performs call to Steam server.
		 */
		if(logger.isDebugEnabled()){
			logger.debug(String.format("getPlayerSummary called with steamId64: %s", steamId64.toString()));
		}

		String key = SpringPropertiesUtil.getProperty("global.steam_api_key");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		params.put("steamids", steamId64.toString());
		params.put("format", "json");

		String url = String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v%04d/?", steamAPIVersion);
		SteamResponse response = null;
		try {
			response = SteamHttpRequest.sendHttpRequest(url, params);
		} catch (Exception e) {
			logger.error(e);
		}

		if(logger.isDebugEnabled()&&response!=null){
			logger.debug(String.format("Response code: %d", response.getResponseCode()));
		}

		if(response.getResponseCode()!=200){
			return null;
		}

		return response;
	}

	private SteamResponse getFriendsList(Long steamId64) {
		/*
		 * Retrieves player's friends list from Steam.
		 */
		if(logger.isDebugEnabled()){
			logger.debug(String.format("getFriendsList called with steamId64: %s", steamId64.toString()));
		}

		String key = SpringPropertiesUtil.getProperty("global.steam_api_key");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		params.put("steamid", steamId64.toString());
		params.put("format", "json");

		SteamResponse response = null;
		try {
			response = SteamHttpRequest.sendHttpRequest("http://api.steampowered.com/ISteamUser/GetFriendList/v1?", params);
		} catch (Exception e) {
			logger.error(e);
		}

		if(logger.isDebugEnabled()&&response!=null){
			logger.debug(String.format("Response code: %d", response.getResponseCode()));
		}

		if(response.getResponseCode()!=200){
			return null;
		}

		return response;
	}

	private DotaPlayer storeJsonPlayer(SteamResponse data){
		/*
		 * De-serializes player record from JSON and saves 
		 * DotaPlayer object.
		 */
		if(logger.isDebugEnabled()){
			logger.debug("storeJsonPlayer called");
		}

		if(data==null){
			logger.info("storeJsonPlayer DATA IS NULL");
			return null;
		}

		DotaPlayer player = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try{
			JsonNode rootNode = objectMapper.readTree(data.response);
			JsonNode response = rootNode.get("response");
			JsonNode record = response.get("players").get(0);

			JsonNode value = record.get("steamid");
			if(value != null) {
				Long steamId64 = Long.parseLong(value.asText());
				player = dotaPlayerCatalogDAO.getPlayerBySteamId64(steamId64);
				if(player == null){
					player = new DotaPlayer();
				}
				player.setSteamId64(steamId64);
				player.setUsername(String.format("http://steamcommunity.com/openid/id/%s", steamId64));
			}

			value = record.get("personaname");
			if(value != null) {
				player.setPersonName(value.asText());
			}

			value = record.get("avatar");
			if(value != null) {
				DotaImage image = player.getImage();
				Integer contentLength = null;
				if(image==null){
					image = new DotaImage();
				} else {
					contentLength = image.getFileSize();
				}

				String imageUrl = value.asText();
				if(imageUrl.equals("")){
					player.setImage(null);
				} else {
					ImageResponse imageResponse = null;
					try {
						imageResponse = SteamHttpRequest.downloadImage(imageUrl, contentLength);
						if(imageResponse!=null){
							dotaImageCatalogDAO.saveImage(image);
							image.write(imageResponse); // the pk required
							dotaImageCatalogDAO.saveImage(image);
							player.setImage(image);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			}

			value = record.get("avatarmedium");
			if(value!=null){
				DotaImage image = player.getImage();
				if(image!=null){
					String mediumImageUrl = value.asText();
					if(!mediumImageUrl.equals("")){
						DotaImage mediumImage = image.getImage();
						Integer contentLength = null;
						if(mediumImage==null){
							mediumImage = new DotaImage();
						} else {
							contentLength = mediumImage.getFileSize();
						}

						ImageResponse imageResponse = null;
						try{
							imageResponse = SteamHttpRequest.downloadImage(mediumImageUrl, contentLength);
							if(imageResponse!=null){
								dotaImageCatalogDAO.saveImage(mediumImage);
								mediumImage.write(imageResponse); // the pk required
								dotaImageCatalogDAO.saveImage(mediumImage);
								image.setImage(mediumImage);
								dotaImageCatalogDAO.saveImage(image);
							}
						} catch(Exception e){
							logger.error(e);
						}
					}
				}
			}

			value = record.get("avatarfull");
			if(value!=null){
				DotaImage image = player.getImage();
				if(image!=null){
					DotaImage mediumImage = image.getImage();
					if(mediumImage!=null){
						String fullImageUrl = value.asText();
						if(!fullImageUrl.equals("")){
							DotaImage fullImage = mediumImage.getImage();
							Integer contentLength = null;
							if(fullImage==null){
								fullImage = new DotaImage();
							} else {
								contentLength = fullImage.getFileSize();
							}

							ImageResponse imageResponse = null;
							try{
								imageResponse = SteamHttpRequest.downloadImage(fullImageUrl, contentLength);
								if(imageResponse!=null){
									dotaImageCatalogDAO.saveImage(fullImage);
									fullImage.write(imageResponse); // the pk required
									dotaImageCatalogDAO.saveImage(fullImage);
									mediumImage.setImage(fullImage);
									dotaImageCatalogDAO.saveImage(mediumImage);
								}
							} catch(Exception e){
								logger.error(e);
							}
						}
					}
				}
			}

			value = record.get("realname");
			if(value != null) player.setRealName(value.asText());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!1");
			System.out.println(player.getRealName());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!1");

			value = record.get("profileurl");
			if(value != null) player.setProfileUrl(value.asText());

			value = record.get("personastate");
			if(value != null) player.setPersonState(value.asText());

			value = record.get("profilestate");
			if(value != null) player.setProfileState(value.asText());

			value = record.get("communityvisibilitystate");
			if(value != null) player.setVisibilityState(value.asText());

			value = record.get("primaryclanid");
			if(value != null) player.setPlayerClanId(value.asText());

			value = record.get("gameid");
			if(value != null) player.setGameId(value.asText());

			value = record.get("loccountrycode");
			if(value != null) player.setCountryCode(value.asText());

			value = record.get("loccityid");
			if(value != null) player.setCityId(value.asText());

			value = record.get("timecreated");
			if(value != null) player.setDateAdded(value.asText());

			value = record.get("lastlogoff");
			if(value != null) player.setLastLogoff(value.asText());

			player.setFetchTimeNow();

		} catch (Exception e){
			logger.info(e.toString());
			e.printStackTrace();
		} finally {
			if(player != null){
				dotaPlayerCatalogDAO.savePlayer(player);
			}
		}
		return player;
	}

	private List<Map.Entry<Long, Date>> friendSteamIdsFromJson(SteamResponse data){
		if(logger.isDebugEnabled()){
			logger.debug("friendSteamIdsFromJson called");
		}

		if(data==null){
			return null;
		}

		List<Map.Entry<Long, Date>> friends = new ArrayList<Map.Entry<Long, Date>>();

		ObjectMapper objectMapper = new ObjectMapper();
		try{
			JsonNode rootNode = objectMapper.readTree(data.response);
			JsonNode response = rootNode.get("friendslist");
			JsonNode jsonFriends = response.get("friends");
			if(jsonFriends.isArray()){
				for (final JsonNode friendNode : jsonFriends) {
					JsonNode friendSteamId64Node = friendNode.get("steamid");
					if(friendSteamId64Node == null) continue;
					Long friendSteamId64 = Long.parseLong(friendSteamId64Node.asText());

					JsonNode friendSinceNode = friendNode.get("friend_since");
					Date friendSince = new Date();
					if(friendSinceNode != null) friendSince = new Date(Long.parseLong(friendSinceNode.asText()));

					Map.Entry<Long, Date> friend = new AbstractMap.SimpleEntry<Long, Date>(friendSteamId64, friendSince);
					friends.add(friend);
				}
			}
		} catch (Exception e){
			logger.info(e);
			e.printStackTrace();
		}

		return friends;
	}

	/* 
	 * Retrieves the history of bans
	 */
	private HashMap<String, Object> getPlayerBans(Long steamId64){
		if(logger.isDebugEnabled()){
			logger.debug("getPlayerBans called");
		}

		String key = SpringPropertiesUtil.getProperty("global.steam_api_key");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		params.put("steamids", steamId64.toString());
		params.put("format", "json");

		String url = "http://api.steampowered.com/ISteamUser/GetPlayerBans/v1?";
		SteamResponse response = null;
		try {
			response = SteamHttpRequest.sendHttpRequest(url, params);
		} catch (Exception e) {
			logger.error(e);
		}

		if(logger.isDebugEnabled()&&response!=null){
			logger.debug(String.format("Response code: %d", response.getResponseCode()));
		}

		if(response.getResponseCode()!=200){
			return null;
		}

		HashMap<String, Object> result = new HashMap<String,Object>();
		ObjectMapper objectMapper = new ObjectMapper();
		try{
			JsonNode rootNode = objectMapper.readTree(response.response);
			JsonNode playersNode = rootNode.get("players");
			Iterator<JsonNode> players = playersNode.getElements();
			if(players==null){
				if (logger.isDebugEnabled()) {
					logger.debug("No player records arrived in response.");
				}
				return null; // no point to continue
			}
			while(players.hasNext()){
				JsonNode player = players.next();
				Long playerSteamId64 = player.get("SteamId").asLong();
				if(playerSteamId64==steamId64){
					Boolean communityBanned = player.get("CommunityBanned").asBoolean();
					Boolean VACBanned = player.get("VACBanned").asBoolean();
					Integer numberOfVACBans = player.get("NumberOfVACBans").asInt();
					Integer daysSinceLastBan = player.get("DaysSinceLastBan").asInt();
					result.put("communityBanned", communityBanned);
					result.put("VACBanned", VACBanned);
					result.put("numberOfVACBans", numberOfVACBans);
					result.put("daysSinceLastBan", daysSinceLastBan);
				}
			}
		} catch (Exception e){
			logger.info(e);
			e.printStackTrace();
		}
		return result;
	}
}