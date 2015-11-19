package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.sql.Timestamp;
import java.util.Date;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.task.Task;

public class AchievementTaskProgress{

    public final int achievementId;
    public final int taskId;

    public int progress;

    public Timestamp startDate;
    public Timestamp finishDate;

    public AchievementTaskProgress(final int achievementId, final int taskId, final int progress, final Timestamp startDate, final Timestamp finishDate){
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
        progress += amount;
        if(progress > task().threshold)
            progress = task().threshold;
        return progress;
    }

    public double progressPercent(){
        return progress * 100d / task().threshold;
    }

    public boolean started(){
        return startDate != null;
    }

    public void startNow(){
        startDate = new Timestamp(System.currentTimeMillis());
    }

    public void finishNow(){
        finishDate = new Timestamp(System.currentTimeMillis());
    }

    public boolean finished(){
        return /*startDate != null
                && finishDate != null
                && finishDate.after(startDate)
                &&*/ taskFinished();
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
        player.sendf("@dre@Achievement progress%s - %,d/%,d %s",
                (star ? "@yel@*@bla@" : ""),
                progress,
                task().threshold,
                task().desc);
    }

}
