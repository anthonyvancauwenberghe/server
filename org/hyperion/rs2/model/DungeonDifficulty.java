package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.dungoneering.Room;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

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
    EASY(2, 5, 0, 5_000, Time.ONE_MINUTE * 3, new int[]{2881, 2882}, 1, 5338, 299, 255, 32, 449, 5595),
    MEDIUM(3, 8, 35, 20_000, Time.FIVE_MINUTES + Time.ONE_MINUTE, new int[]{2883},  null),
    HARD(4, 10, 70, 50_000, Time.TEN_MINUTES, null, null);


    public final int min_level, spawns, rooms, xp;
    private final int[] monsters, bosses;
    public final long time;

    private DungeonDifficulty(final int spawns, final int rooms,  final int min_level, final int xp, final long time, final int[] bosses, final int... monsters) {
        this.min_level = min_level;
        this.spawns = spawns;
        this.monsters = monsters;
        this.bosses = bosses;
        this.rooms = rooms;
        this.xp = xp;
        this.time = time;

    }

    public int getRandomMonster() {
        return monsters[Misc.random(monsters.length - 1)];
    }

    public int getBoss() {
        return bosses[Misc.random(bosses.length - 1)];
    }

}
