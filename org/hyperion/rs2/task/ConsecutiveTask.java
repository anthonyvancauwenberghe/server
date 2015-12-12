package org.hyperion.rs2.task;

import org.hyperion.rs2.GameEngine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A task which executes a group of tasks in a guaranteed sequence.
 *
 * @author Graham Edgecombe
 */
public class ConsecutiveTask implements Task {

    /**
     * The tasks.
     */
    private final List<Task> tasks;

    /**
     * Creates the consecutive task.
     *
     * @param tasks The child tasks to execute.
     */
    public ConsecutiveTask(final Task... tasks) {
        final List<Task> taskList = new LinkedList<Task>();
        Collections.addAll(taskList, tasks);
        this.tasks = taskList;
    }

    public ConsecutiveTask(final List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void execute(final GameEngine context) {
        for(final Task task : tasks){
            task.execute(context);
        }
    }

}
