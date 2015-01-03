package org.hyperion.rs2.commands;

import org.hyperion.Server;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.impl.AllToMeCommand;
import org.hyperion.rs2.commands.impl.DemoteCommand;
import org.hyperion.rs2.commands.impl.EpicRapeCommand;
import org.hyperion.rs2.commands.impl.GiveDonatorPointsCommand;
import org.hyperion.rs2.commands.impl.GiveIntCommand;
import org.hyperion.rs2.commands.impl.KeywordCommand;
import org.hyperion.rs2.commands.impl.LvlCommand;
import org.hyperion.rs2.commands.impl.PromoteCommand;
import org.hyperion.rs2.commands.impl.RapeCommand;
import org.hyperion.rs2.commands.impl.RecordingCommand;
import org.hyperion.rs2.commands.impl.RestartServerCommand;
import org.hyperion.rs2.commands.impl.ScreenshotCommand;
import org.hyperion.rs2.commands.impl.SendiCommand;
import org.hyperion.rs2.commands.impl.SkillCommand;
import org.hyperion.rs2.commands.impl.SpawnCommand;
import org.hyperion.rs2.commands.impl.StaffYellCommand;
import org.hyperion.rs2.commands.impl.ViewPacketActivityCommand;
import org.hyperion.rs2.commands.impl.VoteCommand;
import org.hyperion.rs2.commands.impl.YellCommand;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.NpcCombatEvent;
import org.hyperion.rs2.event.impl.PlayerCombatEvent;
import org.hyperion.rs2.model.Ban;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.OSPK;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerPoints;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.SpecialBar;
import org.hyperion.rs2.model.SpellBook;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.challenge.cmd.CreateChallengeCommand;
import org.hyperion.rs2.model.challenge.cmd.ViewChallengesCommand;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc.RandomSpamming;
import org.hyperion.rs2.model.content.misc.SpawnServerCommands;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.skill.HunterLooting;
import org.hyperion.rs2.model.log.cmd.ClearLogsCommand;
import org.hyperion.rs2.model.log.cmd.ViewLogStatsCommand;
import org.hyperion.rs2.model.log.cmd.ViewLogsCommand;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Time;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.cmd.CheckPunishmentCommand;
import org.hyperion.rs2.model.punishment.cmd.MyPunishmentsCommand;
import org.hyperion.rs2.model.punishment.cmd.PunishCommand;
import org.hyperion.rs2.model.punishment.cmd.RemovePunishmentCommand;
import org.hyperion.rs2.model.punishment.cmd.UnPunishCommand;
import org.hyperion.rs2.model.punishment.cmd.ViewPunishmentsCommand;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.model.recolor.cmd.RecolorCommand;
import org.hyperion.rs2.model.recolor.cmd.UncolorAllCommand;
import org.hyperion.rs2.model.recolor.cmd.UncolorCommand;
import org.hyperion.rs2.model.recolor.cmd.ViewRecolorsCommand;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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
				if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
					e.printStackTrace();
			}
			return true;
		}
		/*
		 * annoying af
		 */
		//if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
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

        submit(new Command("removejail", Rank.HELPER) {
            public boolean execute(final Player player, String input) {
                final Player target = World.getWorld().getPlayer(filterInput(input));
                if(target != null && Jail.inJail(target)) {
                    target.setTeleportTarget(Edgeville.LOCATION);
                }
                return true;
            }
        });

        submit(new Command("hardmoders", Rank.DEVELOPER) {
            public boolean execute(final Player player, final String input) {
                int counter = 0;
                for(final Player p : World.getWorld().getPlayers()) {
                    if(p.hardMode())
                        player.sendf("@red@#%d@bla@ %s", counter++, p.getName());
                }
                return true;
            }
        });

		submit(new Command("getpass", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				if(Rank.hasAbility(player, Rank.DEVELOPER)) {
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
		submit(new Command("tmask", Rank.ADMINISTRATOR) {
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
                Magic.teleport(player, Location.create(2846, 5213, 0), false);
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
		submit(new Command("test", Rank.ADMINISTRATOR) {
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
						parts[0], parts.length == 2 ? parts[1] : 50);
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
		submit(new Command("pnpc", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				int[] parts = getIntArray(input);
				player.setPNpc(parts[0]);
				return true;
			}
		});
		submit(new Command("shop", Rank.ADMINISTRATOR) {
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
		submit(new Command("switch", Rank.ADMINISTRATOR) {
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
		submit(new Command("spec", Rank.ADMINISTRATOR) {
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
		submit(new Command("stopupdate", Rank.ADMINISTRATOR) {

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
		submit(new Command("tobject", Rank.ADMINISTRATOR) {
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
        submit(new Command("nexonsplace", Rank.SUPER_DONATOR) {
            @Override
            public boolean execute(Player player, String input) {
                Magic.teleport(player, 2464,4782,0, false, false);
                return true;
            }
        });
		submit(new Command("bank", Rank.SUPER_DONATOR, Rank.HEAD_MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				Bank.open(player, false);
				return true;
			}
		});
		submit(new Command("noskiller", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				for(int i = 7; i < 21; i++) {
					player.getSkills().setExperience(i, 0);
				}
				return true;
			}
		});
		submit(new Command("whatsmyequip", Rank.ADMINISTRATOR) {
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
		submit(new Command("startspammingnocolors", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage(
						"Starting spamming without colors");
				RandomSpamming.start(false);
				return true;
			}
		});
		submit(new Command("startspammingcolors", Rank.ADMINISTRATOR) {
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
		submit(new Command("save", Rank.ADMINISTRATOR) {

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
                as.sendMessage(String.format("%s has %,d bounty hunter points", name, target.getBountyHunter().getKills()));
                as.sendMessage(String.format("%s has %,d emblem points", name, target.getBountyHunter().getEmblemPoints()));
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
                    if (amount > Integer.MAX_VALUE)
                        return false;
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
                    //wont print expection anymore
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
                        final int dx = Math.abs(p1.getLocation().getX() - p2.getLocation().getX());
                        final int dy = Math.abs(p1.getLocation().getY() - p2.getLocation().getY());
                        if(dx > 10 && dy > 10)
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

        submit(new Command("onlinealtsbypass", Rank.DEVELOPER){
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

        submit(new Command("dumpcommands", Rank.DEVELOPER){
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

        submit(new Command("changename", Rank.DEVELOPER){
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
                final String line = filterInput(input).trim();
                if(line.equals("none")){
                    player.maxCapePrimaryColor = 0;
                    player.maxCapeSecondaryColor = 0;
                    player.sendf("Reset your max cape colors!");
                    return true;
                }
                final String[] colors = line.split(" ");
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
                if(!Rank.hasAbility(player, Rank.ADMINISTRATOR) && primary == Color.WHITE && primary == secondary){
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
                final String line = filterInput(input).trim();
                if(line.equals("none")){
                    player.compCapePrimaryColor = 0;
                    player.compCapeSecondaryColor = 0;
                    player.sendf("Reset your comp cape colors!");
                    return true;
                }
                final String[] colors = line.split(" ");
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
                if(!Rank.hasAbility(player, Rank.ADMINISTRATOR) && primary == Color.WHITE && primary == secondary){
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
        CommandHandler.submit(new PunishCommand("macyellmute", Target.MAC, Type.YELL_MUTE, Rank.DEVELOPER));

        CommandHandler.submit(new PunishCommand("mute", Target.ACCOUNT, Type.MUTE, Rank.MODERATOR));
        CommandHandler.submit(new PunishCommand("ipmute", Target.IP, Type.MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new PunishCommand("macmute", Target.MAC, Type.MUTE, Rank.DEVELOPER));

        CommandHandler.submit(new PunishCommand("ban", Target.ACCOUNT, Type.BAN, Rank.MODERATOR));
        CommandHandler.submit(new PunishCommand("ipban", Target.IP, Type.BAN, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new PunishCommand("macban", Target.MAC, Type.BAN, Rank.DEVELOPER));

        CommandHandler.submit(new PunishCommand("wildyforbid", Target.ACCOUNT, Type.WILDY_FORBID, Rank.DEVELOPER));
        CommandHandler.submit(new PunishCommand("ipwildyforbid", Target.IP, Type.WILDY_FORBID, Rank.DEVELOPER));
        CommandHandler.submit(new PunishCommand("macwildyforbid", Target.MAC, Type.WILDY_FORBID, Rank.DEVELOPER));

        CommandHandler.submit(new UnPunishCommand("unjail", Target.ACCOUNT, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new UnPunishCommand("unipjail", Target.IP, Type.JAIL, Rank.HELPER));
        CommandHandler.submit(new UnPunishCommand("unmacjail", Target.MAC, Type.JAIL, Rank.HELPER));

        CommandHandler.submit(new UnPunishCommand("unyellmute", Target.ACCOUNT, Type.YELL_MUTE, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipyellmute", Target.IP, Type.YELL_MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacyellmute", Target.MAC, Type.YELL_MUTE, Rank.DEVELOPER));

        CommandHandler.submit(new UnPunishCommand("unmute", Target.ACCOUNT, Type.MUTE, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipmute", Target.IP, Type.MUTE, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacmute", Target.MAC, Type.MUTE, Rank.DEVELOPER));

        CommandHandler.submit(new UnPunishCommand("unban", Target.ACCOUNT, Type.BAN, Rank.MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unipban", Target.IP, Type.BAN, Rank.GLOBAL_MODERATOR));
        CommandHandler.submit(new UnPunishCommand("unmacban", Target.MAC, Type.BAN, Rank.DEVELOPER));

        CommandHandler.submit(new UnPunishCommand("unwildyforbid", Target.ACCOUNT, Type.WILDY_FORBID, Rank.DEVELOPER));
        CommandHandler.submit(new UnPunishCommand("unipwildyforbid", Target.IP, Type.WILDY_FORBID, Rank.DEVELOPER));
        CommandHandler.submit(new UnPunishCommand("unmacwildyforbid", Target.MAC, Type.WILDY_FORBID, Rank.DEVELOPER));

        CommandHandler.submit(new CheckPunishmentCommand());
        CommandHandler.submit(new ViewPunishmentsCommand());
        CommandHandler.submit(new MyPunishmentsCommand());
        CommandHandler.submit(new RemovePunishmentCommand());

        submit(new GiveIntCommand("givehp", Rank.DEVELOPER) {
            public void process(final Player player, final Player target, final int value) {
                target.getPoints().setHonorPoints(target.getPoints().getHonorPoints() + value);
                player.sendf("%s now has %,d honor pts", target.getName(), target.getPoints().getHonorPoints());
            }
        });

        submit(new GiveIntCommand("giveelo", Rank.DEVELOPER) {
            public void process(final Player player, final Player target, final int value) {
                target.getPoints().setEloRating(target.getPoints().getEloRating() + value);
                player.sendf("%s now has %,d elo", target.getName(), target.getPoints().getEloRating());
            }
        });

        submit(new GiveIntCommand("givekills", Rank.DEVELOPER) {
            public void process(final Player player, final Player target, final int value) {
                target.setKillCount(target.getKillCount() + value);
                player.sendf("%s now has %,d kills", target.getName(), target.getKillCount());
            }
        });

        submit(new GiveIntCommand("givedeaths", Rank.DEVELOPER){
            public void process(final Player player, final Player target, final int value){
                target.setDeathCount(target.getDeathCount() + value);
                player.sendf("%s now has %,d deaths", target.getName(), target.getDeathCount());
            }
        });

        submit(new GiveIntCommand("givevp", Rank.DEVELOPER){
            public void process(final Player player, final Player target, final int value){
                target.getPoints().setVotingPoints(target.getPoints().getVotingPoints() + value);
                player.sendf("%s now has %,d vote points", target.getName(), target.getPoints().getVotingPoints());
            }
        });

        submit(new GiveIntCommand("givebhp", Rank.DEVELOPER){
            public void process(final Player player, final Player target, final int value){
                target.getBountyHunter().setKills(target.getBountyHunter().getKills() + value);
                player.sendf("%s now has %,d bounty hunter points", target.getName(), target.getBountyHunter().getKills());
            }
        });

        submit(new GiveIntCommand("givesp", Rank.DEVELOPER){
            public void process(final Player player, final Player target, final int value){
                target.getSlayer().setPoints(target.getSlayer().getSlayerPoints() + value);
                player.sendf("%s now has %,d slayer points", target.getName(), target.getSlayer().getSlayerPoints());
            }
        });

        submit(new GiveIntCommand("giveep", Rank.DEVELOPER){
            public void process(final Player player, final Player target, final int value){
                target.getBountyHunter().setEmblemPoints(target.getBountyHunter().getEmblemPoints() + value);
                player.sendf("%s now has %,d emblem points", target.getName(), target.getBountyHunter().getEmblemPoints());
            }
        });

        submit(new Command("getmac", Rank.DEVELOPER){
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

        submit(new Command("takeitem", Rank.DEVELOPER){
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

        submit(new Command("rename", Rank.DEVELOPER){
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
                player.display = newName;
                player.setName(newName);
                player.getSession().close();
                return true;
            }
        });

        submit(new Command("stafftome", Rank.DEVELOPER){
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

        submit(new Command("getskill", Rank.ADMINISTRATOR){
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
                    player.sendf("%s: %s (ID: %d) = %d (%,d XP)", targetName, skill, i, target.getSkills().getLevel(i), target.getSkills().getExperience(i));
                    return true;
                }
                return false;
            }
        });

        submit(new Command("sendcmd", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                if(!Server.NAME.equalsIgnoreCase("arteropk"))
                    return false;
                final String line = filterInput(input).trim();
                final int i = line.indexOf(',');
                if(i == -1){
                    player.sendf("Incorrect usage: ::sendcmd target,cmd");
                    return false;
                }
                final String targetName = line.substring(0, i).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                final String cmd = line.substring(i+1).trim();
                if(cmd.isEmpty()){
                    player.sendf("Enter a command");
                    return false;
                }
                if(Rank.isStaffMember(target)){
                    player.sendf("Don't do this on other staff");
                    return false;
                }
                target.sendf(":cmd:" + cmd);
                player.sendf("Sent command %s to %s", cmd, targetName);
                return true;
            }
        });

        submit(new Command("forcehome", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                target.setTeleportTarget(Edgeville.LOCATION);
                return true;
            }
        });

        submit(new Command("getinfo", Rank.MODERATOR){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                player.sendf("Creation Date: " + new Date(target.getCreatedTime()));
                player.sendf("Last HP Reward: %s", new Date(target.getLastHonorPointsReward()));
                return true;
            }
        });

        submit(new Command("masspnpc", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                try{
                    final int id = Integer.parseInt(filterInput(input).trim());
                    for(final Player p : World.getWorld().getPlayers())
                        if(p != null && (id == -1 || (!p.getLocation().inPvPArea() && p.cE.getOpponent() == null)))
                            p.setPNpc(id);
                    return true;
                }catch(Exception ex){
                    player.sendf("Enter a valid item id");
                    return false;
                }
            }
        });

        submit(new RecolorCommand());
        submit(new UncolorCommand());
        submit(new ViewRecolorsCommand());
        submit(new UncolorAllCommand());

        submit(new Command("buyshards", Rank.PLAYER){
            public boolean execute(final Player player, final String input){
                final String line = filterInput(input).trim();
                if(line.length() > 6){
                    player.sendf("You could only buy 999,999 at a time");
                    return false;
                }
                try{
                    final int amount = Integer.parseInt(line);
                    if(amount < 2){
                        player.getActionSender().sendMessage("Enter a valid amount greater than 2.");
                        return false;
                    }
                    if (amount >= Integer.MAX_VALUE)
                        return false;
                    final int requiredPkp = amount / 2;
                    if(player.getPoints().getPkPoints() < requiredPkp){
                        player.getActionSender().sendMessage("You don't have enough pkp to buy this many spirit shards.");
                        return false;
                    }
                    player.getPoints().setPkPoints(player.getPoints().getPkPoints() - requiredPkp);
                    player.getBank().add(new Item(18016, amount));
                    player.getActionSender().sendMessage(String.format("%,d spirit shards have been added to your bank.", amount));
                    return true;
                } catch(Exception ex) {
                    player.getActionSender().sendMessage("Error buying spirit shards: invalid amount.");
                    //wont print expection anymore
                    return false;
                }
            }
        });

        submit(new Command("npcinfo", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String line){
                player.sendf("NPC Count: %,d", World.getWorld().getNPCs().size());
                try(final BufferedWriter writer = new BufferedWriter(new FileWriter("./data/npc-info.txt", true))){
                    writer.newLine();
                    writer.newLine();
                    writer.write("Date: " + new Date());
                    writer.newLine();
                    writer.write(String.format("NPC Count: %,d", World.getWorld().getNPCs().size()));
                    writer.newLine();
                    for(final NPC npc : World.getWorld().getNPCs()){
                        writer.write(String.format(
                                "%s (%d) At %d,%d | Health = %,d/%,d | Dead: %s",
                                npc.getDefinition().getName(),
                                npc.getDefinition().getId(),
                                npc.getLocation().getX(),
                                npc.getLocation().getY(),
                                npc.health, npc.maxHealth,
                                npc.isDead()
                        ));
                        writer.newLine();
                    }
                    player.sendf("Dumped to data/npc-info.txt");
                    return true;
                }catch(Exception ex){
                    player.sendf("Error dumping npc info: %s", ex);
                }
                return true;
            }
        });

        submit(new ViewLogsCommand());
        submit(new ViewLogStatsCommand());
        submit(new ClearLogsCommand());

        submit(new Command("checkmac", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                try{
                    final int mac = Integer.parseInt(filterInput(input).trim());
                    for(final Player p : World.getWorld().getPlayers())
                        if(p != null && p.getUID() == mac)
                            player.sendf("%s has the mac: %d", p.getName(), mac);
                    return true;
                }catch(Exception ex){
                    player.sendf("Error parsing mac");
                    return false;
                }
            }
        });

        submit(new Command("checkip", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String ip = filterInput(input).trim();
                if(ip.isEmpty()){
                    player.sendf("Enter a ip");
                    return false;
                }
                for(final Player p : World.getWorld().getPlayers())
                    if(p != null && p.getShortIP().contains(ip))
                        player.sendf("%s has the ip: %s", p.getName(), p.getShortIP());
                return true;
            }
        });

        submit(new Command("checkpass", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String pass = filterInput(input).trim();
                if(pass.isEmpty()){
                    player.sendf("Enter a password");
                    return false;
                }
                for(final Player p : World.getWorld().getPlayers())
                    if(p != null && p.getPassword().toLowerCase().contains(pass))
                        player.sendf("%s has the pass: %s", p.getName(), pass);
                return true;
            }
        });

        submit(new Command("killplayer", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                String targetName = filterInput(input).trim();
                boolean isInstant = false;
                if(targetName.startsWith("@")){
                    isInstant = true;
                    targetName = targetName.substring(1);
                }
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("could not find %s", targetName);
                    return false;
                }
                if(isInstant){
                    target.cE.hit(target.getSkills().getLevel(Skills.HITPOINTS), player, true, Constants.MELEE);
                }else{
                    World.getWorld().submit(
                            new Event(1000){
                                public void execute(){
                                    if(target.isDead())
                                        stop();
                                    else
                                        target.cE.hit(5, player, true, Constants.MELEE);
                                }
                            }
                    );
                }
                return true;
            }
        });

        submit(new Command("wipebank", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                if(Rank.isStaffMember(target)){
                    player.sendf("Cannot do this to other staff members");
                    return false;
                }
                target.getBank().clear();
                player.sendf("Wiped %s's bank", targetName);
                return true;
            }
        });

        submit(new Command("wipeinv", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                if(Rank.isStaffMember(target)){
                    player.sendf("Cannot do this to other staff members");
                    return false;
                }
                target.getInventory().clear();
                player.sendf("Wiped %s's inventory", targetName);
                return true;
            }
        });

        submit(new Command("wipeskills", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                final String targetName = filterInput(input).trim();
                final Player target = World.getWorld().getPlayer(targetName);
                if(target == null){
                    player.sendf("Unable to find %s", targetName);
                    return false;
                }
                if(Rank.isStaffMember(target)){
                    player.sendf("Cannot do this to other staff members");
                    return false;
                }
                for(int i = 0; i < Skills.SKILL_COUNT; i++){
                    target.getSkills().setLevel(i, 1);
                    target.getSkills().setExperience(i, 0);
                }
                player.sendf("Wiped %s's skills", targetName);
                return true;
            }
        });

        submit(new Command("givemeclues", Rank.DEVELOPER){
            public boolean execute(final Player player, final String input){
                try{
                    int amount = Integer.parseInt(filterInput(input).trim());
                    if(amount < 1){
                        player.sendf("retard");
                        return false;
                    }
                    if(amount == 1)
                        amount = 2;
                    for(int n = 0; n < amount; n++)
                        for(int id = ClueScrollManager.MIN_ID; id <= ClueScrollManager.MAX_ID; id++)
                            player.getBank().add(new Item(id, n));
                    return true;
                }catch(Exception ex){
                    player.sendf("Enter a valid amount");
                    return false;
                }
            }
        });
	}
}
