package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class NpcKillRequirement implements Requirement{

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
    private final int kills;

    public NpcKillRequirement(final int npcId, final int kills){
        this.npcId = npcId;
        this.kills = kills;
    }

    public int getNpcId(){
        return npcId;
    }

    public int getKills(){
        return kills;
    }

    public NPCDefinition getNpc(){
        return NPCDefinition.forId(npcId);
    }

    public Integer apply(final Player player){
        return kills;
    }

    public String toString(final Player player){
        final String name = NPCDefinition.forId(npcId).getName();
        return String.format("Kill %,d %s%s", kills, name, kills > 1 ? "s" : 0);
    }

    public static Predicate<AchievementContext> filter(final int npcId){
        return new Filter(npcId);
    }
}
