package org.hyperion.rs2.task.impl;

import org.hyperion.engine.GameEngine;
import org.hyperion.rs2.task.Task;

/**
 * A task which stops the game engine.
 *
 * @author Graham Edgecombe
 */
public class DeathTask implements Task {

	@Override
	public void execute(GameEngine context) {
		if(context.isRunning()) {
			System.out.println("SHUTTING DOWN GAME ENGINE");
			context.stop();
		}
	}

}
