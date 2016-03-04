package org.hyperion.rs2.commands;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.commands.impl.CommandResult;
import org.hyperion.rs2.commands.newimpl.AdministratorCommands;
import org.hyperion.rs2.commands.newimpl.CommunityManagerCommands;
import org.hyperion.rs2.commands.newimpl.DeveloperCommands;
import org.hyperion.rs2.commands.newimpl.DonatorCommands;
import org.hyperion.rs2.commands.newimpl.EventManagerCommands;
import org.hyperion.rs2.commands.newimpl.ForumModeratorCommands;
import org.hyperion.rs2.commands.newimpl.GlobalModeratorCommands;
import org.hyperion.rs2.commands.newimpl.HeadModeratorCommands;
import org.hyperion.rs2.commands.newimpl.HelperCommands;
import org.hyperion.rs2.commands.newimpl.HeroCommands;
import org.hyperion.rs2.commands.newimpl.LegendCommands;
import org.hyperion.rs2.commands.newimpl.ModeratorCommands;
import org.hyperion.rs2.commands.newimpl.OwnerCommands;
import org.hyperion.rs2.commands.newimpl.PlayerCommands;
import org.hyperion.rs2.commands.newimpl.ServerCommands;
import org.hyperion.rs2.commands.newimpl.SuperDonatorCommands;
import org.hyperion.rs2.commands.newimpl.VeteranCommands;
import org.hyperion.rs2.commands.newimpl.WikiEditorCommands;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 10/02/2016.
 * Commands rewritten by DrHales
 */
public final class NewCommandHandler {

    /**
     * The splitter used.
     */
    public final static String SPLITTER = ",";

    /**
     * Simple static integer to keep the maximum available parts for the command.
     */
    private final static int MAXIMUM_PARTS = 5;

    /**
     * This map just keeps all the commands that got submitted in it.
     */
    private final static HashMap<String, List<NewCommand>> COMMANDS = new HashMap<>();

    public static HashMap<String, List<NewCommand>> getCommands() {
        return COMMANDS;
    }

    /**
     * This map keeps what command which player used. The CommandUsage keeps the time of it's creation,
     * therefor we can keep people from using commands multiple times in a certain time frame.
     * This map gets cleaned by an event that is submitted whenever a command is used that has a delay to it.
     */
    private final static HashMap<String, List<String>> COMMANDS_USED = new HashMap<>();

    /**
     * Initializes the commands for specific groups. The groups are all defined in the
     * COMMAND_TYPES array. Add a group to the COMMAND_TYPES array to make them initialize.
     * Command groups must implement {@link NewCommandExtension}.
     */
    static {
        long initial = System.currentTimeMillis();
        final NewCommandExtension[] COMMAND_TYPES = {new ServerCommands(),
                new PlayerCommands(),
                new HeroCommands(),
                new LegendCommands(),
                new VeteranCommands(),
                new DonatorCommands(),
                new SuperDonatorCommands(),
                new WikiEditorCommands(),
                new EventManagerCommands(),
                new HelperCommands(),
                new ForumModeratorCommands(),
                new ModeratorCommands(),
                new GlobalModeratorCommands(),
                new CommunityManagerCommands(),
                new HeadModeratorCommands(),
                new AdministratorCommands(),
                new DeveloperCommands(),
                new OwnerCommands()};
        Arrays.stream(COMMAND_TYPES).map(NewCommandExtension::init).forEach(NewCommandHandler::submit);
        System.out.println(String.format("%,d commands submitted in %,dms", COMMANDS.size(), System.currentTimeMillis() - initial));
    }

    /**
     * This class adds the submitted command to the map. It does this by submitting them one by one to a help method.
     *
     * @param commands The commands that need submitting.
     */
    public static void submit(NewCommand... commands) {
        Arrays.stream(commands).forEach(NewCommandHandler::submit);
    }

    /**
     * This class adds the submitted command to the map. It does this by submitting them one by one to a help method.
     *
     * @param commands The commands that need submitting.
     */
    public static void submit(Collection<NewCommand> commands) {
        commands.forEach(NewCommandHandler::submit);
    }

    /**
     * Adds a single command to the commands map.
     *
     * @param command The command to add to the map.
     */
    private static void submit(NewCommand command) {
        if (!COMMANDS.containsKey(command.getKey()))
            COMMANDS.put(command.getKey(), new ArrayList<>());
        COMMANDS.get(command.getKey()).add(command);
    }

    private static void commandUsed(String playerName, String commandUsed, long delay) {
        if (!COMMANDS_USED.containsKey(playerName))
            COMMANDS_USED.put(playerName, new ArrayList<>());
        COMMANDS_USED.get(playerName).add(commandUsed);

        World.submit(new Task(delay) {
            @Override
            public void execute() {
                if (COMMANDS_USED.containsKey(playerName))
                    COMMANDS_USED.get(playerName).remove(commandUsed);
                stop();
            }
        });
    }

    /**
     * This method processes the command if possible.
     *
     * @param key    The command key.
     * @param player The player using the command.
     * @param input  The extra input for the command.
     * @return Whether the command was found or not. If not it'll continuing searching.
     */
    public static boolean processCommand(String key, Player player, String input) {
        //First we check if the map actually contains this command
        if (!COMMANDS.containsKey(key))
            return false;
        if (COMMANDS_USED.containsKey(player.getName()) && COMMANDS_USED.get(player.getName()).stream().filter(commandUsage -> commandUsage.equalsIgnoreCase(key)).count() > 0)
            return false;
        //After we split the input, if the command is just the key we skip a part.
        List<NewCommand> fittingCommands;
        String[] parts = {};
        if (!key.equals(input)) {
            parts = input.replace(key + " ", "").toLowerCase().split(SPLITTER);
            //We allow a maximum of 5 parts to prevent abuse.
            if (parts.length > MAXIMUM_PARTS)
                return false;
            int requiredLength = parts.length;
            fittingCommands = COMMANDS.get(key).stream().filter(command -> command.getRequiredInput().length == requiredLength).collect(Collectors.toList());
        } else {
            fittingCommands = COMMANDS.get(key).stream().filter(command -> command.getRequiredInput().length == 0).collect(Collectors.toList());
        }
        //Then we do a simple filter to only get the commands with the same amount of arguments.
        //If there are no commands left then we tell the player all the possible commands with that key.
        if (fittingCommands.isEmpty()) {
            player.sendMessage("The possible combinations for this command are: ");
            COMMANDS.get(key).forEach(command -> player.sendMessage(command.getModelInput()));
            return true;
        }
        //We try for each command that fits the amount of inputs if the command works or returns individual input (meaning it fit but didn't .
        for (NewCommand command : fittingCommands) {
            if (command == null)
                continue;
            CommandResult commandResult = command.doCommand(player, parts);
            if (commandResult == CommandResult.GOT_ERROR_MESSAGE)
                return true;
            if (commandResult == CommandResult.SUCCESSFUL) {
                if (command.hasDelay())
                    commandUsed(player.getName(), key, command.getDelay());
                return true;
            }
        }
        //If after trying neither of the commands worked (and they didn't give their individual message) we give them the possible inputs, but this time filtered.
        player.sendf("The possible combinations for this command with %,d arguments are: ", parts.length);
        fittingCommands.forEach(command -> player.sendMessage(command.getModelInput()));
        return true;
    }
}
