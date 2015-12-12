package org.hyperion.rs2.task;

import org.hyperion.rs2.GameEngine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A task which can execute multiple child tasks simultaneously.
 *
 * @author Graham Edgecombe
 */
public class ParallelTask implements Task {

    /**
     * The child tasks.
     */
    private final List<Task> tasks;

    /**
     * Creates the parallel task.
     *
     * @param tasks The child tasks.
     */
    public ParallelTask(final Task... tasks) {
        final List<Task> taskList = new LinkedList<Task>();
        Collections.addAll(taskList, tasks);
        this.tasks = taskList;
    }

    public ParallelTask(final List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void execute(final GameEngine context) {
        for(final Task task : tasks){
            context.submitTask(new Runnable() {
                @Override
                public void run() {
                    task.execute(context);
                }
            });
        }
        try{
            context.waitForPendingParallelTasks();
        }catch(final ExecutionException e){
            throw new RuntimeException(e);
        }
    }

}
