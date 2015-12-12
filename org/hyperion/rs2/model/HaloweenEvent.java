package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HaloweenEvent {
    public static final List<Integer> zombies = new ArrayList<Integer>();
    private static final int bottomX = 2976, topX = 3314;
    private static final int bottomY = 3544, topY = 3901;

    static {
        zombies.add(3066);
        zombies.add(2839);
    }

    public static void init(final Map<Integer, NPC> map) {

    }
}
