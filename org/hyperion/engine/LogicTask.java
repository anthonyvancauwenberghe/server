package org.hyperion.engine;

import java.util.concurrent.Callable;

/**
 * Created by Gilles on 23/02/2016.
 */
public abstract class LogicTask implements Callable<Boolean> {

    private final String taskName;

    public LogicTask(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
}
