package org.hyperion.engine.task;

import org.hyperion.Configuration;

import java.util.Objects;

/**
 * A task that the {@link TaskManager} will execute
 *
 * @author Gilles
 */
public abstract class Task {
    
    /**
     * The delay between the task executing.
     */
    private long delay;
 
    /**
     * A flag which indicates if this task should be executed once immediately.
     */
    private final boolean immediate;

    /**
     * The current 'count down' value. When this reaches zero the task will be
     * executed.
     */
    private long countdown;

    /**
     * The key for the task
     */
    private final Object key;
 
    /**
     * A flag which indicates if this task is still running.
     */
    private boolean running = true;

    public Task(long delay, boolean immediate, Object key) {
        if(delay < 0)
            throw new IllegalArgumentException("The delay for a task cannot be negative.");
        if(key == null)
            throw new IllegalArgumentException("The key for a task cannot be null.");
        this.delay = delay;
        this.immediate = immediate;
        this.key = key;
        countdown = delay / Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY);
    }

    public Task(long delay, boolean immediate) {
        this(delay, immediate, "No key provided");
    }

    public Task(long delay) {
        this(delay, false);
    }

    public Task(long delay, Object key) {
        this(delay, false, key);
    }

    public final Object getKey() {
        return Objects.requireNonNull(key);
    }

    public boolean isImmediate() {
        return immediate;
    }

    public boolean isRunning() {
        return running;
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void stop() {
        running = false;
    }
 
    /**
     * This method should be called by the scheduling class every cycle. It
     * updates the {@link #countdown} and calls the {@link #execute()} method
     * if necessary.
     * @return A flag indicating if the task is running.
     */
    public boolean tick() {
        if (running && --countdown == 0) {
            execute();
            countdown = delay / Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY);
        }
        return running;
    }
 
    /**
     * Performs this task's action.
     */
    protected abstract void execute();
}