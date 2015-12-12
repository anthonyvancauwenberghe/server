package org.hyperion.rs2.model;

/**
 * Created by Allen Kinzalow on 4/12/2015.
 */
public class ValueMonitor {

    private final Player player;
    private long startValue;
    private long startPKValue;

    public ValueMonitor(final Player player) {
        this.player = player;
    }

    public void setStartValues(final long startValue, final long startPKValue) {
        this.startValue = startValue;
        this.startPKValue = startPKValue;
    }

    public long getStartValue() {
        return startValue;
    }

    public long getStartPKValue() {
        return startPKValue;
    }

    public long getValueDelta(final long endValue) {
        return endValue - startValue;
    }

    public long getPKValueDelta(final long endPKValue) {
        return endPKValue - startPKValue;
    }

}
