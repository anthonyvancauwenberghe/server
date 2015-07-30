package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class NpcKillRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int npcId;

        private Filter(final int npcId){
            this.npcId = npcId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof NpcKillRequirement
                    && ctx.<NpcKillRequirement>getRequirement().getNpcId() == npcId;
        }
    }

    private final int npcId;

    public NpcKillRequirement(final int npcId, final int kills){
        super(kills);
        this.npcId = npcId;
    }

    public int getNpcId(){
        return npcId;
    }

    public String toString(){
        return String.format("NpcKillRequirement(npcId=%s,value=%,d)", npcId, get());
    }

    public static Predicate<AchievementContext> filter(final int npcId){
        return new Filter(npcId);
    }
}
