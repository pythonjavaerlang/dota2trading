package in.dota2.steam;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.dota2.steam.exceptions.Dota2Exception;

/**
 * This class represents the game statistics for a single user and a specific
 * game
 * <p>
 * It is subclassed for individual games if the games provide special
 * statistics that are unique to this game.
 *
 * @author Sebastian Staudt
 */
public class GameStats {

    protected ArrayList<GameAchievement> achievements;

    protected int achievementsDone;

    protected SteamGame game;

    protected String hoursPlayed;

    protected String privacyState;

    protected SteamId user;

    protected XMLData xmlData;

    /**
     * Creates a <code>GameStats</code> (or one of its subclasses) instance for
     * the given user and game
     *
     * @param steamId The custom URL or the 64bit Steam ID of the user
     * @param gameName The friendly name of the game
     * @return The game stats object for the given user and game
     * @throws Dota2Exception if an error occurs while parsing the
     *         data
     */
    public static GameStats createGameStats(Object steamId, String gameName)
            throws Dota2Exception {
    	/*
    	 * TODO: To implement Dota 2 stats, if any available
    	 */
            return new GameStats(steamId, gameName);
    }

    /**
     * Returns the base Steam Communtiy URL for the given player and game IDs
     *
     * @param userId The 64bit SteamID or custom URL of the user
     * @param gameId The application ID or short name of the game
     * @return The base URL used for the given stats IDs
     */
    protected static String getBaseUrl(Object userId, Object gameId) {
        String gameUrl;
        if(gameId instanceof Integer) {
            gameUrl = "appid/" + gameId;
        } else {
            gameUrl = (String) gameId;
        }

        if(userId instanceof Long) {
            return "http://steamcommunity.com/profiles/" + userId + "/stats/" + gameUrl;
        } else {
            return "http://steamcommunity.com/id/" + userId + "/stats/" + gameUrl;
        }
    }

    /**
     * Creates a <code>GameStats</code> object and fetches data from the Steam
     * Community for the given user and game
     *
     * @param steamId The custom URL or the 64bit Steam ID of the user
     * @param gameId The app ID or friendly name of the game
     * @throws Dota2Exception if the stats cannot be fetched
     */
    protected GameStats(Object steamId, Object gameId)
            throws Dota2Exception {
        if(steamId instanceof String) {
            this.user = SteamId.create((String) steamId, false);
        } else if(steamId instanceof Long) {
            this.user = SteamId.create((Long) steamId, false);
        }

        try {
            this.xmlData = new XMLData(getBaseUrl(steamId, gameId) + "?xml=all");

            if(this.xmlData.hasElement("error")) {
                throw new Dota2Exception(this.xmlData.getString("error"));
            }

            this.privacyState = this.xmlData.getString("privacyState");
            if(this.isPublic()) {
                Pattern appIdPattern = Pattern.compile("http://steamcommunity\\.com/+app/+([1-9][0-9]*)", Pattern.CASE_INSENSITIVE);
                Matcher appIdMatcher = appIdPattern.matcher(this.xmlData.getString("game", "gameLink"));
                appIdMatcher.find();
                int appId = Integer.parseInt(appIdMatcher.group(1));
                this.game = SteamGame.create(appId, this.xmlData.getElement("game"));
                this.hoursPlayed = this.xmlData.getString("stats", "hoursPlayed");
            }
        } catch(Exception e) {
            throw new Dota2Exception("XML data could not be parsed.", e);
        }
    }

    /**
     * Returns the achievements for this stats' user and game
     * <p>
     * If the achievements' data hasn't been parsed yet, parsing is done now.
     *
     * @return All achievements belonging to this game
     */
    public ArrayList<GameAchievement> getAchievements() {
        if(this.achievements == null) {
            this.achievements = new ArrayList<GameAchievement>();
            this.achievementsDone = 0;

            for(XMLData achievementData : this.xmlData.getElements("achievements", "achievement")) {
                GameAchievement achievement = new GameAchievement(this.user, this.game, achievementData);
                if(achievement.isUnlocked()) {
                    this.achievementsDone += 1;
                }
                this.achievements.add(achievement);
            }
        }

        return achievements;
    }

    /**
     * Returns the number of achievements done by this player
     * <p>
     * If achievements haven't been parsed yet for this player and this game,
     * parsing is done now.
     *
     * @return The number of achievements completed
     * @see #getAchievements
     */
    public int getAchievementsDone() {
        if(this.achievements == null) {
            this.getAchievements();
        }

        return this.achievementsDone;
    }

    /**
     * Returns the percentage of achievements done by this player
     * <p>
     * If achievements haven't been parsed yet for this player and this game,
     * parsing is done now.
     *
     * @return The percentage of achievements completed
     * @see #getAchievementsDone
     */
    public float getAchievementsPercentage() {
        return (float) this.getAchievementsDone() / this.achievements.size();
    }

    /**
     * Returns the base Steam Communtiy URL for the stats contained in this
     * object
     *
     * @return The base URL used for queries on these stats
     */
    public String getBaseUrl() {
        return getBaseUrl(this.user.getId(), this.game.getId());
    }

    /**
     * Returns the game these stats belong to
     *
     * @return The game object
     */
    public SteamGame getGame() {
        return this.game;
    }

    /**
     * Returns the privacy setting of the Steam ID profile
     *
     * @return The privacy setting of the Steam ID
     */
    public String getPrivacyState() {
        return this.privacyState;
    }

    /**
     * Returns the number of hours this game has been played by the player
     *
     * @return The number of hours this game has been played
     */
    public String getHoursPlayed() {
        return this.hoursPlayed;
    }

    /**
     * Returns the Steam ID of the player these stats belong to
     *
     * @return The Steam ID instance of the player
     */
    public SteamId getUser() {
        return this.user;
    }

    /**
     * Returns whether this Steam ID is publicly accessible
     *
     * @return <code>true</code> if this Steam ID is publicly accessible
     */
    protected boolean isPublic() {
        return this.privacyState.equals("public");
    }
}
