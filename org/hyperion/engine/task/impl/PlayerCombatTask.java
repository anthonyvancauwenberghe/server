package org.hyperion.engine.task.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Container;

public class PlayerCombatTask extends Task {
	
	public PlayerCombatTask() {
		super(600L);
	}
	
	public synchronized static Player[] cloneEntityList() {
		return World.getPlayers().stream().toArray(Player[]::new);
	}
	
	@Override
	public void execute() {
        final long startTime = System.currentTimeMillis();
		Player[] players = cloneEntityList();
        final int size = players.length;
        final int startPoint = Combat.random(size - 1);

	}

    public static boolean stakeReset(final Player player) {
        final Player opp = player.getTrader();
        if(opp != null && !opp.isDead() && !player.isDead() && !opp.getSession().isConnected() && !player.getSession().isConnected() && player.duelAttackable > 0 && opp.duelAttackable > 0) {
            FileLogging.savePlayerLog(opp, "Duel TIE against "+player.getName());
            FileLogging.savePlayerLog(player, " Duel TIE against " + opp.getName());
            Container.transfer(player.getDuel(), player.getInventory());//jet is a smartie
            Container.transfer(opp.getDuel(), opp.getInventory());
            opp.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            player.setTeleportTarget(Location.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
            return true;
        }
        return false;
    }

}
