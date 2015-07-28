package org.hyperion.rs2.model.joshyachievements;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.requirement.KillStreakRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.NpcKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.PlayerKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillRequirement;

public class AchievementTracker{

    public static class Progress{

        private Date start;
        private int current;
        private Date end;

        private Progress(final Date start, final int current, final Date end){
            this.start = start;
            this.current = current;
            this.end = end;
        }

        private Progress(){
            this(null, 0, null);
        }

        public int getCurrent(){
            return current;
        }

        public int increment(final int size){
            return current += size;
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

        map = new TreeMap<>();
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

    private void finish(final AchievementContext ctx, final Progress progress){
        progress.finish();
        player.sendf("Congratulations! Achievement complete: %s", ctx.getTitle(player));
        ctx.reward(player);
    }

    public String getUpdateString(final AchievementContext ctx, final int current, final int max){
        return String.format(
                "[@blu@%s@bla@] @blu@%,d@bla@ / @blu@%,d@bla@ (@blu@%,d@bla@ remaining!)",
                ctx.getTitle(player),
                current,
                max,
                max-current
        );
    }

    public String getUpdateString(final AchievementContext ctx){
        return getUpdateString(ctx, getProgress(ctx).getCurrent(), ctx.applyRequirement(player));
    }

    private void progress(final AchievementContext ctx, final int incrementSize, final boolean showUpdateText){
        final Progress progress = getProgress(ctx.getId());
        if(progress.isComplete()) //already finished the achievement
            return;
        if(progress.isStarted()){
            final int current = progress.getCurrent();
            final int max = ctx.applyRequirement(player);
            if(current >= ctx.applyRequirement(player)){ //finally finished
                finish(ctx, progress);
            }else {
                if(showUpdateText)
                    player.sendMessage(getUpdateString(ctx, current, max));
                if(progress.increment(incrementSize) >= max)
                    finish(ctx, progress);
            }
        }else{
            progress.start();
            player.sendf("Good luck! Achievement started: %s", ctx.getTitle(player));
        }
    }

    private void progress(final AchievementContext ctx, final boolean showUpdateText){
        progress(ctx, 1, showUpdateText);
    }

    public void npcKilled(final int npcId){
        AchievementContext.findFirst(this, NpcKillRequirement.filter(npcId))
                .ifPresent(a -> progress(a, true));
    }

    public void playerKilled(){
        AchievementContext.findFirst(this, PlayerKillRequirement.filter())
                .ifPresent(a -> progress(a, true));
    }

    public void skillIncreased(final int skill, final int xpGained){
        AchievementContext.findFirst(this, SkillRequirement.filter(skill))
                .ifPresent(a -> progress(a, xpGained, true));
    }

    public void killStreakIncreased(){
        AchievementContext.findFirst(this, KillStreakRequirement.filter())
                .ifPresent(a -> progress(a, true));
    }

}
