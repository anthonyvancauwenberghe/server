package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;

public class AchievementProgress{

    public final int achievementId;
    private final Map<Integer, AchievementTaskProgress> progress;

    public AchievementProgress(final int achievementId){
        this.achievementId = achievementId;

        progress = new TreeMap<>();

        achievement().tasks.stream()
                .forEach(t -> add(new AchievementTaskProgress(achievementId, t.id)));
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

    public boolean finished(){
        return tasksFinished();
    }

    public boolean tasksFinished(){
        return progress.values().stream()
                    .allMatch(AchievementTaskProgress::finished);
    }

    public String progressColor(){
        if(finished())
            return "@gre@";
        else if(progress.values().stream().anyMatch(atp -> atp.started() || atp.progress > 0))
            return "@or1@";
        else
            return "@red@";
    }

    public String getTabString(){
        String color = "@red@";
        //System.out.println("progress size: " + progress.size());
        if(progress.values().stream().anyMatch(atp -> atp.started() || atp.progress > 0))
            color = "@or1@";
        if(finished())
            color = "@gre@";
        return color + (achievement().title.length() <= 26 ? achievement().title : achievement().title.substring(0, 25).trim() + "...");
    }

    public void sendProgressHeader(final Player player){
        player.sendf("@dre@Achievement progress: %s %1.2%", achievement().title, progress());
    }

    public void sendAllTaskProgressHeaders(final Player player){
        progress.values()
                .forEach(p -> p.sendProgress(player, false));
    }

    public double progressPercent(){
        return progress() * 100d / achievement().tasks.threshold;
    }

    public String info(){
        final String color = progressColor();
        final double percent = progressPercent();
        return String.format("%s%s @bla@| %s%1.2f%%", color, achievement().shortTitle, color, percent);
    }

    public boolean shouldSendInfoFor(final int taskId){
        final AchievementTaskProgress atp = progress(taskId);
        if(atp == null)
            return false;
        //was going to do some checks here..
        return true;
    }
}
