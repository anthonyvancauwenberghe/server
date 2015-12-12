package org.hyperion.rs2.model.shops;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.EquipmentReq;

public class RecklessHonorShop extends PointsShop {

    public RecklessHonorShop(final int id, final String name, final Container container) {
        super(id, name, container);
    }

    @Override
    public String getPointsName() {
        return "DeviousPK Points";
    }

    @Override
    public int getPrice(final int itemId) {
        switch(itemId){
            case 19817:
            case 19816:
            case 19815:
                return 5000;
        }
        return 5000;
    }

    @Override
    protected int getPointsAmount(final Player player) {
        return player.getPoints().getPkPoints();
    }

    @Override
    protected void setPointsAmount(final Player player, final int value) {
        player.getPoints().setPkPoints(value);
    }

    @Override
    public void buyFromShop(final Player player, final Item item) {
        final int requiredHonors = EquipmentReq.requiredHonorPoints(item.getId());
        if(player.getPoints().getHonorPoints() < requiredHonors){
            player.getActionSender().sendMessage("You need at least " + requiredHonors + " honor points to buy this item.");
        }else{
            super.buyFromShop(player, item);
            player.getActionSender().sendString(3901, "Honor points: @gre@" + player.getPoints().getHonorPoints());
        }
    }
}
