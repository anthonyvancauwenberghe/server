package org.hyperion.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gilles on 23/02/2016.
 */
public abstract class LogicTask implements Callable<Boolean> {

    private final String taskName;
    private final long timeout;
    private final TimeUnit timeUnit;

    public LogicTask(String taskName, long timeout, TimeUnit timeUnit) {
        this.taskName = taskName;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
