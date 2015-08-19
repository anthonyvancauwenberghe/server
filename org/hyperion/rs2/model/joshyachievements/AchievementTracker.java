package org.hyperion.rs2.model.joshyachievements;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.requirement.AchievementCompletionRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.ItemOpenRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.KillStreakRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.NpcKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.PlayerKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillXpRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillingObjectRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.VoteRequirement;

public class AchievementTracker{

    public static class Progress{

        private final int achievementId;
        private Date start;
        private int current;
        private Date finish;

        private Progress(final int achievementId, final Date start, final int current, final Date finish){
            this.achievementId = achievementId;
            this.start = start;
            this.current = current;
            this.finish = finish;
        }

        private Progress(final int achievementId){
            this(achievementId, null, 0, null);
        }

        public int getAchievementId(){
            return achievementId;
        }

        public AchievementContext getAchievement(){
            return AchievementContext.get(achievementId);
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

        public Date getFinish(){
            return finish;
        }

        public void setFinish(final Date finish){
            this.finish = finish;
        }

        public void finish(){
            finish = new Date();
        }

        public boolean isStarted(){
            return start != null;
        }

        public boolean isComplete(){
            return start != null && finish != null && finish.after(start);
        }

        public boolean isNotComplete(){
            return !isComplete();
        }

        private String toSaveString(){
            return String.format(
                    "%d:%d:%d:%d",
                    achievementId,
                    Optional.ofNullable(start).map(Date::getTime).orElse(-1L),
                    current,
                    Optional.ofNullable(finish).map(Date::getTime).orElse(-1L)
            );
        }

        private static Progress fromSaveString(final String line){
            final String[] tokens = line.split(":");
            final int achievementId = Integer.parseInt(tokens[0]);
            final Date start = Optional.of(Integer.parseInt(tokens[1]))
                    .filter(i -> i != -1)
                    .map(Date::new)
                    .orElse(null);
            final int current = Integer.parseInt(tokens[2]);
            final Date end = Optional.of(Integer.parseInt(tokens[3]))
                    .filter(i -> i != -1)
                    .map(Date::new)
                    .orElse(null);
            return new Progress(achievementId, start, current, end);
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
            map.put(id, new Progress(id));
        return map.get(id);
    }

    private void putProgress(final Progress progress){
        map.put(progress.getAchievementId(), progress);
    }

    public Progress getProgress(final AchievementContext ctx){
        return getProgress(ctx.getId());
    }

    public Stream<Progress> streamProgress(){
        return map.values().stream();
    }

    private void finish(final AchievementContext ctx, final Progress progress){
        progress.finish();
        player.sendf("Congratulations! Achievement complete: %s", ctx.getTitle());
        ctx.reward(player);
        achievementCompleted();
    }

    public String getUpdateString(final AchievementContext ctx, final int current, final int max){
        return String.format(
                "[@blu@%s@bla@] @blu@%,d@bla@ / @blu@%,d@bla@ (@blu@%,d@bla@ remaining!)",
                ctx.getTitle(),
                current,
                max,
                max-current
        );
    }

    public String getUpdateString(final AchievementContext ctx){
        return getUpdateString(ctx, getProgress(ctx).getCurrent(), ctx.getRequirementMax());
    }

    public String getUpdateString(final int id){
        return getUpdateString(AchievementContext.get(id));
    }

    private void progress(final AchievementContext ctx, final int incrementSize, final boolean showUpdateText){
        final Progress progress = getProgress(ctx.getId());
        if(progress.isComplete()) //already finished the achievement
            return;
        final int current = progress.increment(incrementSize);
        final int max = ctx.getRequirementMax();
        if(!progress.isStarted()){
            player.sendf("Good luck! Achievement started: %s", ctx.getTitle());
            progress.start();
        }
        if(showUpdateText)
            player.sendMessage(getUpdateString(ctx, current, max));
        if(current >= max)
            finish(ctx, progress);
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
        AchievementContext.findFirst(this, SkillXpRequirement.filter(skill))
                .ifPresent(a -> progress(a, xpGained, true));
    }

    public void skillingObjectIncreased(final int skill, final int itemId, final int gained){
        AchievementContext.findFirst(this, SkillingObjectRequirement.filter(skill, itemId))
                .ifPresent(a -> progress(a, gained, true));
    }

    public void killStreakIncreased(){
        AchievementContext.findFirst(this, KillStreakRequirement.filter())
                .ifPresent(a -> progress(a, true));
    }

    public void achievementCompleted(){
        AchievementContext.findFirst(this, AchievementCompletionRequirement.filter())
                .ifPresent(a -> progress(a, true));
    }

    public void itemOpened(final int itemId, final int times){
        AchievementContext.findFirst(this, ItemOpenRequirement.filter(itemId))
                .ifPresent(a -> progress(a, times, true));
    }

    public void itemOpened(final int itemId){
        itemOpened(itemId, 1);
    }

    public void voted(final int times){
        AchievementContext.findFirst(this, VoteRequirement.filter())
                .ifPresent(a -> progress(a, times, true));
    }

    public void voted(){
        voted(1);
    }

    public String toSaveString(){
        return streamProgress()
                .map(Progress::toSaveString)
                .collect(Collectors.joining(","));
    }

    public void loadFromSaveString(final String line){
        Stream.of(line.split(","))
                .map(Progress::fromSaveString)
                .forEach(this::putProgress);
    }

}
