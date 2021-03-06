package in.dota2.steam;

import in.dota2.steam.exceptions.Dota2Exception;

/**
 * The GameLeaderboard class represents a single entry in a leaderboard
 *
 */
public class GameLeaderboardEntry {

    protected SteamId steamId;

    protected int score;

    protected int rank;

    protected GameLeaderboard leaderboard;

    /**
     * Creates new entry instance for the given XML data and leaderboard
     *
     * @param entryData The XML data of the leaderboard of the leaderboard
     *        entry
     * @param leaderboard The leaderboard this entry belongs to
     */
    public GameLeaderboardEntry(XMLData entryData, GameLeaderboard leaderboard) {
        try {
            this.steamId     = SteamId.create(entryData.getString("steamid"), false);
        } catch(Dota2Exception e) {}
        this.score       = entryData.getInteger("score");
        this.rank        = entryData.getInteger("rank");
        this.leaderboard = leaderboard;
    }

    /**
     * Returns the Steam ID of this entry's player
     *
     * @return The Steam ID of the player
     */
    public SteamId getSteamId() {
        return this.steamId;
    }

    /**
     * Returns the score of this entry
     *
     * @return The score of this player
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Returns the rank where this entry is listed in the leaderboard
     *
     * @return The rank of this entry
     */
    public int getRank() {
        return this.rank;
    }

    /**
     * Returns the leaderboard this entry belongs to
     *
     * @return The leaderboard of this entry
     */
    public GameLeaderboard getLeaderboard() {
        return this.leaderboard;
    }

}
