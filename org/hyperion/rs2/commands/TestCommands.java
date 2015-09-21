package org.hyperion.rs2.commands;

import org.hyperion.Server;
import org.hyperion.rs2.model.Player;

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

		CommandHandler.submit(new Command("setlevel") {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				try {
					String[] args = input.substring(9).trim().split(",");
					int skill = Integer.parseInt(args[0]);
					int level = Integer.parseInt(args[1]);
						player.getSkills().setLevel(skill, level);
						if (level <= 99) {
							player.getSkills().setExperience(skill, player.getSkills().getXPForLevel(level) + 5);
						}
					return true;
				} catch (Exception e) {
					player.sendMessage("Use as ::setlevel SKILL,LEVEL");
					return false;
				}
			}
		});
	}

}
