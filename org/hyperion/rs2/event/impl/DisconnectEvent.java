package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class DisconnectEvent extends Event {

	public static final long DELAY = 1000;

	public DisconnectEvent() {
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

	private static String forceLogout(Player glitcher) {
		if(glitcher == null) {
			return "That player is offline";
		}
		try {
			World.unregister(glitcher);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Player forced logout: " + glitcher.getName());
		return "Forced logout succesful.";
	}

	static {
		CommandHandler.submit(new Command("forcelogout", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				String name = input.replaceAll("forcelogout ", "");
				Player glitcher = World.getPlayerByName(name);
				player.getActionSender().sendMessage(forceLogout(glitcher));
				return true;
			}
		});
	}

}
