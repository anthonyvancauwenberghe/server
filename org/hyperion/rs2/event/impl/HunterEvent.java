package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.HunterNpcs;
import org.hyperion.rs2.model.NPC;

/**
 * @author Jack Daniels
 */

public class HunterEvent extends Event {

	/**
	 * The delay in milliseconds between consecutive spawning Imps.
	 */
	public static int CYCLETIME = 15000;

	/**
	 * Creates the Hunter event each 15 seconds.
	 */
	public HunterEvent() {
		super(CYCLETIME);
		execute();
	}

	private static int counter = 0;

	@Override
	public void execute() {
		counter = (counter == 0 ? 1 : 0);
		if(counter == 0) {
			synchronized(HunterNpcs.imps) {
				for(NPC n : HunterNpcs.imps) {
					HunterNpcs.randomWalk(n);
				}
			}
		}
		if(HunterNpcs.imps.size() <= HunterNpcs.MAX_IMPS) {
			for(int i = 0; i < 10; i++)
				HunterNpcs.spawnNewImp();
		}
	}

}
