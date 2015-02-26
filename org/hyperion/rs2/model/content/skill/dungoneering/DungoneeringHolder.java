package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.madturnip.tools.ItemDefEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class DungoneeringHolder {


    private final Item[] bound = new Item[3];
    private int dungoneeringPoints;
    private Dungeon currentDungeon;
    private Room room;

    public void fireOnLogout(final Player player) {
        if(inDungeon()) {
            currentDungeon.remove(player, false);
        }
    }

    public void start(final List<Player> players, final DungeonDifficulty chosen) {
        final Iterator<Player> it = players.iterator();
        while(it.hasNext()) {
            if(DungeoneeringManager.cantJoin(it.next()))
                it.remove();
        }
        final Dungeon dungeon = new Dungeon(players, chosen);
        dungeon.start();
    }

    public Location clickPortal() {
        if(!room.cleared())
            return null;
        if(room.boss) {
            currentDungeon.complete();
            return null;
        }
        final Location location = room.getChild().getSpawnLocation();
        setCurrentRoom(room.getChild());
        return location;
    }

    private void setCurrentRoom(final Room room) { this.room = room; }

    public boolean inDungeon() { return currentDungeon != null; }

    public Dungeon getCurrentDungeon() { return currentDungeon; }

    public Room getRoom() { return room; }

    /** ************************
     * START OF SAVING
     * *************************/

    public String save() {
        final StringBuilder builder = new StringBuilder(dungoneeringPoints+"-");
        for(final Item item : bound) {
            if(item != null)
                builder.append(item.getId()).append(",").append(item.getCount()).append(" ");
            else
                builder.append("-1").append(",").append("0").append(" ");
        }
        return builder.toString();
    }

    public void load(final String read) {
        if(read.length() < 2) {
            return;
        }
        final String[] split = read.split("-");
        this.dungoneeringPoints = Integer.parseInt(split[0]);
        final String[] items = split[1].split(" ");
        try {
            for(int i = 0; i < items.length - 1; i++) {
                final String[] id_count = items[i].split(",");
                bound[i] = Item.create(Integer.parseInt(id_count[0], Integer.parseInt(id_count[1])));
            }
        } catch(final Exception ex) {

        }

    }

}
