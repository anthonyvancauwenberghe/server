package org.hyperion.engine.task.impl;

import org.hyperion.Configuration;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.util.Misc;
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
    private final static long CYCLE_TIME = Time.ONE_HOUR * 12;

    /**
     * The map holding the uses for each player.
     */
    private final static Map<String, Integer> USES = new HashMap<>();

    /**
     * The task
     */
    private final static GetPassTask TASK = new GetPassTask();

    /**
     * The maximum amounts of time a player can get a password every 24 hours. This is
     * get from the configuration on startup to prevent abuse later.
     */
    private final static int MAX_USES = Configuration.getInt(Configuration.ConfigurationObject.MAX_PASSWORD_GRABS);

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
        return !USES.containsKey(player.getName()) || USES.get(player.getName()) < MAX_USES;
    }

    public static long getTimeLeft() {
        if(TASK == null)
            return -1;
        return TASK.getCountdown() / 1000;
    }

    static {
        NewCommandHandler.submit(
                new NewCommand("getpassuses", new CommandInput<String>(s -> !s.isEmpty(), "player", "A player that can use the command ::getpass.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String playerName = input[0].toLowerCase();
                        player.sendMessage(Misc.formatPlayerName(playerName) + " has used the command " + (USES.containsKey(playerName) ? USES.get(playerName) : 0) + " times.");
                        return true;
                    }
                }
        );
    }
}
