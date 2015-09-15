package org.hyperion.rs2.model.content.bounty;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.PlayerCombatEvent;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class BountyHunterEvent extends Event{

	public BountyHunterEvent() {
		super(Time.ONE_MINUTE);
	}
	
	private int counter = 5;
	
	public void execute() {
		counter--;
		if (counter == 0) {
			counter = 5;
			//Checks if the player is in combat with his target, if not he'll reset
			for (final Player p : World.getWorld().getPlayers()) {
				if (p.getCombat().getOpponent() != null) {
					if (p.getCombat().getOpponent().getEntity() instanceof Player) {
						if (p.getCombat().getOpponent().getPlayer().equals(p.getBountyHunter().getTarget())) {
							continue;
						}
					}
				}
				//This clears the target, which makes the player applicable again
				p.getBountyHunter().clearTarget();
			}
		}
		if (counter % 2 == 0) {
			for (final Player p : World.getWorld().getPlayers()) {
				//if the player has a target, but the target is not in the wilderness anymore
				if(!p.getBountyHunter().applicable2(p.getBountyHunter().getTarget()))
					p.getBountyHunter().clearTarget();

				if (BountyHunter.applicable(p))
					p.getBountyHunter().findTarget();
			}
		}
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null)
				continue;
			//This means the player has no target
			if (p.getBountyHunter().getTarget() == null) {
				p.getActionSender().sendString("@or1@Reset: @gre@" + (((counter + 1) % 2) + 1) + " @or1@min", 36503);
				continue;
			}
			//This will happen if the player or his target get out of the wilderness
			if (!p.getBountyHunter().applicable2(p.getBountyHunter().getTarget()) || !p.getBountyHunter().applicable2(p)) {
				p.getActionSender().sendString("@or1@Reset: @gre@" + (((counter + 1) % 2) + 1) + " @or1@min", 36503);
				continue;
			}
			//If neither happened, it will assume normal scenario
			p.sendMessage("Your target is at level " + p.getBountyHunter().getTarget().wildernessLevel + " wilderness.");
			p.getActionSender().sendString("@or1@Reset: @gre@" + counter + " @or1@min", 36503);
		}
	}
}
