package org.hyperion.rs2.model.joshyachievementsv2.task.impl;

import org.hyperion.rs2.model.joshyachievementsv2.task.Task;
import org.hyperion.rs2.model.joshyachievementsv2.utils.AchievementUtils;

import java.util.List;

public class SlayerTask extends Task {

    public final List<Integer> npcIds;

    public SlayerTask(final int id, final List<Integer> npcIds, final int tasks) {
        super(id, tasks);
        this.npcIds = npcIds;

        if(npcIds.isEmpty()){
            desc = String.format("Complete %,d slayer tasks", tasks);
        }else{
            final String npcsJoined = AchievementUtils.joinNpcs(npcIds);
            desc = String.format("Complete %,d %s slayer tasks", tasks, npcsJoined);
        }
    }

    public static Filter<SlayerTask> filter(final int npcId) {
        return new MyFilter(npcId);
    }

    private static class MyFilter extends Filter<SlayerTask> {

        private final int npcId;

        private MyFilter(final int npcId) {
            super(SlayerTask.class);
            this.npcId = npcId;
        }

        public boolean test(final SlayerTask t) {
            return super.test(t) && (t.npcIds.isEmpty() || t.npcIds.contains(npcId));
        }
    }
}
