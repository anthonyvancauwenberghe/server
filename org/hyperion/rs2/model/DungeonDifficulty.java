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
    EASY(2, 5, 0, new int[]{2883, 2881, 2882}, null),
    MEDIUM(3, 8, 35,null,  null),
    HARD(4, 10, 70, null, null);


    public final int min_level, spawns, rooms;
    private final int[] monsters, bosses;

    private DungeonDifficulty(final int spawns, final int rooms,  final int min_level, final int[] bosses, final int... monsters) {
        this.min_level = min_level;
        this.spawns = spawns;
        this.monsters = monsters;
        this.bosses = bosses;
        this.rooms = rooms;

    }

    public int getRandomMonster() {
        return monsters[Misc.random(monsters.length - 1)];
    }

    public int getBoss() {
        return bosses[Misc.random(bosses.length - 1)];
    }

}
