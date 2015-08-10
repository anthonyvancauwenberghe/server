package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;

import java.io.IOException;
import java.util.Arrays;

public class WildernessBossEvent extends Event {


    private static final Location[] SPAWN_POINTS = { Location.create(3064, 3774, 0),
            Location.create(3144, 3778, 0), Location.create(3255, 3884, 0),
            Location.create(3133, 3846, 0), Location.create(3006, 3825, 0)
    };

    private static final int[] BOSS_IDS = {10141, 9752, 10106, 10126};


    /**
     * Respawn time in minutes.
     */
    private static final int RESPAWN_TIME = 30;

    /**
     * @param forceSpawn used so when the server is restarted, the boss will spawn immediately
     * instead of 30 minutes after the restart.
     */
    public WildernessBossEvent(boolean forceSpawn) {
        super(forceSpawn ? 0 : 60000 * RESPAWN_TIME);
    }

    public static void init() {
        int index = 0;
        int[] bonus = new int[10];
        Arrays.fill(bonus, 390);
        NPCDefinition.getDefinitions()[BOSS_IDS[index]] =
                NPCDefinition.create(BOSS_IDS[index++], 900, 543, bonus, 13602, 13601, new int[]{13603}, 2, "Bal'lak the Pummeller", -1);
        Arrays.fill(bonus, 380);
        NPCDefinition.getDefinitions()[BOSS_IDS[index]] =
                NPCDefinition.create(BOSS_IDS[index++], 1050, 386, bonus, 13424, 13420, new int[]{13430}, 2, "Night-gazer Khighorahk", -1);
        Arrays.fill(bonus, 425);
        NPCDefinition.getDefinitions()[BOSS_IDS[index]] =
                NPCDefinition.create(BOSS_IDS[index++], 1200, 426, bonus, 13005, 13000, new int[]{13001}, 4, "Bulwark Beast", -1);
        Arrays.fill(bonus, 375);
        NPCDefinition.getDefinitions()[BOSS_IDS[index]] =
                NPCDefinition.create(BOSS_IDS[index++], 1100, 433, bonus, 13171, 13167, new int[]{13170}, 2, "Unholy Cursebearer", -1);
    }

    public static boolean isWildernessBoss(int npcId) {
        for(int id : BOSS_IDS)
            if(id == npcId)
                return true;
        return false;
    }

    @Override
    public void execute() throws IOException {
        final int spawn = Combat.random(SPAWN_POINTS.length - 1);
        final int boss = Combat.random(BOSS_IDS.length - 1);
        World.getWorld().getNPCManager().addNPC(SPAWN_POINTS[spawn].getX(), SPAWN_POINTS[spawn].getY(), SPAWN_POINTS[spawn].getZ(), BOSS_IDS[boss], -1);
        this.stop();
    }

}
