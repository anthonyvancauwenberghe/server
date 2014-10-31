package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.task.Task;

/**
 * A task which resets a player after an update cycle.
 *
 * @author Graham Edgecombe
 */
public class PlayerResetTask implements Task {

	/**
	 * The player to reset.
	 */
	private Player player;

	/**
	 * Creates a reset task.
	 *
	 * @param player The player to reset.
	 */
	public PlayerResetTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		if(player.getUpdateFlags().get(UpdateFlag.HIT_3)) {
			player.getUpdateFlags().reset();
			player.getDamage().setHit1(player.getDamage().getHit3());
			player.getUpdateFlags().flag(UpdateFlag.HIT);
		} else
			player.getUpdateFlags().reset();
		player.setTeleporting(false);
		//player.resetTeleportTarget();
		player.setMapRegionChanging(false);
		if(player.cE != null)
			player.cE.isDoingAtk = false;
		player.resetCachedUpdateBlock();
		player.reset();
	}

}
