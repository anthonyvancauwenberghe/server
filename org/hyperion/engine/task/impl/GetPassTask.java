package org.hyperion.engine.task.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Player;
import org.hyperion.util.Time;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gilles on 1/03/2016.
 */
public final class GetPassTask extends Task {
    /**
     * The time for the task.
     */
    private final static long CYCLE_TIME = Time.ONE_DAY;

    /**
     * The map holding the uses for each player.
     */
    private final static Map<String, Integer> USES = new HashMap<>();

    /**
     * The task
     */
    private final static GetPassTask TASK = new GetPassTask();

    /**
     * The maximum amounts of time a player can get a password every 24 hours.
     */
    private final static int MAX_USES = 25;

    public static GetPassTask getTask() {
        return TASK;
    }

    private GetPassTask() {
        super(CYCLE_TIME);
    }

    @Override
    protected void execute() {
        USES.clear();
    }

    public static void incrementUse(Player player) {
        if(!USES.containsKey(player.getName()))
            USES.put(player.getName(), 0);
        USES.put(player.getName(), USES.get(player.getName()) + 1);
    }

    public static boolean canGetPass(Player player) {
        return !USES.containsKey(player.getName()) || USES.get(player.getName()) > MAX_USES;
    }

    public static long getTimeLeft() {
        if(TASK == null)
            return -1;
        return TASK.getCountdown() / 1000;
    }
}
