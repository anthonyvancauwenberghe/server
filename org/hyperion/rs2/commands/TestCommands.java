package org.hyperion.rs2.commands;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class TestCommands {

	public static void init() {
		if(!Server.NAME.equalsIgnoreCase("ArteroBeta"))
			return;

		CommandHandler.submit(new Command("givedp") {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				try {
					player.getPoints().increaseDonatorPoints(Integer.parseInt(filterInput(input)));
					player.getActionSender().sendMessage("You give yourself some donator points.");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use as ::givedp AMOUNT.");
					return false;
				}
				return true;
			}
		});

		CommandHandler.submit(new Command("givehp") {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				try {
					player.getPoints().setHonorPoints(player.getPoints().getHonorPoints() + Integer.parseInt(filterInput(input)));
					player.getActionSender().sendMessage("You give yourself some honor points.");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use as ::givehp AMOUNT.");
					return false;
				}
				return true;
			}
		});

		CommandHandler.submit(new Command("givevp") {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				try {
					player.getPoints().setVotingPoints(player.getPoints().getVotingPoints() + Integer.parseInt(filterInput(input)));
					player.getActionSender().sendMessage("You give yourself some vote points.");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use as ::givevp AMOUNT.");
					return false;
				}
				return true;
			}
		});

		CommandHandler.submit(new Command("givepkp") {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				try {
					player.getPoints().setPkPoints(player.getPoints().getPkPoints() + Integer.parseInt(filterInput(input)));
					player.getActionSender().sendMessage("You give yourself some ArteroPK points.");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use as ::givepkp AMOUNT.");
					return false;
				}
				return true;
			}
		});
	}

}
