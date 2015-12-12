package org.hyperion.rs2.model.joshyachievementsv2.task.impl;

import org.hyperion.rs2.model.joshyachievementsv2.task.Task;
import org.hyperion.rs2.model.joshyachievementsv2.utils.AchievementUtils;

import java.util.List;

public class NpcKillTask extends Task {

    public final List<Integer> npcIds;

    public NpcKillTask(final int id, final List<Integer> npcIds, final int kills) {
        super(id, kills);
        this.npcIds = npcIds;

        final String npcsJoined = AchievementUtils.joinNpcs(npcIds);

        desc = String.format("Kill %,d %s", kills, npcsJoined);
    }

    public static Filter<NpcKillTask> filter(final int npcId) {
        return new MyFilter(npcId);
    }

    private static class MyFilter extends Filter<NpcKillTask> {

        private final int npcId;

        private MyFilter(final int npcId) {
            super(NpcKillTask.class);
            this.npcId = npcId;
        }

        public boolean test(final NpcKillTask t) {
            return super.test(t) && t.npcIds.contains(npcId);
        }
    }
}
