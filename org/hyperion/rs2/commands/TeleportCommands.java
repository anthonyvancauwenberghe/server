package org.hyperion.rs2.commands;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.misc2.Zanaris;

public class TeleportCommands {

	public static void init() {
		CommandHandler.submit(new Command("market", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, Location.create(3009, 3383, 0), false);
				return true;
			}
		});
        CommandHandler.submit(new Command("graves", Rank.PLAYER) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
				DialogueManager.openDialogue(player, 194);
                return true;
            }
        });
        CommandHandler.submit(new Command("wests", Rank.DEVELOPER) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
				DialogueManager.openDialogue(player, 196);
                return true;
            }
        });
		//arre's chillspot
		CommandHandler.submit(new Command("arreplace", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, Location.create(3207, 3219, 2), false);
				return true;
			}
		});

        CommandHandler.submit(new Command("moveloc", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(Player player, String input) {
                input = filterInput(input);
                final int offX = Integer.parseInt(input.split(" ")[0]);
                final int offY = Integer.parseInt(input.split(" ")[1]);
                player.setTeleportTarget(Location.create(player.getLocation().getX() + offX, player.getLocation().getY() + offY, player.getLocation().getZ()));
                return true;
            }
        });

		CommandHandler.submit(new Command("gdz", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, Location.create(3287, 3886, 0), false);
				return true;
			}
		});

		CommandHandler.submit(new Command("skilling", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, Location.create(3810, 2832, 0), false, false);
				return true;
			}
		});

        CommandHandler.submit(new Command("nathanplace", Rank.MODERATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                Magic.teleport(player, Location.create(2108, 4452, 3), false);
                return true;
            }
        });

		CommandHandler.submit(new Command("jail", Rank.HELPER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				String name = filterInput(input);
				Player victim = World.getPlayerByName(name);
				if(victim != null) {
					if(victim.getLocation().inPvPArea()) {
						player.getActionSender().sendMessage("You can't teleport people out of the wild!");
					} else if(victim.duelAttackable > 0) {
						player.getActionSender().sendMessage("You can't teleport people out of a duel!");
					} else if(Rank.isStaffMember(victim)) {
                        player.getActionSender().sendMessage("This command no longer works on staff members.");
                    } else if(Jail.inJail(victim)) {
						player.getActionSender().sendMessage("This player is already in jail.");
					} else
						victim.setTeleportTarget(Jail.LOCATION);
				} else {
					if(name.equalsIgnoreCase("jail")) {
                        if(player.duelAttackable > 0) {
                            player.getActionSender().sendMessage("This player is currently in a duel.");
                            return false;
                        }
						player.setTeleportTarget(Jail.LOCATION);
					} else
						player.getActionSender().sendMessage("This player is not online.");
				}
				return true;
			}
		});
		CommandHandler.submit(new Command("unjail", Rank.HELPER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				String name = filterInput(input);
				Player releasing = World.getPlayerByName(name);
				if(releasing != null) {
					if(Rank.isStaffMember(releasing)) {
						player.getActionSender().sendMessage("This command no longer works on staff members.");
					} else if(!Jail.inJail(releasing)) {
						player.getActionSender().sendMessage("This player is not in jail.");
					} else
						releasing.setTeleportTarget(Zanaris.LOCATION);
				} else {
					if(name.equalsIgnoreCase("unjail")) {
                        if(player.duelAttackable > 0) {
                            player.getActionSender().sendMessage("This player is currently in a duel.");
                            return false;
                        }
						player.setTeleportTarget(Zanaris.LOCATION);
					} else
						player.getActionSender().sendMessage("This player is not online.");
				}
				return true;
			}
		});
		CommandHandler.submit(new Command("xteletome", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				if(player.getLocation().inPvPArea() && !Rank.hasAbility(player, Rank.DEVELOPER))
					return false;
				if(player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER))
					return false;
				String name = filterInput(input);
				Player target = World.getPlayerByName(name);
				if(target != null) {
                    if(!Rank.hasAbility(player, Rank.DEVELOPER) && target.duelAttackable > 0) {
                        player.getActionSender().sendMessage("This player is currently in a duel.");
                        return false;
                    }
					if (!Rank.hasAbility(player, Rank.getPrimaryRank(target)) && Rank.isStaffMember(target)) {
						player.getActionSender().sendMessage("This player has a higher rank than you, you cannot teleport to them.");
                        target.getActionSender().sendMessage(player.getSafeDisplayName()+" tried teleporting you to them, you should ask them what they want.");
                        return false;
                    }
					target.setTeleportTarget(player.getLocation());
				} else {
					player.getActionSender().sendMessage("This player is not online.");
				}
				return true;
			}
		});
		CommandHandler.submit(new Command("xteleto", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				if(player.getLocation().inPvPArea() && !Rank.hasAbility(player, Rank.ADMINISTRATOR))
					return false;
                if(player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER))
                    return false;

				String name = filterInput(input);
				Player x = World.getPlayerByName(name);
				if(x != null) {
                    if(!Rank.hasAbility(player, Rank.DEVELOPER) && x.duelAttackable > 0) {
                        player.getActionSender().sendMessage("This player is currently in a duel.");
                        return false;
                    }
					player.setTeleportTarget(x.getLocation());
				} else {
					player.getActionSender().sendMessage(
							"This player is not online.");
				}
				return true;
			}
		});
	}

}
