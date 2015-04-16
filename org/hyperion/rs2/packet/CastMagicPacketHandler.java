package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.BanManager;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.net.Packet;

public class CastMagicPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		switch(packet.getOpcode()) {
			case 249:
	        /*
			 * Option 1.
			 */
				option1(player, packet);
				break;
			case 131:
			/*
			 * Option 2.
			 */
				option2(player, packet);
				break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option1(final Player player, Packet packet) {
		int id = packet.getShortA();
		if(id <= 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player victim = (Player) World.getWorld().getPlayers().get(id);
		int spell = packet.getLEShort();
		//System.out.println("spell: " + spell);
		if(victim != null) {

            if(victim.getLocation().inDuel() || Duel.inDuelLocation(victim))
            {
                if(id != player.duelAttackable)
                {
                    player.sendMessage("You cannot do this to this player");
                    return;
                }

            }

			if(victim.getName().equalsIgnoreCase(player.getName())) {
				System.out.println("Abusing..." + player.getName());
				World.getWorld().getBanManager().moderate("serbar", player, BanManager.BAN, true, Long.MAX_VALUE, "abuse");
				return;
			}
			if(victim.getLocation().distance(player.getLocation()) <= 8)
				player.getWalkingQueue().reset();
			if(spell == 30298) {
				Magic.clickVenganceOther(player, victim);
			} else {
				player.cE.addSpellAttack(spell);
			}
			if(victim.getLastAttack().timeSinceLastAttack() > 10000) {
                victim.getLastAttack().updateLastAttacker(player.getName());
            }
			player.cE.setOpponent(victim.cE);
			Combat.processCombat(player.cE);
		}
	}

	private void option2(final Player player, Packet packet) {
		int id = packet.getLEShortA();
		if(id <= 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		NPC victim = (NPC) World.getWorld().getNPCs().get(id);
		int spell = packet.getShortA();
		if(victim != null) {
			if(victim.getLocation().distance(player.getLocation()) <= 8)
				player.getWalkingQueue().reset();
			player.cE.addSpellAttack(spell);
			player.cE.setOpponent(victim.cE);
		}
	}


}

