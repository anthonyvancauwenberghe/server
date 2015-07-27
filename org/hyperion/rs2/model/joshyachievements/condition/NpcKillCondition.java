package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Predicate;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class NpcKillCondition implements Condition{

    private static class Filter implements Predicate<AchievementContext>{

        private final int npcId;

        private Filter(final int npcId){
            this.npcId = npcId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getCondition() instanceof NpcKillCondition
                    && ctx.<NpcKillCondition>getCondition().getNpcId() == npcId;
        }
    }

    private final int npcId;
    private final int kills;

    public NpcKillCondition(final int npcId, final int kills){
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

    public int getMax(final Player player){
        return kills;
    }

    public int getCurrent(final Player player){
        return player.getNPCLogs().map().getOrDefault(npcId, 0);
    }

    public String toString(){
        final String name = NPCDefinition.forId(npcId).getName();
        return String.format("Kill %,d %s%s", kills, name, kills > 1 ? "s" : 0);
    }

    public static Predicate<AchievementContext> filter(final int npcId){
        return new Filter(npcId);
    }
}
