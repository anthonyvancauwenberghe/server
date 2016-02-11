package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;


/**
 * @author SaosinHax/Linus/Vegas/Flux/Tinderbox/Jack Daniels/Arsen/Jolt <- All same person
 */

public class EPEvent extends Event {

	/**
	 * The delay in milliseconds between consecutive facing.
	 */
	public static final long CYCLETIME = Time.ONE_MINUTE;

	/**
	 * Creates the Bankers facing event each second.
	 */
	public EPEvent() {
		super(CYCLETIME);
	}

	@Override
	public void execute() {
		for(Player p : World.getPlayers()) {
			if(p.getLocation().inPvPArea()) {
				if(System.currentTimeMillis() - p.getLastEPIncrease() > Time.ONE_HOUR) {
					p.increaseEP();
					continue;
				}
				int bonus = (p.wildernessLevel / 20) + 1;
				if(p.cE.getOpponent() != null)
					bonus *= 2;
				int risk = p.getRisk();
				if(risk > 50000)
					risk = 50000;
				bonus += risk / 5000;
				if(Misc.random(30 / bonus) == 1) {
					p.increaseEP();
				}
			}
		}
	}

	static {
		CommandHandler.submit(new Command("maxep", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				for(int i = 0; i < 10; i++) {
					player.increaseEP();
				}
				return true;
			}
		});
	}

}
