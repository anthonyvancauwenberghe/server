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
			for (final Player p : World.getWorld().getPlayers()) {
				if (p.getCombat().getOpponent() != null) {
					if (p.getCombat().getOpponent().getEntity() instanceof Player) {
						if (p.getCombat().getOpponent().getPlayer().equals(p.getBountyHunter().getTarget())) {
							continue;
						}
					}
				}
				p.getBountyHunter().setPrevTarget(p.getBountyHunter().getTarget());
				p.getBountyHunter().setTarget(null);
				p.getActionSender().removeArrow();
			}
		}
		if (counter % 2 == 0) {
			for (final Player p : World.getWorld().getPlayers()) {
				if (!BountyHunter.applicable(p) && !BountyHunterLogout.isBlocked(p))
				    p.getBountyHunter().findTarget();
			}
		}
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null)
				continue;
			if (p.getBountyHunter().getTarget() == null) {
				p.getActionSender().sendString("@or1@Reset: @gre@" + (((counter + 1) % 2) + 1) + " @or1@min", 36503);
				continue;
			}
			p.sendMessage("Your target is at level " + p.getBountyHunter().getTarget().wildernessLevel + " wilderness.");
			p.getActionSender().sendString("@or1@Reset: @gre@" + counter + " @or1@min", 36503);
		}
	}
}
