package org.hyperion.rs2.event.impl;

import org.hyperion.map.pathfinding.Path;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.combat.attack.CorporealBeast;

/**
 * Handles all events related to combat.
 *
 * @author Martin
 */
public class NpcCombatEvent extends Event {
	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;//

	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public NpcCombatEvent() {
		super(CYCLE_TIME);
	}

	public static long lastTimeDid = System.currentTimeMillis();

	@Override
	public void execute() {
        final long startTime = System.currentTimeMillis();
		NpcCombatEvent.agressiveNPCS();
		for(NPC npc : World.getWorld().getNPCs()) {
			try {
				if(npc.cE.getOpponent() != null) {
					if(! Combat.processCombat(npc.cE))
						Combat.resetAttack(npc.cE);
				} else if(! npc.isDead())
					NPC.randomWalk(npc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
        long deltaMs = System.currentTimeMillis() - startTime;
		NpcCombatEvent.corpHeal();
        System.out.println("\u001B[34m NPC Combat event took: "+(deltaMs) + "ms and corp heal: "+ (System.currentTimeMillis() - deltaMs)+ "\u001B[0m");


    }

	public static void corpHeal() {
		boolean willHeal = true;
		for(NPC npc : World.getWorld().getNPCs()) {
			try{
				if(npc.getDefinition().getId() == 8133) {
					for(Player p : npc.getRegion().getPlayers()) {
						if(p != null) {
						CombatEntity combatEntity = p.getCombat();
						if(combatEntity.getAbsX() >= 2511 && combatEntity.getAbsY() >= 4634 &&
								combatEntity.getAbsX() <= 2536 && combatEntity.getAbsY() <= 4656) {
							if(p.getLocation().getY() <= 4636 || p.getLocation().getY() >= 4655) {
								CorporealBeast.stomp(npc, p.cE, true);
							}
							willHeal = false;
							break;
						}
					}
					}
					if(willHeal) {
						npc.health = npc.maxHealth;
						Player.resetCorpDamage();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void agressiveNPCS() {
		//if(Math.random() < 100)
		//return;
		//handles agressive npcs
		for(NPC npc : World.getWorld().getNPCs()) {
			try {
				//simple agressive code used for jad etc
				if(npc.agreesiveDis > 0 && npc.ownerId > 0) {
					Player player1 = (Player) World.getWorld().getPlayers().get(npc.ownerId);
					if(player1 == null) {
						npc.cE.setOpponent(null);
						npc.serverKilled = true;
						npc.health = 0;
						if(! npc.isDead()) {
							World.getWorld().submit(new NpcDeathEvent(npc));
						}
						npc.setDead(true);
						continue;
					}
					if(player1.getLocation().isWithinDistance(npc.getLocation(), npc.agreesiveDis)) {
						npc.cE.setOpponent(player1.cE);
					}
				} else if(npc.agreesiveDis > 0) {
					//complicated agressecode used for all players
					int dis = 1000;
					Player player2 = null;
					for(Player player4 : World.getWorld().getPlayers()) {
						if(player4.getLocation().distance(npc.getLocation()) < dis && player4.getLocation().distance(npc.getLocation()) < npc.agreesiveDis) {
							dis = player4.getLocation().distance(npc.getLocation());
							player2 = player4;
						}
					}
					if(player2 != null) {
						npc.cE.setOpponent(player2.cE);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
