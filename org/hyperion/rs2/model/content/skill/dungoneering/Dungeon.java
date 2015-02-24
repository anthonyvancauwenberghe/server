package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.DungeonDifficulty;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.misc2.Edgeville;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Dungeon {

    private List<Room> rooms;

    private final List<Player> players;
    public final int heightLevel;
    private final DungeonDifficulty difficulty;

    public Dungeon(final List<Player> players, final DungeonDifficulty difficulty) {
        this.players = players;
        this.heightLevel = players.get(0).getIndex() * 4;
        this.difficulty = difficulty;
    }


    public final void remove(final Player player) {
        player.setTeleportTarget(Edgeville.LOCATION);



        players.remove(player);
    }

    public void complete() {
        for(final Player player : players) {
            complete();
        }
    }

    public void assignChildren() {
        for(int i = 0; i < rooms.size() - 2; i++) {
            rooms.get(i).setChild(rooms.get(i+1));
        }
        final Room boss = rooms.get(rooms.size() - 1);
        boss.boss = true;

    }

}
