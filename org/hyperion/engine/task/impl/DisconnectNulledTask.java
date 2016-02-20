package org.hyperion.engine.task.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class DisconnectNulledTask extends Task {

	public static final long DELAY = Time.ONE_MINUTE;

	public DisconnectNulledTask() {
		super(DELAY);
	}

	@Override
	public void execute() {
		long currentTime = System.currentTimeMillis();
		for(Player player : World.getPlayers()) {
			if(player != null) {
				if(player.isDisconnected() && currentTime - player.cE.lastHit >= 10000) {
					forceLogout(player);
					break;
				}
			}
		}
	}

	private static void forceLogout(Player player) {
		if(player == null) {
			return;
		}
		try {
			World.unregister(player);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
