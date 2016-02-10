package org.hyperion.rs2.commands;

import org.hyperion.rs2.commands.impl.CommandResult;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.savingnew.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 10/02/2016.
 */
public class NewCommandHandler {

    /**
     * Simple static integer to keep the maximum available parts for the command.
     */
    private final static int MAXIMUM_PARTS = 5;

    /**
     * This map just keeps all the commands that got submitted in it.
     */
    private final static HashMap<String, List<NewCommand>> COMMANDS = new HashMap<>();

    /**
     * This class adds the submitted command to the map. It does this by submitting them one by one to a help method.
     * @param commands The commands that need submitting.
     */
    public static void submit(NewCommand... commands) {
        Arrays.stream(commands).forEach(NewCommandHandler::submit);
    }

    /**
     * Adds a single command to the commands map.
     * @param command The command to add to the map.
     */
    private static void submit(NewCommand command) {
        if(!COMMANDS.containsKey(command.getKey()))
            COMMANDS.put(command.getKey(), new ArrayList<>());
        COMMANDS.get(command.getKey()).add(command);
    }

    /**
     * This method processes the command if possible.
     * @param key The command key.
     * @param player The player using the command.
     * @param input The extra input for the command.
     * @return Whether the command was found or not. If not it'll continuing searching.
     */
    public static boolean processCommand(String key, Player player, String input) {
        //First we check if the map actually contains this command
        if(!COMMANDS.containsKey(key))
            return false;
        //After we split the input, if the command is just the key we skip a part.
        List<NewCommand> fittingCommands;
        String[] parts = {};
        if(!key.equals(input)) {
            parts = input.replace(key + " ", "").toLowerCase().split(":");
            //We allow a maximum of 5 parts to prevent abuse.
            if(parts.length > MAXIMUM_PARTS)
                return false;
            int requiredLength = parts.length;
            fittingCommands = COMMANDS.get(key).stream().filter(command -> command.getRequiredInput().length == requiredLength).collect(Collectors.toList());
        } else {
            fittingCommands = COMMANDS.get(key).stream().filter(command -> command.getRequiredInput().length == 0).collect(Collectors.toList());
        }
        //Then we do a simple filter to only get the commands with the same amount of arguments.
        //If there are no commands left then we tell the player all the possible commands with that key.
        if(fittingCommands.isEmpty()) {
            player.sendMessage("The possible combinations for this command are: ");
            COMMANDS.get(key).forEach(command -> player.sendMessage(command.getModelInput()));
            return true;
        }
        //We try for each command that fits the amount of inputs if the command works or returns individual input (meaning it fit but didn't .
        for(NewCommand command : fittingCommands) {
            if (command == null)
                continue;
            CommandResult commandResult = command.doCommand(player, parts);
            if(commandResult == CommandResult.SUCCESSFUL || commandResult == CommandResult.GOT_ERROR_MESSAGE)
                return true;
        }
        //If after trying neither of the commands worked (and they didn't give their individual message) we give them the possible inputs, but this time filtered.
            player.sendMessage("The possible combinations for this command with " + parts.length + " arguments are: ");
            fittingCommands.forEach(command -> player.sendMessage(command.getModelInput()));
            return true;
    }

    /**
     * SILY EXAMPLECOMMAND JUST TO GIVE YOU THE IDEA, REMOVE IT AFTER SEEING.
     * ADD ALL COMMAND IN THIS STATIC METHOD TEMPORARILY, I'LL ADD A PROPER INITIALIZER LATER.
     * ~ GLIS
     */
    static {
        NewCommandHandler.submit(new NewCommand("isonline", Rank.DEVELOPER, new CommandInput<>(PlayerSaving::playerExists, "player", "A player that exists in the system.")) {
            @Override
            protected boolean execute(Player player, String[] input) {
                player.sendMessage("Player " + TextUtils.optimizeText(input[0]) + " is currently " + (World.getWorld().getPlayer(input[0]) == null ? "offline" : "online"));
                return true;
            }
        });
    }
}
