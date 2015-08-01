package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class NpcKillRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int npcId;

        private Filter(final int npcId){
            this.npcId = npcId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof NpcKillRequirement
                    && ctx.<NpcKillRequirement>getRequirement().getNpcIds().contains(npcId);
        }
    }

    private final List<Integer> npcIds;

    public NpcKillRequirement(final int[] npcIds, final int kills){
        super(kills);
        this.npcIds = Arrays.stream(npcIds).boxed().collect(Collectors.toList());
    }

    public List<Integer> getNpcIds(){
        return npcIds;
    }

    public String toString(){
        return String.format("NpcKillRequirement(npcIds=%s,value=%,d)", npcIds, get());
    }

    public static Predicate<AchievementContext> filter(final int npcId){
        return new Filter(npcId);
    }
}
