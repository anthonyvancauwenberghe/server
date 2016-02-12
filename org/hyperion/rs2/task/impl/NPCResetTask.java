package org.hyperion.rs2.task.impl;

import org.hyperion.engine.GameEngine;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.task.Task;

/**
 * A task which resets an NPC after an update cycle.
 *
 * @author Graham Edgecombe
 */
public class NPCResetTask implements Task {

	/**
	 * The npc to reset.
	 */
	private NPC npc;

	/**
	 * Creates the reset task.
	 *
	 * @param npc The npc to reset.
	 */
	public NPCResetTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		if(npc.getUpdateFlags().get(UpdateFlag.HIT_3)) {
			npc.getUpdateFlags().reset();
			npc.getDamage().setHit1(npc.getDamage().getHit3());
			npc.getUpdateFlags().flag(UpdateFlag.HIT);
		} else
			npc.getUpdateFlags().reset();
		if(npc.cE != null)
			npc.cE.isDoingAtk = false;
		npc.setTeleporting(false);
		npc.reset();
	}

}
