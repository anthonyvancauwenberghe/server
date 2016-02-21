package org.hyperion.engine.task.impl;

import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.region.RegionManager;

/**
 * Handles all events related to combat.
 *
 * @author Martin
 */
public class NpcCombatTask {
	//TODO MOVE THIS
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
