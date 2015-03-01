package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 3/1/15
 * Time: 2:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DungeoneeringStore extends PointsShop {

    public DungeoneeringStore(int id, String name, Container container) {
        super(id, name, container);
    }


    @Override
    public String getPointsName() {
        return "Dungeoneering Tokens";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPrice(int itemId) {
        switch(itemId) {
            case 17985:
            case 17986:
            case 17987:
            case 17988:
            case 17989:
                return 10_000 + (itemId - 17984) * 2000;
        }
        return 50_000;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int getPointsAmount(Player player) {
        return player.getDungoneering().getTokens();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setPointsAmount(Player player, int value) {
        player.getDungoneering().setTokens(value);
    }

}
