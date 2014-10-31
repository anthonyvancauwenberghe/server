package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.util.Misc;

public class HaloweenEvent {
	public static final List<Integer> zombies = new ArrayList<Integer>();
	private static final int bottomX = 2976, topX =  3314; 
	private static final int bottomY = 3544, topY = 3901;
	static {
		zombies.add(3066);
		zombies.add(2839);
	}
	
	public static void init(final Map<Integer, NPC> map) {
		for(int x = bottomX; x < topX; x++) {
			for(int y = bottomY; y < topY; y++) {
                if(Combat.getWildLevel(x, y) >= 10)
                	continue;
				if(Misc.random(100) == 1) {
					NPCDefinition nD = NPCDefinition.forId(zombies.get(Misc.random(zombies.size() - 1)));
					NPC n = new NPC(nD, 120,Location.create(x, y, 0));
					n.maxHealth = 400 + Misc.random(400);
					n.health = n.maxHealth;
					n.combatLevel = 300 + Misc.random(500);
					n.agreesiveDis = 3;
					n.walkToXMax = 5;
					n.walkToXMin = 5;
					n.walkToYMax = 5;
					n.walkToYMin = 5;
					n.randomWalk = true;
					n.bones = 526;
					World.getWorld().npcsWaitingList.add(n);
					map.put(x * 16 + y * 4, n);
				}
			}
		}
	}
}
