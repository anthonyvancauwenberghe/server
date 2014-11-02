package org.hyperion.rs2.model.content.skill.slayer;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.content.ContentEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/2/14
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlayerTask {

    private final Player player;
    private int taskAmount, totalTasks, slayerPoints;
    /**
     * SlayerTask that they have, save the ordinal
     */
    private SlayerTasks task;

    public SlayerTask(final Player player) {
        this.player = player;
    }

    public int getTaskAmount() {
        return taskAmount;
    }

    public SlayerTasks getTask() {
        return task;
    }

    public boolean assignTask() {
        if(taskAmount > 0)
            return false;
        task = SlayerTasks.forLevel(player.getSkills().getRealLevels()[Skills.SLAYER]);
        taskAmount = task.getDifficulty().getAmount();
        player.sendf("You have been assigned %d %s to kill", taskAmount, task);
        return true;
    }

    public boolean killedTask(final int npcid) {
        if(task == null) return false;
        if(task.getIds().contains(npcid) && taskAmount > 0) {
            if(--taskAmount == 0) {
                totalTasks++;
                if(!handleTotalTasks()) {
                    slayerPoints += task.getDifficulty().getSlayerPoints();
                    player.sendf("You now have @red@%d@bla@ slayer points", slayerPoints);
                }
                player.sendf("You have completed %d tasks in a row", totalTasks);
            }
            ContentEntity.addSkillXP(player, task.getXP(), Skills.SLAYER);
            return true;
        }
        return false;
    }

    public boolean handleTotalTasks() {
        int pointsToAdd = 0;
        if(totalTasks%250 == 0)
            pointsToAdd = 180;
        else if(totalTasks%100 == 0)
            pointsToAdd = 100;
        else if(totalTasks %50 == 0)
            pointsToAdd = 50;
        else if(totalTasks%10 == 0)
            pointsToAdd = 20;
        if(pointsToAdd > 0) {
            slayerPoints += pointsToAdd;
            player.sendf("For completing your @red@%d@bla@th task in a row, you receive @red@%d@bla@ Slayer Points", taskAmount, pointsToAdd);
            return true;
        }
        return false;

    }

}
