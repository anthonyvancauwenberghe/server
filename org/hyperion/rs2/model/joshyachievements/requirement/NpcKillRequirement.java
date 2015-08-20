package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class NpcKillRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final boolean slayerTask;
        private final int npcId;

        private Filter(final boolean slayerTask, final int npcId){
            this.slayerTask = slayerTask;
            this.npcId = npcId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof NpcKillRequirement
                    && ctx.<NpcKillRequirement>getRequirement().isSlayerTask() == slayerTask
                    && ctx.<NpcKillRequirement>getRequirement().getNpcIds().contains(npcId);
        }
    }

    private final boolean slayerTask;
    private final List<Integer> npcIds;

    public NpcKillRequirement(final boolean slayerTask, final int[] npcIds, final int kills){
        super(kills);
        this.slayerTask = slayerTask;
        this.npcIds = Arrays.stream(npcIds).boxed().collect(Collectors.toList());
    }

    public boolean isSlayerTask(){
        return slayerTask;
    }

    public List<Integer> getNpcIds(){
        return npcIds;
    }

    public String toString(){
        return String.format("NpcKillRequirement(npcIds=%s,value=%,d)", npcIds, get());
    }

    public static Predicate<AchievementContext> filter(final boolean slayerTask, final int npcId){
        return new Filter(slayerTask, npcId);
    }
}
