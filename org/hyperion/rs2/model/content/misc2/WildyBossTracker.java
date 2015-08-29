package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.event.impl.WildernessBossEvent;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 8/28/15
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class WildyBossTracker implements ContentTemplate {


    @Override
    public int[] getValues(int type) {
        if(type == ClickType.EAT)
            return new int[]{15008};
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        if(WildernessBossEvent.currentBoss != null)
            player.sendf("You must walk @red@ %s", compassDirection(player.getLocation().getX(), player.getLocation().getY(), WildernessBossEvent.currentBoss.getLocation().getX(), WildernessBossEvent.currentBoss.getLocation().getY()));
        else
            player.sendf("Wilderness boss spawning in:@red@ %s @bla@minutes", TimeUnit.MINUTES.convert(
                    (WildernessBossEvent.DELAY_FOR_RESPAWN - (System.currentTimeMillis() - WildernessBossEvent.timeStart)), TimeUnit.MILLISECONDS)
            );
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static String compassDirection(int xPos, int yPos, int xDest, int yDest) {
        if(xDest == xPos && yDest == yPos)
            return "Undefined";
        final StringBuilder builder = new StringBuilder();
        if(yPos != yDest) {
            builder.append(yPos > yDest ? "South" : "North");
        }
        if(xPos != xDest) {
            builder.append(xPos > xDest ? "West" : "East");
        }


        return builder.toString();

    }

}
