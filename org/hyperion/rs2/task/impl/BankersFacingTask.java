package org.hyperion.rs2.task.impl;

import org.hyperion.engine.GameEngine;
import org.hyperion.rs2.task.Task;

/**
 * Performs garbage collection and finalization.
 *
 * @author Graham Edgecombe
 */
public class BankersFacingTask implements Task {

	@Override
	public void execute(GameEngine context) {
		context.submitWork(new Runnable() {
			public void run() {

			}
		});
	}

}
