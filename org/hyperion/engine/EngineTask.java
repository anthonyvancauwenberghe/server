package org.hyperion.engine;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * The IO task
 *
 * Created by Gilles on 23/02/2016.
 */
public abstract class EngineTask<T> implements Callable<T> {

    private final String taskName;
    private final long timeout;
    private final TimeUnit timeUnit;

    public EngineTask(String taskName, long timeout, TimeUnit timeUnit) {
        this.taskName = taskName;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public final String getTaskName() {
        return taskName;
    }

    public final long getTimeout() {
        return timeout;
    }

    public final TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void stopTask() {}
}
