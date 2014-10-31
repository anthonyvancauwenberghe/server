package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.task.Task;

/**
 * @author Arsen
 */
public class HunterTask implements Task {

	@Override
	public void execute(GameEngine context) {
		context.submitWork(new Runnable() {
			public void run() {
				//System.out.println("I run dis code");

			}
		});
	}

}
