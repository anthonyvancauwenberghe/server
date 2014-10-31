package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.EntityList;
import org.hyperion.util.Misc;

public class RandomTeleportEvent extends Event {

	public static final int DELAY = 3000;

	public static final int TELEPORTS_AMOUNT = 20;

	private Player player;

	public RandomTeleportEvent(Player player) {
		super(DELAY);
		this.player = player;
	}

	private int counter = 0;

	@Override
	public void execute() {
		Player randomPlayer = null;
		EntityList<Player> list = World.getWorld().getPlayers();
		while(randomPlayer == null) {
			int randIndex = Misc.random(list.size() - 1);
			randomPlayer = (Player) list.get(randIndex);
		}
		player.setTeleportTarget(randomPlayer.getLocation());
		counter++;
		if(counter >= TELEPORTS_AMOUNT) {
			this.stop();
		}
	}


}
