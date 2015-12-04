package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.constraint.Constraint;
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

    public String progressColor(){
        if(finished())
            return "@gre@";
        else if(progress > 0)
            return "@or1@";
        else
            return "@red@";
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

    public String getShortDesc(){
        return (task().desc.length() <= 26 ? task().desc : task().desc.substring(0, 25).trim() + "...");
    }

    public void sendProgress(final Player player, final boolean star){
        player.sendf("@dre@Achievement progress%s - %,d/%,d %s",
                (star ? "@yel@*@bla@" : ""),
                progress,
                task().threshold,
                getShortDesc());
    }

    public List<String> info(final Player player){
        final List<String> info = new ArrayList<>();
        final Task task = task();
        final String color = progressColor();
        final double percent = progressPercent();
        final boolean finished = taskFinished();
        info.add(String.format("Task #%d: %s%s @bla@| %s%,d/%,d @bla@| %s%1.2f%%", task.number, color, task.shortDesc(), color, progress, task.threshold, color, percent));
        if(startDate != null)
            info.add(String.format("Started: @blu@%s @bla@| Finished: %s", startDate, finishDate != null ? "@blu@"+finishDate : "---"));
        if(!finished){
            for(final Constraint c : task.constraints.list)
                info.add(String.format(" %s- @bla@%s", c.constrainedColor(player), c.shortDesc()));
            if(task.hasPreTask())
                info.add(String.format("Depends on Task #%d", task.preTask().number));
        }
        return info;
    }

}
