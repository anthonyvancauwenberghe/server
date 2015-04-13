package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Player;

/**
 * Created by Allen Kinzalow on 4/12/2015.
 */
public class ValueMonitor {

    private int startValue;
    private int startPKValue;

    private Player player;

    public ValueMonitor(Player player) {
        this.player = player;
    }

    public void setStartValues(int startValue, int startPKValue) {
        this.startValue = startValue;
        this.startPKValue = startPKValue;
    }

    public int getStartValue() {
        return startValue;
    }

    public int getStartPKValue() {
        return startPKValue;
    }

    public int getValueDelta(int endValue) {
        return endValue - startValue;
    }

    public int getPKValueDelta(int endPKValue) {
        return endPKValue - startPKValue;
    }

}
