package org.hyperion.rs2.model.content.transport;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.agility.courses.GnomeStronghold;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Liberty and Arsen Maxyutov
 */

public class GnomeGliders implements ContentTemplate {

    //button,x,y,h,move
    public static final int[][] GLIDER_DATA = {{826, 2848, 3497, 0, 1}, //  TO MOUNTAIN
            {825, GnomeStronghold.location.getX(), GnomeStronghold.location.getY(), GnomeStronghold.location.getZ(), 2},
            // TO GRAND TREE
            {827, 3321, 3427, 0, 3}, // TO CASTLE
            {828, 3278, 3212, 0, 4}, // TO DESERT
            {824, 2894, 2730, 0, 8}, // TO CRASH ISLAND
            {12342, 2544, 2970, 0, 10}, // TO OGRE AREA
    };

    public static final int NPC_COORDINATES[][] = {{2481, 3434}, {2849, 3496}, {2546, 2973}, {2894, 2728}, {3281, 3209},
            {3324, 3426}, {3115, 3515},};

    private static final List<NPC> npcs = new LinkedList<NPC>();

    private static void flightButtons(final Player player, final int button) {
        for(int i = 0; i < getLength(); i++){
            if(getButton(i) == button){
                handleFlight(player, i);
            }
        }
    }

    private static boolean farFromNpcs(final Player player) {
        for(final NPC npc : npcs){
            if(player.getLocation().isWithinDistance(npc.getLocation(), 15))
                return false;
        }
        return true;
    }

    private static void handleFlight(final Player player, final int flightId) {
        if(farFromNpcs(player))
            return;
        player.getActionSender().showInterface(802);
        player.getActionSender().sendClientConfig(153, getMove(flightId));
        World.getWorld().submit(new Event(1800) {
            public void execute() {
                player.setTeleportTarget(Location.create(getX(flightId), getY(flightId), getH(flightId)));
                this.stop();
            }
        });
        World.getWorld().submit(new Event(2400) {
            public void execute() {
                player.getActionSender().removeAllInterfaces();
                player.getActionSender().sendClientConfig(153, -1);
                this.stop();
            }
        });

    }

    public static int getLength() {
        return GLIDER_DATA.length;
    }

    public static int getButton(final int i) {
        return GLIDER_DATA[i][0];
    }

    public static int getX(final int i) {
        return GLIDER_DATA[i][1];
    }

    public static int getY(final int i) {
        return GLIDER_DATA[i][2];
    }

    public static int getH(final int i) {
        return GLIDER_DATA[i][3];
    }

    public static int getMove(final int i) {
        return GLIDER_DATA[i][4];
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        if(type == 0){
            flightButtons(player, a);
        }else if(type == 10)
            player.getActionSender().showInterface(802);
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        for(int i = 0; i < NPC_COORDINATES.length; i++){
            addNPC(2649, NPC_COORDINATES[i][0], NPC_COORDINATES[i][1], 0);
        }
    }

    private void addNPC(final int id, final int x, final int y, final int z) {
        final NPC npc = World.getWorld().getNPCManager().addNPC(x, y, z, id, -1);
        npcs.add(npc);
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 0){
            return Misc.getColumn(GLIDER_DATA, 0);
        }
        if(type == 10){
            final int[] a = {2649};
            return a;
        }
        return null;
    }
}