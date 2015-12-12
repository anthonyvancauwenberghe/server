package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Time;

import java.io.FileNotFoundException;

/**
 * @author Arsen Maxyutov
 */
public class SnowItems implements ContentTemplate {

    public static final Item SNOWBALL = new Item(11951);
    public static final int[] SANTA_SUIT_IDS = {14595, 14602, 14603, 14604, 14605};
    private static final long MAXTIME = 1389127745492L;

    static {
        CommandHandler.submit(new Command("testimps", Rank.ADMINISTRATOR) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                player.getExtraData().put("impscaught", 20);
                return true;
            }

        });
    }

    public static void fireSnowBall(final Player player, final Player victim) {
        player.face(victim.getLocation());
        final int offsetY = (player.cE.getAbsX() - (victim.cE.getAbsX() + victim.cE.getOffsetX())) * -1;
        final int offsetX = (player.cE.getAbsY() - (victim.cE.getAbsY() + victim.cE.getOffsetY())) * -1;
        // Lockon Target
        final int hitId = player.cE.getSlotId(player.cE.getEntity());
        // create the projectile
        player.getActionSender().createGlobalProjectile(player.cE.getAbsY(), player.cE.getAbsX(), offsetY, offsetX, 30, 105, 1281, 31, 31, hitId, 65, 16);
    }

    public static void main(final String... args) {
        System.out.println(System.currentTimeMillis() + Time.ONE_WEEK * 2);
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        if(type == 1){
            startSnowing(player);
        }else if(type == 10){

        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        if(System.currentTimeMillis() < MAXTIME)
            World.getWorld().getNPCManager().addNPC(3085, 3497, 0, 9400, -1);
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 1){
            final int[] a = {11949};
            return a;
        }
        return null;
    }

    private void startSnowing(final Player player) {
        ContentEntity.startAnimation(player, 7528);
        ContentEntity.playerGfx(player, 1284);//11951
        player.getInventory().add(new Item(11951, player.getInventory().freeSlots()));
        player.getActionSender().showInterfaceWalkable(11877);
        World.getWorld().submit(new Event(10000) {
            public void execute() {
                player.getActionSender().showInterfaceWalkable(-1);
                this.stop();
            }
        });
    }
}
