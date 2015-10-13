package org.hyperion.rs2.commands;

import org.hyperion.Server;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.cluescroll.ClueScroll;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.skill.RandomEvent;
import org.hyperion.rs2.sql.event.impl.BetaServerEvent;
import org.hyperion.util.Misc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestCommands {

	public static boolean processBetaCommands(final Player player, String commandStart, String s, String withCaps, String[] as) throws IOException {
		if(!Server.NAME.equalsIgnoreCase("ArteroBeta"))
			return false;

		if (commandStart.equalsIgnoreCase("givedp")) {
			try {
				final int amount = Integer.parseInt(s.substring(7).trim());
				final int limit = Math.max(85000, player.getPoints().getDonatorPoints() + amount);
				player.getPoints().setDonatorPoints(limit);
				player.getActionSender().sendMessage("You give yourself some donator points.");
			} catch(Exception e) {
				player.getActionSender().sendMessage("Use as ::givedp AMOUNT.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("givehp")) {
			try {
				player.getPoints().setHonorPoints(player.getPoints().getHonorPoints() + Integer.parseInt(s.substring(7).trim()));
				player.getActionSender().sendMessage("You give yourself some honor points.");
			} catch(Exception e) {
				player.getActionSender().sendMessage("Use as ::givehp AMOUNT.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("givevp")) {
			try {
				player.getPoints().setVotingPoints(player.getPoints().getVotingPoints() + Integer.parseInt(s.substring(7).trim()));
				player.getActionSender().sendMessage("You give yourself some voting points.");
			} catch(Exception e) {
				player.getActionSender().sendMessage("Use as ::givevp AMOUNT.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("givepkp")) {
			try {
				player.getPoints().setPkPoints(player.getPoints().getPkPoints() + Integer.parseInt(s.substring(8).trim()));
				player.getActionSender().sendMessage("You give yourself some ArteroPk points.");
			} catch(Exception e) {
				player.getActionSender().sendMessage("Use as ::givepkp AMOUNT.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("giveclues")) {
			for (ClueScroll clue : ClueScrollManager.getAll())
				player.getBank().add(new BankItem(0, clue.getId(), 1));
			player.sendMessage("All the clue scrolls have been added to your bank.");
			return true;
		}

		if (commandStart.equalsIgnoreCase("setlevel")) {
			try {
				String[] args = s.substring(9).trim().split(",");
				int skill = Integer.parseInt(args[0]);
				int level = Integer.parseInt(args[1]);
				player.getSkills().setLevel(skill, level);
				if (level <= 99) {
					player.getSkills().setExperience(skill, Skills.getXPForLevel(level) + 5);
				}
			} catch (Exception e) {
				player.sendMessage("Use as ::setlevel SKILL,LEVEL");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("setstreak")) {
			try {
				player.getPermExtraData().put("votingStreak", Integer.parseInt(s.substring(10).trim()));
				player.sendMessage("Successfully set your streak to " + Integer.parseInt(s.substring(10).trim()) + ".");
			} catch (Exception e) {
				player.sendMessage("Use as ::setstreak AMOUNT");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("vote")) {
			try {

				World.getWorld().getLogsConnection().query(
						String.format("INSERT INTO `server`.`waitingvotes` " +
										"(`index`, `fakeUsername`, `realUsername`, `runelocus`, `topg`, `rspslist`, " +
										"`runelocusProcessed`, `topgProcessed`, `rspslistProcessed`, `processed`, `timestamp`) " +
										"VALUES (NULL, '%s', '%s', '1', '1', '1', '0', '0', '0', '0', CURRENT_TIMESTAMP);",
								player.getName(), player.getName())
				);

				player.sendMessage("Successfully added a vote to the database.");
			} catch (Exception e) {
				player.sendMessage("Something went wrong, please contact Glis about this.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("resetvote")) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				String yesterday =  new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
				player.getPermExtraData().put("lastVoted", yesterday);
				player.sendMessage("Successfully set your last voted date to yesterday.");
			} catch (Exception e) {
				player.sendMessage("Something went wrong, please contact Glis about this.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("beta")) {
			player.getActionSender().betaChanges();
			return true;
		}

		if (commandStart.equalsIgnoreCase("home")) {
			player.setTeleportTarget(Edgeville.LOCATION);
			return true;
		}

		if (commandStart.equalsIgnoreCase("bank")) {
			Bank.open(player, false);
			return true;
		}

		if (commandStart.equalsIgnoreCase("whitelistadd") && (player.getName().equalsIgnoreCase("nigga") || player.getName().equalsIgnoreCase("Glis") || player.getName().equalsIgnoreCase("Seven"))) {
			try {
				World.getWorld().getLogsConnection().query(String.format("INSERT INTO server.whitelist (name) VALUES ('%s')", s.substring(13).trim()));
				BetaServerEvent.whitelist.add(s.substring(13).trim());
				player.sendMessage(Misc.formatPlayerName(s.substring(13).trim()) + " has been successfully added to the whitelist.");
			} catch(Exception e) {
				player.sendMessage("Use as ::whitelistadd NAME.");
			}
			return true;
		}

		if (commandStart.equalsIgnoreCase("whitelistremove") && (player.getName().equalsIgnoreCase("nigga") || player.getName().equalsIgnoreCase("Glis") || player.getName().equalsIgnoreCase("Seven"))) {
			try {
				World.getWorld().getLogsConnection().query(String.format("DELETE FROM `server`.`whitelist` WHERE whitelist.name = '%s'", s.substring(15).trim()));
				BetaServerEvent.whitelist.remove(s.substring(15).trim());
				player.sendMessage(Misc.formatPlayerName(s.substring(15).trim()) + " has been successfully removed from the whitelist.");
			} catch(Exception e) {
				player.sendMessage("Use as ::whitelistremove NAME.");
			}
			return true;
		}
		if (commandStart.equalsIgnoreCase("tele")) {
			if (as.length == 3 || as.length == 4) {
				int l = Integer.parseInt(as[1]);
				int k3 = Integer.parseInt(as[2]);
				int j5 = player.getLocation().getZ();
				if (as.length == 4) {
					j5 = Integer.parseInt(as[3]);
				}
				if (player.duelAttackable > 0) {player.getActionSender().sendMessage("you cannot teleport out of a duel.");
				}
				player.setTeleportTarget(Location.create(l, k3, j5));
			} else {
				player.getActionSender().sendMessage("Syntax is ::tele [x] [y] [z].");
			}
			return true;
		}
		if (commandStart.equalsIgnoreCase("item")) {
				String input = s.substring(5).trim();
				final String[] parts = input.split(",");
				String targetName = player.getName();
				int itemId;
				int quantity = 1;
				switch(parts.length){
					case 1:
						itemId = Integer.parseInt(parts[0].trim());
						break;
					case 2:
						itemId = Integer.parseInt(parts[0].trim());
						quantity = Integer.parseInt(parts[1].trim());
						break;
					case 3:
						targetName = parts[0].trim();
						itemId = Integer.parseInt(parts[1].trim());
						quantity = Integer.parseInt(parts[2].trim());
						break;
					default:
						return true;
				}
				final Player target = World.getWorld().getPlayer(targetName);
				if(target == null){
					player.sendf("Error finding %s", targetName);
					return true;
				}
				target.getInventory().add(Item.create(itemId, quantity));
				player.sendf("Added %s x %,d to %s's inventory", ItemDefinition.forId(itemId).getName(), quantity, target.getSafeDisplayName());
				return true;
			}
		if (commandStart.equalsIgnoreCase("whitelist")) {
			player.getActionSender().showWhitelist();
		}

		if(commandStart.equalsIgnoreCase("finishclue")) {
			try {
				final ClueScroll clue = ClueScrollManager.getInInventory(player);
				if (clue != null)
					clue.apply(player);
				return true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		if(commandStart.equalsIgnoreCase("triggerrandom")) {
			RandomEvent.triggerRandom(player, false);
		}

		if(commandStart.equalsIgnoreCase("polls")) {
			player.getPoll().openInterface();
		}

		return false;
	}

}
