package org.hyperion.rs2.model.content.pvptasks;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Combat;

public final class TaskHandler {
    public static void checkTask(final Player killer, final Player victim) {
        if(killer != null && killer.getPvPTask() != null && victim != null){
            if(killer.debug)
                killer.getActionSender().sendMessage("killed da dude pvp task registr!");
            if(killer.getPvPTask().isTask(killer, victim)){
                killer.decrementPvPTask(1);
                if(killer.getPvPTaskAmount() <= 0){
                    handleTaskEnd(killer);
                }else
                    handleReward(killer);
            }
        }
    }

    public static void assignTask(final Player player) {
        DialogueManager.openDialogue(player, 140);
    }

    public static void handleReward(final Player p) {
        final int pointsReceived;
        p.getPoints().increaseDonatorPoints(pointsReceived = (1 + Combat.random(2)), false);
        //p.getActionSender().sendMessage("@bla@You have just killed your PvP Task and receive @gre@"+pointsReceived+"@bla@ Donator Points!");
        p.getActionSender().sendMessage("@red@You have " + p.getPvPTaskAmount() + " " + PvPTask.toString(p.getPvPTask()) + "s left to kill!");
    }

    public static void handleTaskEnd(final Player killer) {
        killer.getActionSender().sendMessage("@red@You have just completed your PvP Task!");
        killer.getPoints().increasePkPoints(75 + Combat.random(30));
        killer.setPvPTask(null);
        killer.setPvPTaskAmount(0);
    }
}
