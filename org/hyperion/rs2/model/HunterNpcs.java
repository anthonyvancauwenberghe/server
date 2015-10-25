package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.content.skill.Hunter;
import org.hyperion.util.Misc;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vegas/Linus/Flux/Jolt/KFC/Tinderbox/Jack Daniels <- Same Person
 */

public class HunterNpcs {

	public static final int MIN_X = 2943;
	public static final int MAX_Y = 3518;
	public static final int MAX_X = 3279;
	public static final int MIN_Y = 3156;

	public static final int MAX_IMPS = 250;

	public static List<NPC> imps = new LinkedList<NPC>();

	public static int getRandomImp() {
		int max = Hunter.IMP_IDS.length - 1;
		int r1 = Misc.random(max);
		int r2 = Misc.random(max);
		int min = Math.min(r1, r2);
		return Hunter.IMP_IDS[min];
	}

	public static void hunterStartup() {
		for(int i = 0; i < MAX_IMPS; i++) {
			spawnImp();
		}
	}

	public static void spawnNewImp() {
		spawnImp();
	}

	public static void spawnImp() {
		if(Server.OLD_SCHOOL)
			return;
		int x = MIN_X + Misc.random(MAX_X - MIN_X);
		int y = MIN_Y + Misc.random(MAX_Y - MIN_Y);
		int impId = getRandomImp();
		NPC imp = World.getWorld().getNPCManager()
				.addNPC(Location.create(x, y, 0), impId, - 1);
		synchronized(imps) {
			imps.add(imp);
		}
	}

	public static boolean removeImp(int NpcId, int x, int y) {
		NPC caughtImp = null;
		synchronized(imps) {
			for(NPC imp : imps) {
				if(imp.getDefinition().getId() == NpcId) {
					if(imp.getLocation().equals(Location.create(x, y, 0))) {
						caughtImp = imp;
						break;
					}
				}
			}
		}
		if(caughtImp != null) {
			synchronized(imps) {
				imps.remove(caughtImp);
			}
			caughtImp.setDead(true);
			caughtImp.isHidden(true);
			caughtImp.destroy();
			return true;
		}
		return false;
	}

	public static void randomWalk(NPC npc) {
		int walkToX;
		int walkToY;
		do {
			walkToX = npc.getLocation().getX()
					+ (Combat.random(1) == 0 ? Misc.random(50) : Misc
					.random(- 50));
		} while(walkToX > MAX_X || walkToX < MIN_X);
		do {
			walkToY = npc.getLocation().getY()
					+ (Combat.random(1) == 0 ? Misc.random(50) : Misc
					.random(- 50));
		} while(walkToY > MAX_Y || walkToY < MIN_Y);
		npc.getWalkingQueue().reset();
		npc.getWalkingQueue().addStep(walkToX, walkToY);
		npc.getWalkingQueue().finish();
	}

}
