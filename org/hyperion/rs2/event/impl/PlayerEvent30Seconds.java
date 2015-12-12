package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;

/**
 * An event which increases ActivityPoints, refreshes Quest Tab , refreshes Skills.
 */
public class PlayerEvent30Seconds extends Event {

    /**
     * The delay in milliseconds between consecutive execution.
     */
    public static final int CYCLETIME = 30000;

    /**
     * Creates the event each 30 seconds.
     */
    public PlayerEvent30Seconds() {
        super(CYCLETIME);
    }

    @Override
    public void execute() {

    }

}
