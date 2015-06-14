package org.hyperion.rs2.model.content.minigame;

import com.mysql.jdbc.TimeUtil;
import javafx.util.converter.PercentageStringConverter;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.combat.attack.BorkAndMinions;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.Percentage;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.shops.PvMStore;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 6/9/15
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bork implements ContentTemplate {

    private static final String KEY = "borkevent";
    private static final String TIME_KEY = "borktime";

    private static final Location TELEPORT_LOCATION = Location.create(3555, 9947, 0);
    private static final Location BORK_LOCATION = Location.create(3564, 9959, 0);
    private static final Point[] MINION_LOCATIONS = {
            new Point(3551, 9938), new Point(3563, 9941), new Point(3547, 9957)
    };
    private static final long DELAY = Time.TEN_HOURS/2L;

    private static final int INTERFACE_ID = 6568;
    private static final int[] CHILD_IDS = {6569, 6570, 6572, 6664};

    static {
        CommandHandler.submit(new Command("bork", Rank.PLAYER) {

            public boolean execute(final Player player, final String input) {
                long delay;
                if((delay = System.currentTimeMillis() - player.getPermExtraData().getLong(TIME_KEY)) < DELAY) {
                    player.sendf("You must wait@red@ %d @bla@more minutes to kill Bork", TimeUnit.MINUTES.convert(DELAY - delay, TimeUnit.MILLISECONDS));
                    return true;
                } else if(player.getTotalOnlineTime() < Time.ONE_HOUR * 3)  {
                    player.sendf("You need at least 3 hours of online time to attempt Bork");
                    return true;
                }
                final int height = player.getIndex() * 4;
                Magic.teleport(player, TELEPORT_LOCATION.transform(0, 0, height), false);
                World.getWorld().getNPCManager().addNPC(BORK_LOCATION.transform(0, 0, height),BorkAndMinions.BORK_ID, -1);
                for(int i = 0; i<3; i++)
                    World.getWorld().getNPCManager().addNPC(Location.create(MINION_LOCATIONS[i].x, MINION_LOCATIONS[i].y, height), BorkAndMinions.MINION_ID, -1);
                World.getWorld().submit(new BorkEvent(player));
                return true;
            }

        });
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.NPC_DEATH)
            return new int[]{BorkAndMinions.BORK_ID};
        return new int[0];
    }

    private static final class BorkEvent extends Event {
        private static final double PKP_MULTIPLIER = 3;
        private static final double TOKEN_MULTIPLIER = 1;
        /**
         * This represents the percent which is covered by time, rest is given
         */
        private static final double MOD = 50.0;
        private static final int ORIGINAL_TIME = 500;

        private final Player player;
        private int time;

        public BorkEvent(final Player player) {
            super(1000);
            this.player = player;
            time = ORIGINAL_TIME;
            player.getPermExtraData().put(TIME_KEY, System.currentTimeMillis());
            player.getActionSender().showInterfaceWalkable(INTERFACE_ID);
            player.getExtraData().put("cantteleport", true);
            player.getExtraData().put(KEY, this);
            int i = 0;
            player.getActionSender().sendString(CHILD_IDS[i++], "Kill BORK");
            //for(; i < CHILD_IDS.length; i++)
                //player.getActionSender().sendString(CHILD_IDS[i], "");


        }

        @Override
        public void execute()  {
            if(time > 0)
                time--;
            updateInterface();
        }

        public void giveReward(boolean kill) {
            int percentIncrease = (int)percentIncrease();
            int tokens = (int)(percentIncrease * TOKEN_MULTIPLIER);
            int pkt = (int)(percentIncrease * PKP_MULTIPLIER);
            if(!kill)
            {
                pkt = pkt/3;
                tokens = tokens/3;
            }
            player.getBank().add(Item.create(PvMStore.TOKEN, tokens));
            player.getBank().add(Item.create(5020, pkt));
            player.sendf("@red@ %d @bla@ PvM Tokens and @red@ %d @bla@ Pk Tickets have been added to your bank", tokens, pkt);
        }

        public void updateInterface() {
            player.getActionSender().sendString(CHILD_IDS[2], String.format("%.1f", percentIncrease()));

        }

        public double percentIncrease() {
            final Percentage percent = new Percentage(time, ORIGINAL_TIME);
            return percent.toDouble(MOD);
        }


        @Override
        public void stop() {
            super.stop();
            player.getActionSender().removeAllInterfaces();
            player.getActionSender().showInterfaceWalkable(-1);
            player.getExtraData().put("cantteleport", false);
            player.getExtraData().remove(KEY);
        }

    }

    public static boolean doDeath(final Player player) {
        if(player.getExtraData().get(KEY) == null) {
            return false;
        }
        ((BorkEvent)player.getExtraData().get(KEY)).giveReward(false);
        ((BorkEvent)player.getExtraData().get(KEY)).stop();
        player.setTeleportTarget(Edgeville.LOCATION, false);
        return true;

    }

    @Override
    public boolean npcDeath(final Player player, int npcId, int x, int y, int slot) {
        if(player.getExtraData().get(KEY) == null || npcId != BorkAndMinions.BORK_ID) {
            return false;
        }
        ((BorkEvent)player.getExtraData().get(KEY)).giveReward(true);
        ((BorkEvent)player.getExtraData().get(KEY)).stop();
        player.setTeleportTarget(Edgeville.LOCATION, false);
        return true;

    }
}
