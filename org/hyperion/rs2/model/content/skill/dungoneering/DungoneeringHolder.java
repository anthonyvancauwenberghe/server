package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.DungeonDifficulty;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

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
    private DungeonDifficulty chosen;

    public boolean inDungeon() {
        return currentDungeon != null;
    }

    public void fireOnLogout(final Player player) {
        if(inDungeon()) {
            currentDungeon.remove(player);
        }
    }

    public void start(final List<Player> players) {
        final Dungeon dungeon = new Dungeon(players, chosen);
    }

    public void setChosen(final DungeonDifficulty difficulty) {
        this.chosen = difficulty;
    }

    public DungeonDifficulty getChosen() {
        return chosen == null ? DungeonDifficulty.EASY : chosen;
    }

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
