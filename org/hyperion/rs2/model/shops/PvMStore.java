package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 5/26/15
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PvMStore extends CurrencyShop{

    public static final int TOKEN = 17564;


    /**
     * @param id
     * @param name
     * @param container
     */
    public PvMStore(int id, String name, Container container) {
        super(id, name, container, TOKEN, false);
    }

    @Override
    public void valueSellItem(Player player, Item item) {
    }

    @Override
    public void sellToShop(Player player, Item item) {
        player.sendMessage("You cannot sell to this shop!");
    }
}


