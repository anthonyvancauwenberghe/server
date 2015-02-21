package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;

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
    EASY(2, 0, null),
    MEDIUM(3, 35, null),
    HARD(4, 70, null);


    private final int min_level, spawns;
    private final LinkedList<Integer> npcs;

    private DungeonDifficulty(final int spawns, final int min_level, final int... monsters) {
        this.min_level = min_level;
        this.spawns = spawns;
        final LinkedList<Integer> npcs = new LinkedList<>();
        for(int i : monsters)
            npcs.add(i);
        this.npcs = npcs;


    }

    public List<Integer> getNpcs() {
        return npcs;
    }

    public void getBoss(final Room room) {
        World.getWorld().getNPCManager().addNPC(room.definition.x, room.definition.y, room.dungeon.heightLevel, -1, npcs.getLast()).agreesiveDis = 20;
    }
}
