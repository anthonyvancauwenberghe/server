package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/26/14
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmblemShop extends PointsShop{

    public EmblemShop(int id, String name, Container container) {
        super(id, name, container);
    }

    @Override
    public void buyFromShop(Player player, Item item) {
        super.buyFromShop(player, item);
        player.getActionSender().sendString(3901, "Emblem Points: @red@" + player.getBountyHunter().getEmblemPoints());
    }


    @Override
    public String getPointsName() {
        return "Emblem Points";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getPrice(int itemId) {
        switch(itemId) {
            case 18806:
                return 1;
            case 19605:
                return 2000;
            case 13898:
                return 500;
            case 13892:
            case 13886:
                return 850;
            case 13188:
            case 13189:
            case 13190:
            case 13191:
            case 13192:
            case 13193:
            case 13194:
                return 250;

        }
        return 50_000;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int getPointsAmount(Player player) {
        return player.getBountyHunter().getEmblemPoints();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void setPointsAmount(Player player, int value) {
        player.getBountyHunter().setEmblemPoints(value);
    }
}