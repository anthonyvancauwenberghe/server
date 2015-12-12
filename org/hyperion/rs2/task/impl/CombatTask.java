package org.hyperion.rs2.task.impl;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.task.Task;

/**
 * Performs garbage collection and finalization.
 *
 * @author Graham Edgecombe
 */
public class CombatTask implements Task {

    @Override
    public void execute(final GameEngine context) {
        context.submitWork(new Runnable() {
            public void run() {

            }

        });
    }

}
