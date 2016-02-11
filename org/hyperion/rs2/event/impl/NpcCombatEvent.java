package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.combat.attack.CorporealBeast;
import org.hyperion.rs2.model.region.RegionManager;

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
		super(CYCLE_TIME, "npccombat");
	}

	public static long lastTimeDid = System.currentTimeMillis();

	@Override
	public void execute() {
        final long startTime = System.currentTimeMillis();
		NpcCombatEvent.agressiveNPCS();
		for(NPC npc : World.getNpcs()) {
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
        //corpHeal();
        if(deltaMs > 50)
            System.err.println("[NPC COMBAT EVENT] took: "+(deltaMs) + "ms");


    }

	public static void corpHeal() {
		boolean willHeal = true;
		for(NPC npc : World.getNpcs()) {
			try{
				if(npc.getDefinition().getId() == 8133) {
					for(Player p : RegionManager.getLocalPlayers(npc)) {
						if(p != null) {
						CombatEntity combatEntity = p.getCombat();
						if(combatEntity.getAbsX() >= 2505 && combatEntity.getAbsY() >= 4630 &&
								combatEntity.getAbsX() <= 2536 && combatEntity.getAbsY() <= 4658) {
							if(p.getLocation().getY() <= 4636 || p.getLocation().getY() >= 4655) {
								CorporealBeast.stomp(npc, p.cE, true);
							}
							willHeal = false;
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
		for(NPC npc : World.getNpcs()) {
			try {
                if(npc.ownerId < 1 && npc.agressiveDis < 1 && Combat.getWildLevel(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ()) > 20)
                    npc.agressiveDis = 3;
				if(npc.agressiveDis > 0 && npc.getCombat().getOpponent() == null) {
					//complicated agressecode used for all players
					int dis = 1000;
					Player player2 = null;
					for(Player player4 : RegionManager.getLocalPlayers(npc)) {
						if(player4 != null && player4.getLocation().distance(npc.getLocation()) < dis && player4.getLocation().distance(npc.getLocation()) < npc.agressiveDis) {
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
