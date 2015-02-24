package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.dungoneering.Room;
import org.hyperion.util.Misc;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 9:05 AM
 * To change this template use File | Settings | File Templates.
 */

public enum DungeonDifficulty {
    EASY(2, 0, null, null),
    MEDIUM(3, 35,null,  null),
    HARD(4, 70, null, null);


    public final int min_level, spawns;
    private final int[] monsters, bosses;

    private DungeonDifficulty(final int spawns, final int min_level, final int[] bosses, final int... monsters) {
        this.min_level = min_level;
        this.spawns = spawns;
        this.monsters = monsters;
        this.bosses = bosses;

    }

    public int[] getMonsters() {
        return monsters.clone();
    }

    public int[] getBosses() {
        return monsters.clone();
    }

    public void getBoss(final Room room) {
        World.getWorld().getNPCManager().addNPC(room.definition.x, room.definition.y, room.dungeon.heightLevel, -1, bosses[Misc.random(bosses.length-1)]).agreesiveDis = 20;
    }
}
