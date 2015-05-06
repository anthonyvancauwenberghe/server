package org.hyperion.rs2.model.content.bounty;

import java.util.List;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.PlayerCombatEvent;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

public class BountyHunterEvent extends Event{

	public BountyHunterEvent() {
		super(Time.ONE_MINUTE);
	}
	
	private int counter = 7;
	
	public void execute() {
		counter--;
		if(counter == 0) {
			for(final Player p : World.getWorld().getPlayers()) {
					p.getBountyHunter().setTarget(null);
					p.getActionSender().removeArrow();
			}
		}
		if(counter%2 == 0) {
			final List<Player> list = PlayerCombatEvent.cloneEntityList();
			for(final Player p : list) {
				if(BountyHunter.applicable(p))
					p.getBountyHunter().findTarget();
			}
		}
		if(counter == 0)
			counter = 11;
		for(final Player p : World.getWorld().getPlayers()) {
			p.getQuestTab().sendBHTarget();
			p.getActionSender().sendString("@or1@Target: @gre@"+counter+" @or1@min (@gre@"+(((counter+1)%2)+1)+"@or1@min)", 36503);
		}
	}
	
}
