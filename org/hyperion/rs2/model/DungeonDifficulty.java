package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.attack.RevAttack;
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
    EASY(3, 5, 0, 10_000, Time.ONE_MINUTE * 3, new int[]{2881, 2882, 2883, 3200}, 1, 5338, 299, 255, 32, 449, 5595, 196, 119, 1677, 2627, 4940, 4693, 112, 78, 2630),
    MEDIUM(3, 8, 45, 40_000, Time.FIVE_MINUTES + Time.ONE_MINUTE, RevAttack.getRevs(),  51,52,53, 55, 82, 83,941, 1582, 1583, 49, 2741),
    HARD(2, 10, 80, 150_000, Time.TEN_MINUTES, new int[]{6260, 6247, 6203, 6222, 8349}, 6252, 6248, 6250, 6208, 6204, 6206, 6223, 6225, 6227, 1592, 1591, 1590, 54, 84, 2743, 5253);


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

    public enum DungeonSize {
        SMALL(5, 0.9, .8),
        MEDIUM(15, 2.0, 2.4),
        LARGE(30, 4.5, 4.8);

        public final int size;
        public final double multiplier;
        public final double multi_time;
        private DungeonSize(final int size, final double multiplier, final double multi) {
            this.size = size;
            this.multiplier = multiplier;
            this.multi_time = multi;
        }
    }

}
