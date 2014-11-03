package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/3/14
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlayerShop extends PointsShop {
    public SlayerShop(int id, String name, Container container) {
        super(id, name, container);
    }

    @Override
    public String getPointsName() {
        return "Slayer Points";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPrice(int itemId) {
        switch(itemId) {
            case 13263:
                return 400;
            case 15492:
                return 1000;
            case 17291:
                return 500;
            case 12862:
                return 250;
        }
        return 5000;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int getPointsAmount(Player player) {
        return player.getSlayerTask().getSlayerPoints();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setPointsAmount(Player player, int value) {
        player.getSlayerTask().setPoints(value);
    }
}
