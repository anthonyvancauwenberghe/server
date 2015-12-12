package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.util.TextUtils;

import java.io.FileNotFoundException;

public class DangerousPK implements ContentTemplate {
    static {
        CommandHandler.submit(new Command("fightpits", Rank.PLAYER) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                Magic.teleport(player, Location.create(2399, 5178, 0), false, false);
                return true;
            }

        });
        CommandHandler.submit(new Command("dangerouspk", Rank.PLAYER) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                Magic.teleport(player, 2480, 5174, 0, false, false);
                return true;
            }

        });
    }

    //pick a class
    public static void openDialogue(final Player player) {
        DialogueManager.openDialogue(player, 145);
    }

    //send back to entrance area
    public static Location entrance() {
        return Location.create(2480, 5175, 0);
    }

    //send to wait area
    public static void toWaitArea(final Player player) {
        if(!ItemSpawning.canSpawn(player)){
            player.sendMessage("You can't access dangeroupk from here");
            return;
        }
        if(player.getPoints().getPkPoints() < 75){
            player.sendMessage("You need at least 75PKP to enter this arena!");
            return;
        }
        player.playAnimation(Animation.create(7376));
        World.getWorld().submit(new Event(1000) {
            public void execute() {
                player.setTeleportTarget(Location.create(2475, 5214, 0));
                this.stop();
            }
        });
    }

    /**
     * Checks if in FFACombat, no location currently
     */
    public static boolean inDangerousPK(final Player player) {
        final int x = player.getLocation().getX();
        final int y = player.getLocation().getY();
        return inDangerousPK(x, y);
    }

    public static boolean inDangerousPK(final int x, final int y) {
        return x > 2450 && y > 5205 && y < 5220 && x < 2477;
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        //object clicking, entering/exiting
        if(type == 6){
            if(a == 9359){
                openDialogue(player);
            }
            if(a == 1755){
                Magic.teleport(player, entrance(), false);
            }
        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public int[] getValues(final int type) {
        return new int[]{9359};
    }
    //2474, 5214


    public enum ArmourClass {
        MAGE,
        MELEE,
        RANGE;

        public String toString() {
            String name = super.toString();
            name = name.replace("_", " ");
            name = TextUtils.titleCase(name);
            return name;
        }
    }

}
