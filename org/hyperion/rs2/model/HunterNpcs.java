package org.hyperion.rs2.model;

import org.hyperion.rs2.model.content.skill.Hunter;
import org.hyperion.util.Misc;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vegas/Linus/Flux/Jolt/KFC/Tinderbox/Jack Daniels <- Same Person
 */

public class HunterNpcs {

    private static final int MIN_X = 2943, MAX_Y = 3518, MAX_X = 3279, MIN_Y = 3156;

    private static final List<NPC> imps = new LinkedList<>();

    private static int getRandomImp() {
        final int value = Hunter.IMP_IDS.size() - 1;
        return Hunter.IMP_IDS.get(Math.min(Misc.random(value), Misc.random(value)));
    }

    public static void startup() {
        for (int array = 0; array < 250; array++) {
            spawn();
        }
    }

    public static void spawn() {

        imps.add(NPCManager.addNPC(Position.create(MIN_X + Misc.random(MAX_X - MIN_X), MIN_Y + Misc.random(MAX_Y - MIN_Y), 0), getRandomImp(), -1));
    }

    public static boolean remove(final int id, final int x, final int y) {
        for (NPC value : imps) {
            if (value.getDefinition().getId() == id && value.getPosition().equals(Position.create(x, y, 0))) {
                final NPC npc = value;
                if (npc != null) {
                    imps.remove(npc);
                    npc.setDead(true);
                    npc.isHidden(true);
                    npc.destroy();
                    return true;
                }
            }
        }
        return false;
    }

}
