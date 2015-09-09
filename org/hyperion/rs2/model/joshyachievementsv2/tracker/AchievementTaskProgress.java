package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.util.Date;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.task.Task;

public class AchievementTaskProgress{

    public final int achievementId;
    public final int taskId;

    public int progress;

    public Date startDate;
    public Date finishDate;

    public AchievementTaskProgress(final int achievementId, final int taskId, final int progress, final Date startDate, final Date finishDate){
        this.achievementId = achievementId;
        this.taskId = taskId;
        this.progress = progress;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public AchievementTaskProgress(final int achievementId, final int taskId){
        this(achievementId, taskId, 0, null, null);
    }

    public int progress(final int amount){
        return progress = Math.max(progress + amount, task().threshold);
    }

    public double progressPercent(){
        return (double)progress / task().threshold;
    }

    public boolean started(){
        return startDate != null;
    }

    public void startNow(){
        startDate = new Date();
    }

    public void finishNow(){
        finishDate = new Date();
    }

    public boolean finished(){
        return startDate != null
                && finishDate != null
                && finishDate.after(startDate)
                && taskFinished();
    }

    public boolean taskFinished(){
        return task().finished(progress);
    }

    public Achievement achievement(){
        return Achievements.get().get(achievementId);
    }

    public Task task(){
        return achievement().tasks.get(taskId);
    }

    public void sendProgress(final Player player, final boolean star){
        player.sendf("[@blu@Task #%d@bla@%s] @blu@%,d@bla@ / @red@%,d@bla@ | @gre@%1.2f%%@bla@ Complete",
                taskId,
                (star ? "@red@*@bla@" : ""),
                progress,
                task().threshold, progressPercent());
    }

}
