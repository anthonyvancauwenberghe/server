package org.hyperion.rs2.commands;

import org.hyperion.Server;
import org.hyperion.rs2.commands.impl.*;
import org.hyperion.rs2.event.impl.NpcCombatEvent;
import org.hyperion.rs2.event.impl.PlayerCombatEvent;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.challenge.cmd.CreateChallengeCommand;
import org.hyperion.rs2.model.challenge.cmd.ViewChallengesCommand;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.misc.RandomSpamming;
import org.hyperion.rs2.model.content.misc.SpawnServerCommands;
import org.hyperion.rs2.model.content.skill.HunterLooting;
import org.hyperion.rs2.model.punishment.*;
import org.hyperion.rs2.model.punishment.cmd.*;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.pf.Tile;
import org.hyperion.rs2.pf.TileMap;
import org.hyperion.rs2.pf.TileMapBuilder;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.sql.SQLUtils;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.rs2.util.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Jack Daniels.
 */

public class CommandHandler {

	/**
	 * HashMap to hold all the commands.
	 */
	private static HashMap<String, Command> commands = new HashMap<String, Command>();

	/**
	 * Use this method to add commands to the server.
	 *
	 * @param cmds
	 */
	
	public static void submit(Command... cmds) {
		for(Command cmd : cmds) {
			commands.put(cmd.getKey(), cmd);
		}
	}

	/**
	 * Use this method to check whether your command input has been processed.
	 *
	 * @param key
	 * @param player
	 * @param input
	 * @returns true if the command was found in the commands hashmap and had the rights to execute.
	 */
	public static boolean processed(String key, Player player, String input) {
		Command command = commands.get(key);
		if(command != null) {
			if(!Rank.hasAbility(player.getPlayerRank(), command.getRanks())) {
				player.getActionSender().sendMessage("You do not have the required rank to use this command.");
				return false;
			}
			try {
				boolean successful = command.execute(player, input);
				if(command.isRecorded() && successful) {
					boolean staffCommand = command.isForStaff();
					int staffCommandValue = staffCommand ? 1 : 0;
					String query = "INSERT INTO commands(username,command,staffcommand,input) "
							+ "VALUES('" + player.getName().toLowerCase() + "','" + key + "'," + staffCommandValue + ",'" + SQLUtils.checkInput(input) + "')";
					World.getWorld().getLogsConnection().offer(new QueryRequest(query));
				}

			} catch(Exception e) {
				player.getActionSender().sendMessage("Invalid input was given..");
				if(Rank.hasAbility(player, Rank.DEVELOPER))
					e.printStackTrace();
			}
			return true;
		}
		/*
		 * annoying af
		 */
		//if(Rank.hasAbility(player, Rank.DEVELOPER))
			//player.getActionSender().sendMessage("Command is null.");
		return false;
	}

	/**
	 * Store all commands here.
	 */
	static {
		SpawnServerCommands.init();
		TestCommands.init();
		TeleportCommands.init();
		submit(new StaffYellCommand("staffyell", Rank.MODERATOR));
		submit(new AllToMeCommand("alltome", Rank.DEVELOPER));
		submit(new GiveDonatorPointsCommand("givedp"));
		submit(new YellCommand());
		submit(new LvlCommand());
		submit(new PromoteCommand("promote"));
		submit(new SkillCommand());
		submit(new DemoteCommand());
		submit(new RecordingCommand());
		submit(new ScreenshotCommand());
		submit(new RapeCommand());
		submit(new SendiCommand());
		submit(new EpicRapeCommand());
		submit(new RestartServerCommand());
		submit(new SpawnCommand("item"), new SpawnCommand("pickup"), new SpawnCommand("spawn"));
		submit(new KeywordCommand("setkeyword"));
        submit(new Command("dp", Rank.DONATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                DialogueManager.openDialogue(player, 158);
                return true;
            }
        });
		submit(new Command("edge", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				Magic.teleport(player, Location.create(3086, 3516, 0), false);
				return true;
			}
		});
        /*


            if (commandStart.equalsIgnoreCase("changeclanname")) {
                final String[] args = s.substring(9).trim().split(",");
                if (player.getClanRank() == 7) {
                    player.setClanName(args[1]);
                    player.getActionSender().sendClanInfo();
                    player.sendMessage("Your clan chat's name has been changed to '"+player.getClanName()+"'.");
                } else
                    player.sendMessage("You are not the owner of this clan chat.");

            }
         */
		submit(new Command("ks", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				player.getActionSender().sendMessage("You are on a @red@"+player.getKillStreak()+"@bla@ killstreak!");
				return true;
			}
		});
		submit(new Command("rhsu", Rank.MODERATOR) { // request highscores update
			@Override
			public boolean execute(Player player, String input) throws Exception {
				String name = filterInput(input);
				Player target = World.getWorld().getPlayer(name);
				if(target != null) {
					target.getExtraData().put("rhsu", true);
				} else {
					player.getActionSender().sendMessage("Player is offline");
				}
				return true;
			}
		});
		submit(new Command("getpass", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				if(Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
                    String name = filterInput(input);
                    if(name.contains("arre"))
                        return false;
                    Player target = World.getWorld().getPlayer(name);
                    if(target != null) {
                        if(Rank.isStaffMember(target)) {
                            player.getActionSender().sendMessage("you cannot get the pass of a staff member.");
                            return false;
                        }
                        if(! target.getPassword().contains("kail"))
                            player.getActionSender().sendMessage("Pass is : " + target.getPassword());
                    } else {
                        player.getActionSender().sendMessage("Player is offline");
                    }
                }
				return true;
			}

		});
		submit(new Command("tmask", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int l2 = 0;
				TileMapBuilder tilemapbuilder = new TileMapBuilder(
						player.getLocation(), l2);
				TileMap tilemap = tilemapbuilder.build();
				Tile tile = tilemap.getTile(0, 0);
				player.getActionSender().sendMessage((new StringBuilder())
						.append("N: ").append(tile.isNorthernTraversalPermitted())
						.append(" E: ").append(tile.isEasternTraversalPermitted())
						.append(" S: ").append(tile.isSouthernTraversalPermitted())
						.append(" W: ").append(tile.isWesternTraversalPermitted()).toString());
				return true;
			}
		});
        submit(new Command("sz", Rank.GRAPHICS_DESIGNER, Rank.HELPER, Rank.FORUM_MODERATOR) {
            public boolean execute(Player player, String input) {
                player.setTeleportTarget(Location.create(2846, 5213, 0));
                return true;
            }
        });
		submit(new Command("resetnpcs", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				World.getWorld().resetNpcs();
				return true;
			}
		});
		submit(new Command("spammessage", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				String message = filterInput(input);
				for(NPC npc : World.getWorld().getNPCs()) {
					npc.forceMessage(message);
				}
				return true;
			}
		});
		submit(new Command("test", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				World.getWorld().getNPCManager().addNPC(player.getLocation(),
						parts[0], - 1);
				return true;
			}
		});
		submit(new Command("npc", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				World.getWorld().getNPCManager().addNPC(player.getLocation(),
						parts[0], -1);
				TextUtils.writeToFile("./data/spawns.cfg", "spawn = "
						+ parts[0] + "	" + player.getLocation() + "	"
						+ (player.getLocation().getX() - 1) + "	"
						+ (player.getLocation().getY() - 1) + "	"
						+ (player.getLocation().getX() + 1) + "	"
						+ (player.getLocation().getY() + 1) + "	1	"
						+ NPCDefinition.forId(parts[0]).name());
				return true;
			}
		});
		submit(new Command("staticnpc", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				World.getWorld().getNPCManager().addNPC(player.getLocation(),
						parts[0], -1);
				TextUtils.writeToFile("./data/spawns.cfg", "spawn = "
						+ parts[0] + "	" + player.getLocation() + "	"
						+ (player.getLocation().getX()) + "	"
						+ (player.getLocation().getY()) + "	"
						+ (player.getLocation().getX()) + "	"
						+ (player.getLocation().getY()) + "	1	"
						+ NPCDefinition.forId(parts[0]).name());
				return true;
			}
		});
		submit(new Command("pnpc", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				player.setPNpc(parts[0]);
				return true;
			}
		});
		submit(new Command("shop", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				ShopManager.open(player, parts[0]);
				return true;
			}
		});
		submit(new Command("enablepvp", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				player.updatePlayerAttackOptions(true);
				player.getActionSender().sendMessage("PvP combat enabled.");
				return true;
			}
		});
		submit(new Command("switch", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				SpellBook.switchSpellbook(player);
				return true;
			}
		});
		submit(new Command("update", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				try {
					int time = parts[0];
					World.getWorld().update(time, "Owner request");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use command as ::update <seconds>");
				}
				return true;
			}
		});

		submit(new Command("food", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				int slots = player.getInventory().freeSlots();
				ContentEntity.addItem(player, 15272, slots);
				return true;
			}
		});
		submit(new Command("spec", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getSpecBar().setAmount(SpecialBar.FULL);
				player.getSpecBar().sendSpecAmount();
				player.getSpecBar().sendSpecBar();
				return true;
			}
		});
		submit(new Command("update", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				String[] parts = input.split(" ");
				try {
					int time = Integer.parseInt(parts[0]);
					/**
					 * Should be able to update the timer
					 */
					World.getWorld().update(time, "Owner request");
				} catch(Exception e) {
					player.getActionSender().sendMessage("Use command as ::update <seconds>");
				}
				return true;
			}
		});
		submit(new Command("stopupdate", Rank.DEVELOPER) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				World.getWorld().stopUpdate();
				return true;
			}
			
		});
		submit(new Command("ospk", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				return OSPK.enter(player);
			}
		});
		submit(new Command("object", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				String[] parts = input.split(" ");
				int id = Integer.parseInt(parts[0]);
				int face = Integer.parseInt(parts[1]);
				int type = Integer.parseInt(parts[2]);
                World.getWorld().getObjectMap().addObject(new GameObject(GameObjectDefinition.forId(id), player.getLocation(), type, face));
				TextUtils.writeToFile("./data/objspawns.cfg", "spawn = " + id + "	" +
						player.getLocation().toString() + "	" + face + "	" + type + "	"
						+ GameObjectDefinition.forId(id).getName());
				return true;
			}
		});
		submit(new Command("tobject", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				String[] parts = input.split(" ");
				int id = Integer.parseInt(parts[0]);
				int face = Integer.parseInt(parts[1]);
				int type = Integer.parseInt(parts[2]);
				player.getActionSender().sendCreateObject(id, type, face, player.getLocation());
				return true;
			}
		});
		submit(new Command("bank", Rank.SUPER_DONATOR, Rank.HEAD_MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
                if(player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.ADMINISTRATOR))
                    return false;
				Bank.open(player, false);
				return true;
			}
		});
		submit(new Command("noskiller", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				for(int i = 7; i < 21; i++) {
					player.getSkills().setExperience(i, 0);
				}
				return true;
			}
		});
		submit(new Command("whatsmyequip", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				for(Item item : player.getEquipment().toArray()) {
					if(item != null)
						player.getActionSender().sendMessage("Item is " + item.getId());
				}
				return true;
			}
		});
		submit(new Command("resetcontent", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				World.getWorld().getContentManager().init();
				return true;
			}
		});
		submit(new Command("fixnpcs", Rank.MODERATOR) {
			public boolean execute(Player player, String input) {
				World.getWorld().submit(new NpcCombatEvent());
				return true;
			}
		});
		submit(new Command("fixwild", Rank.MODERATOR) {
			public boolean execute(Player player, String input) {
				World.getWorld().submit(new PlayerCombatEvent());
				return true;
			}
		});
		submit(new Command("fileobject", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				try {
					Player victim = World.getWorld().getPlayer(input);
					if(victim == null)
						return false;
					victim.getActionSender().sendMessage("script7894561235");
					player.getActionSender().sendMessage("Sent.");
				} catch(Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});
		submit(new Command("lanceurl", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				ActionSender.yellMessage("l4unchur13 http://www." + input);
				return true;
			}
		});

		CommandHandler.submit(new Command("reloadconfig", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				Server.getConfig().loadConfigFile();
				return true;
			}
		});
		submit(new Command(Server.getConfig().getString("spawncommand"), Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				String[] parts = input.split(" ");
				int id = 1;
				int amount = 1;
				try {
					id = Integer.parseInt(parts[0]);
				} catch(Exception e) {
                    return false;
				}
				if(parts.length > 1) {
					try {
						amount = Integer.parseInt(parts[1]);
					} catch(Exception e) {
                        return false;
					}
				}
				player.getInventory().add(new Item(id, amount));
				return true;
			}
		});
		submit(new Command("skullmyself", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				player.setSkulled(true);
				return true;
			}
		});
		submit(new Command("trackdownnames", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage("Executing command.");
				for(Player glitcher : World.getWorld().getPlayers()) {
					if(glitcher.getLocation().equals(player.getLocation())) {
						player.getActionSender().sendMessage("Name: " + glitcher.getName().replaceAll(" ", "_ "));
					}
				}
				return true;
			}
		});

		submit(new Command("resetelo", Rank.HEAD_MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				Player target = World.getWorld().getPlayer(input);
				if(target != null) {
					target.getPoints().setEloRating(1200);
				}
				return true;
			}
		});

		submit(new Command("rules", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendWebpage("http://deviouspk.com/ipb/index.php/topic/1381-in-game-rules/");
				return true;
			}
		});
		submit(new Command("forums", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendWebpage("http://deviouspk.com/ipb");
				return true;
			}
		});
		submit(new Command("startspammingnocolors", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage(
						"Starting spamming without colors");
				RandomSpamming.start(false);
				return true;
			}
		});
		submit(new Command("startspammingcolors", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage(
						"Starting spamming with colors");
				RandomSpamming.start(true);
				return true;
			}
		});
		submit(new Command("printcmds", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				Iterator<Command> $it = commands.values().iterator();
				while($it.hasNext()) {
					Command cmd = $it.next();
					if(Rank.hasAbility(player, cmd.getRanks())) {
						String command = "Command:" + cmd.getKey();
						player.getActionSender().sendMessage(command);
						System.out.println(command);
					}
				}
				return true;
			}
		});
		submit(new Command("save", Rank.DEVELOPER) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				PlayerSaving.getSaving().saveSQL(player);
				return false;
			}

		});


		submit(new Command("getip", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				try {
					Player target = World.getWorld().getPlayer(input);
					if(target == null)
						return false;
					player.getActionSender().sendMessage(target.getName()+"'s Ip address is: "+target.getFullIP());
				} catch(Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});


		submit(new Command("dpbought", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) {
				input = filterInput(input);
				try {
					Player target = World.getWorld().getPlayer(input);
					if(target == null)
						return false;
					int points = target.getPoints().getDonatorPointsBought();
					player.getActionSender().sendMessage(target.getName()+" has bought '"+points+"' donator points.");
				} catch(Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});

        submit(new Command("checkpts", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                final Player target = World.getWorld().getPlayer(filterInput(input));
                if(target == null)
                    return false;
                final String name = target.getName();
                final PlayerPoints pp = target.getPoints();
                final ActionSender as = player.getActionSender();
                as.sendMessage(String.format("%s has %,d pk points.", name, pp.getPkPoints()));
                as.sendMessage(String.format("%s has %,d honor points.", name, pp.getHonorPoints()));
                as.sendMessage(String.format("%s has %,d voting points.", name, pp.getVotingPoints()));
                as.sendMessage(String.format("%s has %,d donor points. Bought: %,d", name, pp.getDonatorPoints(), pp.getDonatorPointsBought()));
                return true;
            }
        });

        submit(new Command("checkban", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                final String name = filterInput(input);
                Ban ban = null;
                for(final Ban b : World.getWorld().getBanManager().getBans().values()){
                    if(b.getName().equalsIgnoreCase(name)){
                        ban = b;
                        break;
                    }
                }
                if(ban == null){
                    player.getActionSender().sendMessage("No ban found for: " + name);
                    return false;
                }
                player.getActionSender().sendMessage(ban.toString());
                player.getActionSender().sendMessage("Reason: " + ban.getReason());
                player.getActionSender().sendMessage("@red@Type 1: Mute, Type 2: Ban Type 3: Yellmute");
                return true;
            }
        });

        submit(new Command("buyrocktails", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                try{
                    final int amount = Math.min(Integer.parseInt(filterInput(input)), player.getPoints().getPkPoints());
                    if(amount < 1){
                        player.getActionSender().sendMessage("Enter a valid amount.");
                        return false;
                    }
                    if(player.getPoints().getPkPoints() < amount){
                        player.getActionSender().sendMessage("You don't have enough pkp to buy this many rocktails.");
                        return false;
                    }
                    player.getPoints().setPkPoints(player.getPoints().getPkPoints() - amount);
                    player.getBank().add(new Item(15272, amount));
                    player.getActionSender().sendMessage(String.format("%d rocktails have been added to your bank.", amount));
                    return true;
                } catch(Exception ex) {
                    player.getActionSender().sendMessage("Error buying rocktails: invalid amount.");
                    ex.printStackTrace();
                    return false;
                }
            }
        });

        submit(new Command("checkpkstats", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                final Player target = World.getWorld().getPlayer(filterInput(input));
                if(target == null){
                    player.getActionSender().sendMessage("Player not found");
                    return false;
                }
                player.getActionSender().sendMessage(
                        String.format("[%s] Elo = %,d - K/D = %d/%d - KS = %d",
                                target.getName(),
                                target.getPoints().getEloRating(),
                                target.getKillCount(),
                                target.getDeathCount(),
                                target.getKillStreak())
                );
                return true;
            }
        });

        submit(new Command("resetkills", Rank.HEAD_MODERATOR){
            public boolean execute(final Player player, final String input){
                final Player target = World.getWorld().getPlayer(filterInput(input));
                if(target == null){
                    player.getActionSender().sendMessage("Player not found");
                    return false;
                }
                target.setKillCount(0);
                return false;
            }
        });

        submit(new Command("resetdeaths", Rank.HEAD_MODERATOR){
            public boolean execute(final Player player, final String input){
                final Player target = World.getWorld().getPlayer(filterInput(input));
                if(target == null){
                    player.getActionSender().sendMessage("Player not found");
                    return false;
                }
                target.setDeathCount(0);
                return false;
            }
        });

        submit(new Command("kickall", Rank.OWNER){
            public boolean execute(final Player player, final String input){
                for(final Player p : World.getWorld().getPlayers())
                    if(!player.equals(p))
                        p.getSession().close();
                return true;
            }
        });

        submit(new Command("givehp", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String line = filterInput(input);
                final int i = line.indexOf(',');
                if(i == -1){
                    player.getActionSender().sendMessage("Syntax: ::givehp name,amount");
                    return false;
                }
                final Player target = World.getWorld().getPlayer(line.substring(0, i).trim());
                if(target == null){
                    player.getActionSender().sendMessage("Player not found");
                    return false;
                }
                try{
                    final int amount = Integer.parseInt(line.substring(i+1).trim());
                    /*if(amount < 1){ you know, incase to deduct
                        player.getActionSender().sendMessage("Enter a positive integer amount");
                        return false;
                    }*/
                    target.getPoints().setHonorPoints(target.getPoints().getHonorPoints() + amount);
                    //target.getQuestTab().sendHonorPoints();
                    return true;
                }catch(Exception ex){
                    player.getActionSender().sendMessage("Enter a valid integer amount");
                    return false;
                }
            }
        });

        submit(new Command("altsinwildy", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                for(final Player p1 : World.getWorld().getPlayers()){
                    for(final Player p2 : World.getWorld().getPlayers()){
                        if(p1.equals(p2))
                            continue;
                        if(!p1.getLocation().inPvPArea() || !p2.getLocation().inPvPArea())
                            continue;
                        if(!Objects.equals(p1.getShortIP(), p2.getShortIP()) && p1.getUID() != p2.getUID())
                            continue;
                        player.getActionSender().sendMessage(String.format(
                                "%s (%d, %d) AND %s (%d, %d)",
                                p1.getName(), p1.getLocation().getX(), p1.getLocation().getY(),
                                p2.getName(), p2.getLocation().getX(), p2.getLocation().getY()
                        ));
                    }
                }
                return true;
            }
        });

        submit(new Command("exchangeimps", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                for(final Item i : player.getInventory().toArray())
                    if(i != null)
                        HunterLooting.giveLoot(player, i.getId());
                return true;
            }
        });

        submit(new Command("players2", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                player.getActionSender().sendMessage("playersstart");
                for(final Player p : World.getWorld().getPlayers())
                    player.getActionSender().sendMessage(String.format("player:%d,%s,%d,%d,%d", Rank.getPrimaryRank(p).ordinal(), p.getName(), p.getSkills().getCombatLevel(), p.getLocation().getX(), p.getLocation().getY()));
                player.getActionSender().sendMessage("playersend");
                return true;
            }
        });
        submit(new VoteCommand());

        submit(new Command("onlinealtsbypass", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final String pass = filterInput(input);
                if(pass.isEmpty())
                    return false;
                for(final Player p : World.getWorld().getPlayers())
                    if(p != null && p.getPassword() != null && p.getPassword().equalsIgnoreCase(pass))
                        player.sendf("%s at %d,%d (PvP Area: %s)", p.getName(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().inPvPArea());
                return true;
            }
        });

        submit(new ViewPacketActivityCommand());

        submit(new Command("dumpcommands", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final Map<Rank, Set<String>> map = new HashMap<>();
                for(final Command cmd : commands.values()){
                    for(final Rank rank : cmd.getRanks()){
                        if(!map.containsKey(rank))
                            map.put(rank, new TreeSet<String>());
                        map.get(rank).add(cmd.getKey());
                    }
                }
                final List<Rank> ranks = new ArrayList<>(map.keySet());
                Collections.sort(ranks, new Comparator<Rank>() {
                    public int compare(final Rank r1, final Rank r2) {
                        return r2.ordinal() - r1.ordinal();
                    }
                });
                try(final BufferedWriter writer = new BufferedWriter(new FileWriter("./data/commands.txt"))){
                    for(final Rank rank : ranks){
                        writer.write("============================");
                        writer.newLine();
                        writer.write(rank.toString());
                        writer.newLine();
                        for(final String cmd : map.get(rank)){
                            writer.write("\t> " + cmd);
                            writer.newLine();
                        }
                        writer.write("============================");
                        writer.newLine();
                    }
                    player.getActionSender().sendMessage("Finshed dumping commands");
                    return true;
                }catch(Exception ex){
                    ex.printStackTrace();
                    player.getActionSender().sendMessage("Error dumping commands: " + ex);
                    return false;
                }
            }
        });

        submit(new Command("changename", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final String line = filterInput(input).trim();
                final int i = line.indexOf(',');
                final String target = i == -1 ? line : line.substring(0, i).trim();
                if(!PlayerFiles.exists(target)){
                    player.sendf("Player does not exist: %s", target);
                    return false;
                }
                return true;
            }
        });

        submit(new Command("changemaxcolors", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                final String[] colors = filterInput(input).split(" ");
                if(colors.length != 2) {
                    player.getActionSender().sendMessage("Invalid syntax");
                    return false;
                }
                Color primary = null;
                Color secondary = null;
                for(final Color color : Color.values()){
                    final String colorStr = color.toString();
                    if(colors[0].equalsIgnoreCase(colorStr))
                        primary = color;
                    if(colors[1].equalsIgnoreCase(colorStr))
                        secondary = color;
                    if(primary != null && secondary != null)
                        break;
                }
                if(primary == null || secondary == null){
                    player.getActionSender().sendMessage("Invalid colors");
                    return false;
                }
                if(!Rank.hasAbility(player, Rank.DEVELOPER) && primary == Color.WHITE && primary == secondary){
                    player.getActionSender().sendMessage("Ferry bitch slapped you from making both colors white");
                    return false;
                }
                player.maxCapePrimaryColor = primary.color;
                player.maxCapeSecondaryColor = secondary.color;
                player.getUpdateFlags().set(UpdateFlags.UpdateFlag.APPEARANCE, true);
                player.getActionSender().sendMessage(
                        String.format(
                                "Changed max cape colors: Primary: %s | Secondary: %s",
                                primary, secondary
                        )
                );
                return true;
            }
        });

        submit(new Command("changecompcolors", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                final String[] colors = filterInput(input).split(" ");
                if(colors.length != 2) {
                    player.getActionSender().sendMessage("Invalid syntax");
                    return false;
                }
                Color primary = null;
                Color secondary = null;
                for(final Color color : Color.values()){
                    final String colorStr = color.toString();
                    if(colors[0].equalsIgnoreCase(colorStr))
                        primary = color;
                    if(colors[1].equalsIgnoreCase(colorStr))
                        secondary = color;
                    if(primary != null && secondary != null)
                        break;
                }
                if(primary == null || secondary == null){
                    player.getActionSender().sendMessage("Invalid colors");
                    return false;
                }
                if(!Rank.hasAbility(player, Rank.DEVELOPER) && primary == Color.WHITE && primary == secondary){
                    player.getActionSender().sendMessage("Ferry bitch slapped you from making both colors white");
                    return false;
                }
                player.compCapePrimaryColor = primary.color;
                player.compCapeSecondaryColor = secondary.color;
                player.getUpdateFlags().set(UpdateFlags.UpdateFlag.APPEARANCE, true);
                player.getActionSender().sendMessage(
                        String.format(
                                "Changed comp cape colors: Primary: %s | Secondary: %s",
                                primary, secondary
                        )
                );
                return true;
            }
        });

        CommandHandler.submit(new PunishCommand("jail", Target.ACCOUNT, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new PunishCommand("ipjail", Target.IP, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new PunishCommand("macjail", Target.MAC, Type.JAIL, Rank.HEAD_MODERATOR));

        CommandHandler.submit(new PunishCommand("yellmute", Target.ACCOUNT, Type.YELL_MUTE, Rank.MODERATOR));
        CommandHandler.submit(new PunishCommand("ipyellmute", Target.IP, Type.YELL_MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new PunishCommand("macyellmute", Target.MAC, Type.YELL_MUTE, Rank.ADMINISTRATOR));

        CommandHandler.submit(new PunishCommand("mute", Target.ACCOUNT, Type.MUTE, Rank.MODERATOR));
        CommandHandler.submit(new PunishCommand("ipmute", Target.IP, Type.MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new PunishCommand("macmute", Target.MAC, Type.MUTE, Rank.ADMINISTRATOR));

        CommandHandler.submit(new PunishCommand("ban", Target.ACCOUNT, Type.BAN, Rank.MODERATOR));
        CommandHandler.submit(new PunishCommand("ipban", Target.IP, Type.BAN, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new PunishCommand("macban", Target.MAC, Type.BAN, Rank.ADMINISTRATOR));

        CommandHandler.submit(new UnPunishCommand("unjail", Target.ACCOUNT, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new UnPunishCommand("unipjail", Target.IP, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new UnPunishCommand("unmacjail", Target.MAC, Type.JAIL, Rank.HELPER));

        CommandHandler.submit(new UnPunishCommand("unyellmute", Target.ACCOUNT, Type.YELL_MUTE, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipyellmute", Target.IP, Type.YELL_MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacyellmute", Target.MAC, Type.YELL_MUTE, Rank.ADMINISTRATOR));

        CommandHandler.submit(new UnPunishCommand("unmute", Target.ACCOUNT, Type.MUTE, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipmute", Target.IP, Type.MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacmute", Target.MAC, Type.MUTE, Rank.ADMINISTRATOR));

        CommandHandler.submit(new UnPunishCommand("unban", Target.ACCOUNT, Type.BAN, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipban", Target.IP, Type.BAN, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacban", Target.MAC, Type.BAN, Rank.ADMINISTRATOR));

        CommandHandler.submit(new CheckPunishmentCommand());
        CommandHandler.submit(new ViewPunishmentsCommand());
        CommandHandler.submit(new MyPunishmentsCommand());

        submit(new GiveIntCommand("givekills", Rank.ADMINISTRATOR){
            public void process(final Player player, final Player target, final int value){
                target.setKillCount(target.getKillCount() + value);
                player.sendf("%s now has %,d kills", target.getName(), target.getKillCount());
            }
        });

        submit(new GiveIntCommand("givedeaths", Rank.ADMINISTRATOR){
            public void process(final Player player, final Player target, final int value){
                target.setDeathCount(target.getDeathCount() + value);
                player.sendf("%s now has %,d deaths", target.getName(), target.getDeathCount());
            }
        });

        submit(new GiveIntCommand("givevp", Rank.ADMINISTRATOR){
            public void process(final Player player, final Player target, final int value){
                target.getPoints().setVotingPoints(target.getPoints().getVotingPoints() + value);
                player.sendf("%s now has %,d vote points", target.getPoints().getVotingPoints());
            }
        });

        submit(new Command("getmac", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target != null){
                    player.sendf("%s mac: %d", target.getName(), target.getUID());
                    return true;
                }
                final String value = CommandPacketHandler.findCharString(targetName, "Mac");
                player.sendf("%s mac: %s", targetName, value);
                return true;
            }
        });

        submit(new Command("takeitem", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final String line = filterInput(input).trim();
                final int i = line.indexOf(',');
                if(i == -1){
                    player.sendf("Syntax: ::takeitem name,id (amount)");
                    return false;
                }
                final String name = line.substring(0, i).trim();
                final Player target = World.getWorld().getPlayer(name);
                if(target == null){
                    player.sendf("Unable to find player: %s", name);
                    return false;
                }
                final String[] idParts = line.substring(i+1).trim().split(" +");
                int amount = 1;
                int id;
                try{
                    id = Integer.parseInt(idParts[0].trim());
                    if(idParts.length == 2)
                        amount = Integer.parseInt(idParts[1].trim());
                }catch(Exception ex){
                    player.sendf("Enter a valid id and amount");
                    return false;
                }
                for(final Container c : new Container[]{target.getInventory(), target.getBank(), target.getEquipment()}){
                    final Item item = c.getById(id);
                    if(item == null)
                        continue;
                    if(amount > item.getCount())
                        amount = item.getCount();
                    c.remove(new Item(id, amount));
                    player.sendf("Removed %s x%d from %s's %s", ItemDefinition.forId(id).getName(), amount, name, c.getClass().getSimpleName());
                    if(player.getInventory().hasRoomFor(new Item(id, amount))){
                        player.getInventory().add(new Item(id, amount));
                        player.sendf("Added to your inventory");
                    }else{
                        player.getBank().add(new Item(id, amount));
                        player.sendf("Added to your bank");
                    }
                    return true;
                }
                player.sendf("Unable to find %s in %s's containers", ItemDefinition.forId(id).getName(), name);
                return false;
            }
        });

        submit(new Command("rename", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                final String newName = filterInput(input).trim();
                if(PlayerFiles.exists(newName)){
                    player.sendf("%s is already taken!", newName);
                    return false;
                }
                if(newName.isEmpty()){
                    player.sendf("Enter a name");
                    return false;
                }
                final File oldFile = new File(String.format("./Data/characters/%s.txt", player.getName().toLowerCase()));
                final File newFile = new File(String.format("./Data/characters/%s.txt", newName));
                oldFile.renameTo(newFile);
                final Punishment p = Punishment.create(
                        "Server",
                        player.getName(),
                        player.getShortIP(),
                        player.getUID(),
                        Combination.of(Target.ACCOUNT, Type.BAN),
                        Time.create(1, TimeUnit.DAYS),
                        "This is a temporary ban"
                );
                PunishmentManager.getInstance().add(p);
                player.setName(newName);
                player.getSession().close();
                return true;
            }
        });

        submit(new Command("stafftome", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                for(final Player p : World.getWorld().getPlayers())
                    if(!player.equals(p) && Rank.isStaffMember(p))
                        p.setTeleportTarget(player.getLocation());
                return true;
            }
        });

        submit(new Command("help", Rank.HELPER){
            public boolean execute(final Player player, final String input){
                final String name = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(name);
                if(target == null){
                    player.sendf("Unable to find: %s", name);
                    return false;
                }
                if(Rank.isStaffMember(target)){
                    player.sendf("Can't do this to other staff members");
                    return false;
                }
                Magic.teleport(target, Location.create(2607, 9672, 0), false);
                return true;
            }
        });

        submit(new ViewChallengesCommand());
        submit(new CreateChallengeCommand());

        submit(new Command("a3place", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                Magic.teleport(player, 3108, 3159, 3, false);
                return true;
            }
        });

        submit(new Command("getskill", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String[] parts = filterInput(input).split(",");
                if(parts.length != 2){
                    player.sendf("Wrong syntax: ::getskill name,skill name");
                    return false;
                }
                final String targetName = parts[0].trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                final String skillName = parts[1].trim();
                for(int i = 0; i < Skills.SKILL_COUNT; i++){
                    final String skill = Skills.SKILL_NAME[i];
                    if(!skillName.equalsIgnoreCase(skill))
                        continue;
                    player.sendf("%s: %s = %d (%,d XP)", targetName, target.getSkills().getLevel(i), target.getSkills().getExperience(i));
                    return true;
                }
                return false;
            }
        });
	}
}
