package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Player;

public abstract class EventBuilder {
    private final int milliseconds;
    private boolean executing;

    public EventBuilder(final int ms) {
        this.milliseconds = ms;
    }

    public EventBuilder() {
        this(0);
    }

    public static final void stopEvent(final EventBuilder e) {
        e.executing = false;
    }

    public int getDelay() {
        return milliseconds;
    }

    public boolean checkStop() {
        return !executing;
    }

    public abstract void execute(Player p);
}
