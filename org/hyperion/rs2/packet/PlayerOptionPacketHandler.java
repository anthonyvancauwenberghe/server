package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.BanManager;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatAssistant;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.checkers.GameHandler;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.SnowItems;
import org.hyperion.rs2.model.newcombat.Skills;
import org.hyperion.rs2.net.Packet;
import org.hyperion.util.Misc;


public class PlayerOptionPacketHandler implements PacketHandler {

	@Override//,139,73,153
	public void handle(Player player, Packet packet) {
		switch(packet.getOpcode()) {
			case /*128*/ 153:
	        /*
			 * Option 1.
			 */
				option1(player, packet);
				break;
			case /*37*/ 73:
			/*
			 * Option 2.
			 */
				option2(player, packet);
				break;
			case /*227*/ 139:
			/*
			 * Option 3.
			 */
				option3(player, packet);
				break;
			case 39: // option 4
				option4(player, packet);
				break;
			case 128: // option 5
				option5(player, packet);
				break;
			case 174: // option 6
				option6(player, packet);
				break;
			case 72:
				int id = packet.getShortA();
				//System.out.println("AtkNpc :" + attacknpc);
			/*if(attacknpc < 0 || attacknpc > World.getWorld().getNPCs().size())
				return;*/
				if(id <= 0 || id >= Constants.MAX_NPCS)
					return;
				NPC victim = (NPC) World.getWorld().getNPCs().get(id);
				if(victim != null) {
					if(victim.ownerId > 1)
						return;
					//player.getLogging().log("Clicked on npc : " + victim.getDefinition().getName());
					player.cE.setOpponent(victim.cE);
					//if(player.cE.getPlayer().getName().equals("Flux"))
					//System.out.println("AtkNpcPacket l0ll0");
					if(! Combat.processCombat(player.cE))
						Combat.resetAttack(player.cE);
					int distance = Misc.distance(player.getLocation().getX(), player.getLocation().getY(), victim.getLocation().getX(), victim.getLocation().getY());
					if(distance < 8 && (CombatAssistant.getCombatStyle(player.cE) != 8 || player.cE.getNextMagicAtk() > 0)) {
						player.getWalkingQueue().reset();
					}

				} else {
					System.out.println("NPC Victim is null");
				}
				break;

		}
	}

	/**
	 * Handles the first option on a player option menu.
	 *
	 * @param player
	 * @param packet If moderator is not in the wild and utilizes this option, then it'll send the moderation interface
	 */
	private void option1(final Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if(id <= 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		//System.out.println("attack: "+id);

		final Player victim = (Player) World.getWorld().getPlayers().get(id);
		if(victim != null) {
			if((FightPits.teamRed.contains(player) && FightPits.teamRed.contains(victim)) ||
					(FightPits.teamBlue.contains(player) && FightPits.teamBlue.contains(victim))) {
				player.sendMessage("Friend, not food!");
				return;
			}
			//player.getLogging().log("First option, clicked on player : " + victim.getName());
			if(victim.getName().equalsIgnoreCase(player.getName())) {
				System.out.println("Abusing..." + player.getName());
				World.getWorld().getBanManager().moderate("serbar", player, BanManager.BAN, true, Long.MAX_VALUE, "abuse");
				return;
			}
			if(player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				if(player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11951) {
					ContentEntity.startAnimation(player, 7530);
					SnowItems.fireSnowBall(player, victim);
					player.getWalkingQueue().reset();
					World.getWorld().submit(new Event(1000) {
						public void execute() {
							if(player.getInventory().getCount(SnowItems.SNOWBALL.getId()) > 0)
								player.getInventory().remove(new Item(11951, 1));
							else if(SnowItems.SNOWBALL.equals(player.getEquipment().get(Equipment.SLOT_WEAPON))) {
								player.getEquipment().set(Equipment.SLOT_WEAPON, null);
                            }
							victim.playGraphics(Graphic.create(1282));
							victim.getActionSender().sendMessage("You just got snowballed by " + player.getName() + "!!!");
							if(Math.random() > 0.90) {
								player.forceMessage("Owned idiot");
								player.getPoints().inceasePkPoints(1);
							}
							this.stop();
						}
					});
					return;
				}
			}
			CombatEntity oldCombat = player.getCombat().getOpponent();
			player.cE.setOpponent(victim.cE);
			//so people that spam click don't have an advantage
			if(oldCombat != victim.cE)
			if(! Combat.processCombat(player.cE))
				Combat.resetAttack(player.cE);
			int distance = Misc.distance(player.getLocation().getX(), player.getLocation().getY(), victim.getLocation().getX(), victim.getLocation().getY());
			if(distance < 8 && (CombatAssistant.getCombatStyle(player.cE) != 8 || player.cE.getAutoCastId() > 0)) {
				player.getWalkingQueue().reset();
				//player.getActionSender().sendMessage("Reset Queue");
			}
		}
	}

	/**
	 * Handles the second option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option3(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if(id <= 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player tradeWith = (Player) World.getWorld().getPlayers().get(id);
		if(tradeWith != null && System.currentTimeMillis() - tradeWith.cE.lastHit > 10000 && System.currentTimeMillis() - player.cE.lastHit > 10000 && player.duelAttackable <= 0 && tradeWith.duelAttackable <= 0) {
			if(player.getLocation().inPvPArea() && ! tradeWith.getLocation().inPvPArea()) {
				player.getActionSender().sendMessage("You cannot trade a player that is not in the wilderness, when you are");
				return;
			}
			if(tradeWith.getName().equalsIgnoreCase(player.getName())) {
				System.out.println("Abusing..." + player.getName());
				World.getWorld().getBanManager().moderate("serbar", player, BanManager.BAN, true, Long.MAX_VALUE, "abuse");
				return;
			}
			player.tradeWith2 = tradeWith;
			if(tradeWith.tradeWith2 == player)
				Trade.open(player, tradeWith);
			else {
				tradeWith.getActionSender().sendMessage(player.getName() + " :tradereq:");
				player.getActionSender().sendMessage("Sending trade request.");
			}
		} else {
			player.getActionSender().sendMessage("This player is busy.");
		}
	}

	/**
	 * Handles the third option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option2(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if(id <= 0 || id > Constants.MAX_PLAYERS) {
			return;
		}
		player.isFollowing = null;
		player.getActionSender().follow(id, 1);
		//Spammers pwning.
		if(Rank.hasAbility(player, Rank.MODERATOR)) {
			if(player.getSpam().isHunting()) {
				Player victim = (Player) World.getWorld().getPlayers().get(id);
				player.getActionSender().sendMessage(victim.getSpam().punish());
				player.getSpam().setHunting(false);
			}
		}
	}

	private void option4(Player player, Packet packet) {
		int i = packet.getLEShort() & 0xFFFF;
		if(i <= 0 || i >= Constants.MAX_PLAYERS) {
			return;
		}
		if(player.getName().toLowerCase().equals("faggot"))
			return;
		Player player1 = (Player) World.getWorld().getPlayers().get(i);
		if(player1 != null)
			if(FightPits.inGame(player)) {
				if((FightPits.teamBlue.contains(player) && FightPits.teamBlue.contains(player1))
						|| (FightPits.teamRed.contains(player) && FightPits.teamRed.contains(player1))) {
					int hit = player.getCombat().hit(25, null, false, Constants.EMPTY);
					ContentEntity.heal(player1, hit);
					player1.getActionSender().sendMessage(String.format("Thank %s for healing you %d0 hitpoints", player.getName(), hit));
				} else {
					player.getActionSender().sendMessage("Why would you want to heal that player?");
				}
				return;
			}
		/**
		 * If moderator and not in the wilderness, send moderation options
		 * Still sends the options inside pits game
		 */
			if(Rank.hasAbility(player, Rank.MODERATOR) && ! player.getLocation().inDuel()) {
				ModerationOverride.sendModerationOptions(player, player1);
				player.getActionSender().resetFollow();
				return;
			}
		if(player1 != null && System.currentTimeMillis() - player1.cE.lastHit > 10000 && System.currentTimeMillis() - player.cE.lastHit > 10000 && player.duelAttackable <= 0 && player1.duelAttackable <= 0) {
			player.duelWith2 = player1;
			if(player1.isBusy() || player.isBusy()) {
				player.getActionSender().sendMessage("That player is busy.");
				return;
			}
			if(GameHandler.inGame(player)) {
				if(player.challengedBy == null) {             //hax here , so i search for packet 128 lemme check
					GameHandler.sendRequest(player, player1);
					return;
				}
				if(player.challengedBy == player1) {
					GameHandler.acceptRequest(player);
				} else {
					GameHandler.sendRequest(player, player1);
				}
				return;
			}
			if(player1.duelWith2 == player) {
				Duel.open(player, player1);
			} else {
				player.getActionSender().sendMessage("Sending duel request.");
				player1.getActionSender().sendMessage((new StringBuilder()).append(player.getName()).append(" :duelreq:").toString());
			}
		} else {
			player.getActionSender().sendMessage("This player is busy.");
		}
	}

	private void option5(Player player, Packet packet) {
		int i = packet.getLEShort() & 0xFFFF;
		if(i <= 0 || i >= Constants.MAX_PLAYERS /*|| i > World.getWorld().getPlayers().size()*/) {
			return;
		}
		if(player.getName().toLowerCase().equals("faggot"))
			return;
		Player player1 = (Player) World.getWorld().getPlayers().get(i);
		if(player1 != null) {
			player.duelWith2 = player1;
			if(player1.isBusy() || player.isBusy()) {
				player.getActionSender().sendMessage("That player is busy.");
				return;
			}
			if(player1.duelWith2 == player) {
				Duel.open(player, player1);
			} else {
				player1.getActionSender().sendMessage((new StringBuilder()).append(player.getName()).append(" :duelreq:").toString());
				player1.getActionSender().sendMessage("Sending duel request.");
			}
		}
	}

	private void option6(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if(id <= 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player tradeWith = (Player) World.getWorld().getPlayers().get(id);
		if(tradeWith != null) {
			player.tradeWith2 = tradeWith;
			if(tradeWith.tradeWith2 == player)
				Trade.open(player, tradeWith);
			else {
				tradeWith.getActionSender().sendMessage(player.getName() + " :tradereq:");
				player.getActionSender().sendMessage("Sending trade request.");
			}
		}
	}
}

