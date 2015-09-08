package org.hyperion.rs2.model.joshyachievementsv2.tracker;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.task.Task;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.BarrowsTripTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.BountyHunterKillTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.DungeoneeringFloorsTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.FightPitsTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.ItemOpenTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.KillForBountyTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.KillstreakTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.NpcKillTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.PickupItemTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.PlaceBountyTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.SkillItemTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.SlayerTask;
import org.hyperion.rs2.model.joshyachievementsv2.task.impl.VoteTask;

public class AchievementTracker{

    public static boolean active = false;

    private final Player player;
    private final Map<Integer, AchievementProgress> progress;

    public AchievementTracker(final Player player){
        this.player = player;
        
        progress = new TreeMap<>();
    }

    public void sendInfo(final Achievement a){
        final AchievementProgress p = progress(a.id);
        p.sendProgressHeader(player);
        a.instructions.instruct(player);
        p.sendAllTaskProgressHeaders(player);
    }

    public void sendInfo(final int achievementId){
        final Achievement achievement = Achievements.get().get(achievementId);
        if(achievement != null)
            sendInfo(achievement);
    }

    public void add(final AchievementProgress ap){
        progress.put(ap.achievementId, ap);
    }

    public Stream<AchievementTaskProgress> streamAvailableTaskProgress(){
        return progress.values().stream()
                .flatMap(AchievementProgress::streamAvailableProgress);
    }

    public AchievementProgress progress(final int achievementId){
        return progress.getOrDefault(achievementId, putAndGetProgress(achievementId));
    }

    private AchievementProgress putAndGetProgress(final int achievementId){
        final AchievementProgress p = new AchievementProgress(achievementId);
        add(p);
        return p;
    }

    public AchievementProgress progress(final Achievement achievement){
        return progress(achievement.id);
    }

    public AchievementTaskProgress taskProgress(final int achievementId, final int taskId){
        return progress(achievementId).progress(taskId);
    }

    public AchievementTaskProgress taskProgress(final Task task){
        return taskProgress(task.achievementId, task.id);
    }

    private boolean canDoTask(final Task task){
        return !taskProgress(task).finished()
                && (!task.hasPreTask() || taskProgress(task.preTask()).finished());
    }

    private Optional<Task> findAvailableTask(final Class<? extends Task> clazz, final Predicate<Task> pred, final int progress){
        return Achievements.get().streamTasks(clazz)
                .filter(pred.and(t -> t.canProgress(taskProgress(t).progress, progress))
                        .and(this::canDoTask)
                        .and(t -> t.constraints.constrained(player)))
                .min(Comparator.comparingInt(t -> t.threshold));
    }

    private Optional<Task> findAvailableTask(final Task.Filter filter, final int progress){
        return findAvailableTask(filter.clazz, filter, progress);
    }

    private void progress(final Task.Filter filter, final int progress){
        if(!active)
            return;
        findAvailableTask(filter, progress)
                .ifPresent(t -> progress(t, progress));
    }

    private void progress(final Task task, final int progress){
        final AchievementTaskProgress atp = taskProgress(task);
        if(atp.finished())
            return; //this shouldnt happen but just to be safe
        final AchievementProgress ap = progress(task.achievementId);
        if(!atp.started()){
            if(!ap.started())
                ap.startNow();
            atp.startNow();
        }
        atp.progress(progress);
        ap.sendProgressHeader(player);
        atp.sendProgress(player, true);
        if(atp.taskFinished()){
            atp.finishNow();
            player.sendf("[@blu@Achievement Task Complete@bla@] @red@%s@bla@! Congratulations!", task.desc);
            if(ap.tasksFinished()){
                ap.finishNow();
                player.sendf("[@blu@Achievement Complete@bla] @red@%s@bla@! Congratulations!", ap.achievement().title);
                ap.achievement().rewards.reward(player);
            }
        }
    }

    public void barrowsTrip(){
        progress(BarrowsTripTask.filter(), 1);
    }

    public void bountyHunterKill(){
        progress(BountyHunterKillTask.filter(), 1);
    }

    private void fightPits(final FightPitsTask.Result result){
        progress(FightPitsTask.filter(result), 1);
    }

    public void fightPitsWin(){
        fightPits(FightPitsTask.Result.WIN);
    }

    public void fightPitsLose(){
        fightPits(FightPitsTask.Result.LOSE);
    }

    public void itemOpened(final int itemId, final int quantity){
        progress(ItemOpenTask.filter(itemId), quantity);
    }

    public void itemOpened(final int itemId){
        itemOpened(itemId, 1);
    }

    public void bountyKill(final int bounty){
        progress(KillForBountyTask.filter(), bounty);
    }

    public void onKillstreak(final int killstreak){
        progress(KillstreakTask.filter(), killstreak);
    }

    public void npcKill(final int npcId){
        progress(NpcKillTask.filter(npcId), 1);
    }

    private void pickupItem(final PickupItemTask.From from, final int itemId, final int quantity){
        progress(PickupItemTask.filter(from, itemId), quantity);
    }

    public void pickupItemFromNpc(final int itemId, final int quantity){
        pickupItem(PickupItemTask.From.NPC, itemId, quantity);
    }

    public void pickupItemFromPlayer(final int itemId, final int quantity){
        pickupItem(PickupItemTask.From.PLAYER, itemId, quantity);
    }

    public void bountyPlaced(final int bounty){
        progress(PlaceBountyTask.filter(), bounty);
    }

    public void itemSkilled(final int skill, final int itemId, final int quantity){
        progress(SkillItemTask.filter(skill, itemId), quantity);
    }

    public void voted(final int times){
        progress(VoteTask.filter(), times);
    }

    public void voted(){
        voted(1);
    }

    public void slayerTaskCompleted(final int npcId){
        progress(SlayerTask.filter(npcId), 1);
    }

    public void dungFloorCompleted(final DungeoneeringFloorsTask.Difficulty difficulty, final DungeoneeringFloorsTask.Size size){
        progress(DungeoneeringFloorsTask.filter(difficulty, size), 1);
    }
}
