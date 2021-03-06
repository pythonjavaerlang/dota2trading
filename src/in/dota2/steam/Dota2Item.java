package in.dota2.steam;

import org.json.JSONObject;

import in.dota2.steam.exceptions.Dota2Exception;
import in.dota2.steam.exceptions.WebApiException;
import in.dota2.steam.GameItem;

/**
 * Represents a DotA 2 item
 *
 */
public class Dota2Item extends GameItem {

    private boolean equipped;

    /**
     * Creates a new instance of a Dota2Item with the given data
     *
     * @param inventory The inventory this item is contained in
     * @param itemData The data specifying this item
     * @throws WebApiException on Web API errors
     */
    public Dota2Item(Dota2Inventory inventory, JSONObject itemData)
            throws Dota2Exception {
        super(inventory, itemData);

        this.equipped = !itemData.isNull("equipped");
    }

    /**
     * Returns whether this item is equipped by this player at all
     *
     * @return Whether this item is equipped by this player at all
     */
    public boolean isEquipped() {
        return this.equipped;
    }

}
