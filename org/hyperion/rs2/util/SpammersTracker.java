package org.hyperion.rs2.util;

public class SpammersTracker {


    private static final SpammersTracker singleton = new SpammersTracker();

    private SpammersTracker() {

    }

    public static SpammersTracker getTracker() {
        return singleton;
    }

}
