package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Player;

/**
 * Created by Allen Kinzalow on 4/12/2015.
 */
public class ValueMonitor {

    private long startValue;
    private long startPKValue;

    private Player player;

    public ValueMonitor(Player player) {
        this.player = player;
    }

    public void setStartValues(int startValue, int startPKValue) {
        this.startValue = startValue;
        this.startPKValue = startPKValue;
    }

    public long getStartValue() {
        return startValue;
    }

    public long getStartPKValue() {
        return startPKValue;
    }

    public long getValueDelta(long endValue) {
        return endValue - startValue;
    }

    public long getPKValueDelta(long endPKValue) {
        return endPKValue - startPKValue;
    }

}
