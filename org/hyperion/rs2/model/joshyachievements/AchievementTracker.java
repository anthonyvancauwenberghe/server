package org.hyperion.rs2.model.joshyachievements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.joshyachievements.condition.Condition;
import org.hyperion.rs2.model.joshyachievements.condition.KillStreakCondition;
import org.hyperion.rs2.model.joshyachievements.condition.NpcKillCondition;
import org.hyperion.rs2.model.joshyachievements.condition.PlayerKillCondition;
import org.hyperion.rs2.model.joshyachievements.condition.SkillCondition;

public class AchievementTracker{

    public static class Progress{

        private Date start;
        private Date end;

        private Progress(final Date start, final Date end){
            this.start = start;
            this.end = end;
        }

        private Progress(){
            this(null, null);
        }

        public void start(){
            start = new Date();
        }

        public Date getStart(){
            return start;
        }

        public void setStart(final Date start){
            this.start = start;
        }

        public Date getEnd(){
            return end;
        }

        public void setEnd(final Date end){
            this.end = end;
        }

        public void finish(){
            end = new Date();
        }

        public boolean isStarted(){
            return start != null;
        }

        public boolean isComplete(){
            return start != null && end != null && end.after(start);
        }

        public boolean isNotComplete(){
            return !isComplete();
        }
    }

    private final Player player;
    private final Map<Integer, Progress> map;

    public AchievementTracker(final Player player){
        this.player = player;

        map = new HashMap<>();
    }

    public Player getPlayer(){
        return player;
    }

    public Progress getProgress(final int id){
        if(!map.containsKey(id))
            map.put(id, new Progress());
        return map.get(id);
    }

    public Progress getProgress(final AchievementContext ctx){
        return getProgress(ctx.getId());
    }

    private void progress(final AchievementContext ctx, final String updateText){
        final Progress progress = getProgress(ctx.getId());
        if(progress.isComplete()) //already finished the achievement
            return;
        if(progress.isStarted()){
            if(ctx.isConditionMet(player)){ //finally finished
                progress.finish();
                player.sendf("Congratulations! Achievement complete: %s", ctx.getTitle());
                ctx.reward(player);
            }else if(updateText != null){
                final Condition cond = ctx.getCondition();
                player.sendMessage(
                        updateText.replace("$TITLE", String.format("@blu@%s@bla@", ctx.getTitle()))
                                .replace("$CURRENT", String.format("@blu@%,d@bla@", cond.getCurrent(player)))
                                .replace("$MAX", String.format("@blu@%,d@bla@", cond.getMax(player)))
                                .replace("$REMAINING", String.format("@blu@%,d@bla@", cond.getRemaining(player)))
                );
            }
        }else{
            progress.start();
            player.sendf("Good luck! Achievement started: %s", ctx.getTitle());
        }
    }

    private void progress(final AchievementContext ctx, final String updateFormat, final Object... args){
        progress(ctx, String.format(updateFormat, args));
    }

    private void progress(final AchievementContext ctx){
        progress(ctx, null);
    }

    public void npcKilled(final int npcId){
        final String name = NPCDefinition.forId(npcId).getName();
        AchievementContext.findFirst(this, NpcKillCondition.filter(npcId))
                .ifPresent(a -> progress(a, "[$TITLE] $CURRENT / $MAX %s kills! ($REMAINING kills remaining!)", name));
    }

    public void playerKilled(){
        AchievementContext.findFirst(this, PlayerKillCondition.filter())
                .ifPresent(a -> progress(a, "[$TITLE] $CURRENT / $MAX kills! ($REMAINING kills remaining!)"));
    }

    public void skillIncreased(final int skill){
        final String name = Skills.SKILL_NAME[skill];
        AchievementContext.findFirst(this, SkillCondition.filter(skill))
                .ifPresent(a -> progress(a, "[$TITLE] $CURRENT / $MAX %s XP! ($REMAINING XP remaining!)", name));
    }

    public void killStreakIncreased(){
        AchievementContext.findFirst(this, KillStreakCondition.filter())
                .ifPresent(a -> progress(a, "[$TITLE] $CURRENT / $MAX kills! ($REMAINING kills remaining!"));
    }

}
