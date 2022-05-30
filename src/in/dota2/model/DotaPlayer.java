package in.dota2.model;

import in.dota2.entity.BaseEntity;
import in.dota2.steam.exceptions.Dota2Exception;
//import in.dota2.steam.SteamGroup;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.neovisionaries.i18n.CountryCode;

/**
 * Simple JavaBean domain object representing an player.
 * 
 */
public class DotaPlayer extends BaseEntity {

	protected Long steamId64;
	private String username;
	private String personName;
	private String realName;
	private String profileUrl;
	private Integer personState; // The user's status
	private Integer visibilityState; // Describes the access setting of the profile
	private Boolean profileState; // If set to 1 the user has configured the profile.
	private Long playerClanId;
	private String gameId; // If the user is currently in-game, this value will
							// be returned and set to the gameid of that game.
	private String gameInfo; // If the user is currently in-game, this will be
								// the name of the game they are playing. This
								// may be the name of a non-Steam game shortcut.
	private String countryCode; // If set on the user's Steam Community profile,
								// The user's country of residence, 2-character
								// ISO country code
	private String cityId;
	//private SteamGroup[] groups;
	private Date lastLogoff; //  when the user was last online
	private Date dateAdded;
	private Date fetchTime;
	private Set<DotaPlayer> friends = new HashSet<DotaPlayer>();
	private Set<DotaPlayerItem> items = new HashSet<DotaPlayerItem>();
	private DotaImage image;

	public DotaPlayer(){} // default constructor for Hibernate

	public long getSteamId64() {
		return this.steamId64;
	}

	public void setSteamId64(long steamId64) {
		this.steamId64 = steamId64;
	}

	public void setSteamId64(String steamId64) {
		this.steamId64 = Long.parseLong(steamId64);
	}

	public String getPersonName() {
		return this.personName;
	}
	
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getProfileUrl() {
		return this.profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public Integer getPersonState() {
		return this.personState;
	}

	public String getPersonStateDisplay() {
		String stateDisplay = "";
		switch (this.personState) {
			case 0: stateDisplay = "Offline"; break;
			case 1: stateDisplay = "Online"; break;
			case 2: stateDisplay = "Busy"; break;
			case 3: stateDisplay = "Away"; break;
			case 4: stateDisplay = "Snooze"; break;
			case 5: stateDisplay = "Looking to trade"; break;
			case 6: stateDisplay = "Looking to play"; break;
		}
		return stateDisplay;
	}

	public void setPersonState(Integer personState) {
		this.personState = personState;
	}

	public void setPersonState(String personState) {
		this.personState = Integer.parseInt(personState);
	}

	public Integer getVisibilityState() {
		return this.visibilityState;
	}

	public String getVisibilityStateDisplay() {
		String visibilityStateDisplay = "";
		switch (this.visibilityState) {
			case 1: visibilityStateDisplay = "Private"; break;
			case 2: visibilityStateDisplay = "Friends Only"; break;
			case 3: visibilityStateDisplay = "Friends Of Friends"; break;
			case 4: visibilityStateDisplay = "Users Only"; break;
			case 5: visibilityStateDisplay = "Public"; break;
		}
		return visibilityStateDisplay;
	}
	
	public Boolean getProfileState(){
		return this.profileState;
	};

	public void setProfileState(Boolean state){
		this.profileState = state;
	}

	public void setProfileState(String state){
		if(state.equals("1")){
			this.profileState = true;
		} else {
			this.profileState = false;
		}
	}

	public void setVisibilityState(Integer visibilityState) {
		this.visibilityState = visibilityState;
	}

	public void setVisibilityState(String visibilityState) {
		this.visibilityState = Integer.parseInt(visibilityState);
	}

	public Long getPlayerClanId() {
		return this.playerClanId;
	}

	public void setPlayerClanId(Long playerClanId) {
		this.playerClanId = playerClanId;
	}

	public void setPlayerClanId(String playerClanId) {
		this.playerClanId = Long.parseLong(playerClanId);
	}

	public String getGameId() {
		return this.gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameInfo() {
		return this.gameInfo;
	}

	public void setGameInfo(String gameInfo) {
		this.gameInfo = gameInfo;
	}

	public String getCountryCode() {
		return this.countryCode;
	}

	public String getCountryName() {
		CountryCode cc = CountryCode.getByCode(this.countryCode);
		return cc.getName();
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCityId() {
		return this.cityId;
	}
	
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	/**
	 * Converts a 64bit numeric SteamID as used by the Steam Community to a
	 * SteamID as reported by game servers
	 * 
	 * @param communityId
	 *            The SteamID string as used by the Steam Community
	 * @return The converted SteamID, like <code>STEAM_0:0:12345</code>
	 * @throws Dota2Exception
	 *             if the community ID is to small
	 */
	public static String convertCommunityIdToSteamId(long communityId)
			throws Dota2Exception {
		long steamId1 = communityId % 2;
		long steamId2 = communityId - 76561197960265728L;

		if (steamId2 <= 0) {
			throw new Dota2Exception("SteamID " + communityId
					+ " is too small.");
		}

		steamId2 = (steamId2 - steamId1) / 2;

		return "STEAM_0:" + steamId1 + ":" + steamId2;
	}

	/**
	 * Converts a SteamID as reported by game servers to a 64bit numeric SteamID
	 * as used by the Steam Community
	 * 
	 * @param steamId
	 *            The SteamID string as used on servers, like
	 *            <code>STEAM_0:0:12345</code>
	 * @return The converted 64bit numeric SteamID
	 * @throws Dota2Exception
	 *             if the SteamID doesn't have the correct format
	 */
	public static long convertSteamIdToCommunityId(String steamId)
			throws Dota2Exception {
		if (steamId.equals("STEAM_ID_LAN") || steamId.equals("BOT")) {
			throw new Dota2Exception("Cannot convert SteamID \"" + steamId
					+ "\" to a community ID.");
		}
		if (steamId.matches("^STEAM_[0-1]:[0-1]:[0-9]+$")) {
			String[] tmpId = steamId.substring(8).split(":");
			return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1]) * 2
					+ 76561197960265728L;
		} else if (steamId.matches("^\\[U:[0-1]:[0-9]+\\]+$")) {
			String[] tmpId = steamId.substring(3, steamId.length() - 1).split(
					":");
			return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1])
					+ 76561197960265727L;
		} else {
			throw new Dota2Exception("SteamID \"" + steamId
					+ "\" doesn't have the correct format.");
		}
	}

	/**
	 * Returns the base URL for this Steam ID
	 * <p>
	 * This URL is different for Steam IDs having a custom URL.
	 * 
	 * @return The base URL for this SteamID
	 */
	public String getBaseUrl() {
		if (this.profileUrl == null) {
			return "http://steamcommunity.com/profiles/" + this.steamId64;
		} else {
			return "http://steamcommunity.com/id/" + this.profileUrl;
		}
	}

	/**
	 * Returns the time this group has been fetched
	 * 
	 * @return The timestamp of the last fetch time
	 */
	public Date getFetchTime() {
		return this.fetchTime;
	}

	public void setFetchTime(Date fetchTime) {
		this.fetchTime = fetchTime;
	}

	public void setFetchTimeNow() {
		this.fetchTime = new Date();
	}

	public Date getDateAdded() {
		return this.dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public void setDateAdded(String timestamp) {
		this.dateAdded = new Date(Long.parseLong(timestamp));
	}

	public Date getLastLogoff() {
		return this.lastLogoff;
	}

	public void setLastLogoff(Date lastLogoff) {
		this.lastLogoff = lastLogoff;
	}

	public void setLastLogoff(String timestamp) {
		this.lastLogoff = new Date(Long.parseLong(timestamp));
	}

    public Set<DotaPlayer> getFriends() {
        return friends;
    }

    public void setFriends(Set<DotaPlayer> friends) {
        this.friends = friends;
    }

    public void addFriend(DotaPlayer friend){
        if(friends == null)
            friends = new HashSet<DotaPlayer>();
        friends.add(friend);
    }

    public Set<DotaPlayerItem> getItems() {
        return items;
    }

    public void setItems(Set<DotaPlayerItem> items) {
        this.items = items;
    }

    public void addItem(DotaPlayerItem item){
        if(items == null)
            items = new HashSet<DotaPlayerItem>();
        items.add(item);
    }

    public DotaPlayerItem getAttribute(Integer uniqueIndex) {
    	Iterator<DotaPlayerItem> iter = items.iterator();
    	while(iter.hasNext()){
    		DotaPlayerItem item = iter.next();
    		if(item.getUniqueIndex() == uniqueIndex) return item;
    	}
    	return null;
    }

    public DotaImage getImage(){
    	return this.image;
    }

    public void setImage(DotaImage image){
    	this.image = image;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("SteamId64: ").append(this.steamId64).append("; ");
		sb.append("Person Name:").append((this.personName!=null?this.personName:"Null")).append("; ");
		sb.append("Real Name:").append((this.realName!=null?this.realName:"Null")).append("; ");
		sb.append("Profile Url:").append((this.profileUrl!=null?this.profileUrl:"Null")).append("; ");
		sb.append("Person State:").append((this.personState!=null?this.personState:"Null")).append("; ");
		sb.append("Visibility State:").append(getVisibilityStateDisplay()).append("; ");
		sb.append("Profile State:").append(getProfileState()).append("; ");
		sb.append("Player Clan Id:").append(this.playerClanId).append("; ");
		sb.append("Game Id:").append(this.gameId).append("; ");
		sb.append("Fetch Time:").append(this.fetchTime.toString()).append("; ");
		
		return sb.toString();
	}

    
}
