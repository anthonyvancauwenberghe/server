package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;

public class AchievementProgress{

    public final int achievementId;
    private final Map<Integer, AchievementTaskProgress> progress;

    public Date startDate;
    public Date finishDate;

    public AchievementProgress(final int achievementId){
        this.achievementId = achievementId;

        progress = new TreeMap<>();

        achievement().tasks.stream()
                .forEach(t -> add(new AchievementTaskProgress(achievementId, t.id)));
    }

    public void startNow(){
        startDate = new Date();
    }

    public void finishNow(){
        finishDate = new Date();
    }

    public Achievement achievement(){
        return Achievements.get().get(achievementId);
    }

    public void add(final AchievementTaskProgress atp){
        progress.put(atp.taskId, atp);
    }

    public AchievementTaskProgress progress(final int taskId){
        return progress.get(taskId);
    }

    public Stream<AchievementTaskProgress> streamAvailableProgress(){
        return progress.values().stream()
                .filter(AchievementTaskProgress::started);
    }

    public int progress(){
        return progress.values().stream()
                .mapToInt(atp -> atp.progress)
                .sum();
    }

    public boolean started(){
        return startDate != null;
    }

    public boolean finished(){
        return startDate != null
                && finishDate != null
                && finishDate.after(startDate)
                && tasksFinished();
    }

    public boolean tasksFinished(){
        return progress.values().stream()
                    .allMatch(AchievementTaskProgress::finished);
    }

    public void sendProgressHeader(final Player player){
        player.sendf("[@blu@%s@bla@] @blu@%,d@bla@ / @red@%,d@bla@ | @gre@%1.2f%%@bla@ Complete",
                achievement().title, progress(), achievement().tasks.threshold, progressPercent());
    }

    public void sendAllTaskProgressHeaders(final Player player){
        progress.values()
                .forEach(p -> p.sendProgress(player, false));
    }

    public double progressPercent(){
        return (double)progress() / achievement().tasks.threshold;
    }
}
