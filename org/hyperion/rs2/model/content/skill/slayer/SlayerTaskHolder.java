package org.hyperion.rs2.model.content.skill.slayer;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.content.ContentEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/2/14
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class SlayerTaskHolder {

    private int taskAmount, totalTasks, slayerPoints;
    /**
     * SlayerTaskHolder that they have, save the ordinal
     */
    private SlayerTask task;

    public SlayerTaskHolder(final Player player) {
    }

    public boolean assignTask(final int slayerLevel) {
        if(taskAmount > 0)
            return false;
        task = SlayerTask.forLevel(slayerLevel);
        taskAmount = task.getDifficulty().getAmount();
        return true;
    }

    public int killedTask(final int npcid) {
        if(task == null) return 0;
        if(task.getIds().contains(npcid) && taskAmount > 0) {
            if(--taskAmount == 0) {
                totalTasks++;
                slayerPoints += task.getDifficulty().getSlayerPoints() + handleTotalTasks();
            }
            return task.getXP();
        }
        return 0;
    }

    public int handleTotalTasks() {
        int pointsToAdd =0;
        if(totalTasks%250 == 0)
            pointsToAdd = 180;
        else if(totalTasks%100 == 0)
            pointsToAdd = 100;
        else if(totalTasks %50 == 0)
            pointsToAdd = 50;
        else if(totalTasks%10 == 0)
            pointsToAdd = 20;
        return pointsToAdd;

    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getTaskAmount() {
        return taskAmount;
    }

    public SlayerTask getTask() {
        return task;
    }

    public int getSlayerPoints() {
        return slayerPoints;
    }
}
