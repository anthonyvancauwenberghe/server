package org.hyperion.rs2.commands;

import com.google.gson.JsonElement;
import org.hyperion.engine.EngineTask;
import org.hyperion.engine.GameEngine;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.engine.task.impl.GetPassTask;
import org.hyperion.rs2.commands.impl.CommandResult;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.authentication.PlayerAuthenticationGenerator;
import org.hyperion.rs2.model.punishment.cmd.CheckPunishmentCommand;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.saving.IOData;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 10/02/2016.
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

    /**
     * This map keeps what command which player used. The CommandUsage keeps the time of it's creation,
     * therefor we can keep people from using commands multiple times in a certain time frame.
     * This map gets cleaned by an event that is submitted whenever a command is used that has a delay to it.
     */
    private final static HashMap<String, List<String>> COMMANDS_USED = new HashMap<>();

    /**
     * This class adds the submitted command to the map. It does this by submitting them one by one to a help method.
     *
     * @param commands The commands that need submitting.
     */
    public static void submit(NewCommand... commands) {
        Arrays.stream(commands).forEach(NewCommandHandler::submit);
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

        World.submit(new Task(delay, "newcommandhandler") {
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
        player.sendMessage("The possible combinations for this command with " + parts.length + " arguments are: ");
        fittingCommands.forEach(command -> player.sendMessage(command.getModelInput()));
        return true;
    }

    static {
        NewCommandHandler.submit(
                new NewCommand("authenticator", Rank.HELPER, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PlayerAuthenticationGenerator.startAuthenticationDialogue(player);
                        return true;
                    }
                },
                new NewCommand("changepass", Rank.PLAYER, new CommandInput<String>(string -> string.matches("[a-zA-Z0-9]+"), "password", "The new password to use.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (input[0].length() < 5) {
                            player.sendMessage("The password has to be at least 5 characters long!");
                            return true;
                        }
                        if (input[0].length() > 12) {
                            player.sendMessage("The password cannot be longer than 12 characters!");
                            return true;
                        }
                        if (player.getPassword().equalsIgnoreCase(EncryptionStandard.encryptPassword(input[0]))) {
                            player.sendMessage("Don't use the same password again!");
                            return true;
                        }
                        TextUtils.writeToFile("./data/possiblehacks.txt", String.format("Player: %s Old password: %s New password: %s By IP: %s Date: %s", player.getName(), player.getPassword(), input[0], player.getShortIP(), new Date().toString()));
                        player.setPassword(EncryptionStandard.encryptPassword(input[0].toLowerCase()));
                        player.sendImportantMessage("Your password is now " + input[0].toLowerCase());
                        player.getPermExtraData().put("passchange", System.currentTimeMillis());
                        player.getExtraData().put("needpasschange", false);
                        return true;
                    }
                },
                new NewCommand("getip", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        player.sendMessage("Getting " + Misc.formatPlayerName(targetName) + "'s ip address... Please be patient.");
                        GameEngine.submitIO(new EngineTask<Boolean>("Get player IP", 1, TimeUnit.SECONDS) {
                            @Override
                            public Boolean call() throws Exception {
                                Optional<JsonElement> playerIP = PlayerLoading.getProperty(input[0], IOData.LAST_IP);
                                if(player == null)
                                    return false;
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (playerIP.isPresent())
                                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + "'s IP is '" + playerIP.get().getAsString() + "'");
                                        else
                                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + " has no recorded IP");
                                        stop();
                                    }
                                });
                                return true;
                            }

                            @Override
                            public void stopTask() {
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (player != null)
                                            player.sendMessage("Request timed out... Please try again at a later point.");
                                        stop();
                                    }
                                });
                            }
                        });
                        return true;
                    }
                },
                new NewCommand("getmail", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        player.sendMessage("Getting " + Misc.formatPlayerName(targetName) + "'s e-mail... Please be patient.");
                        GameEngine.submitIO(new EngineTask<Boolean>("Get player email", 1, TimeUnit.SECONDS) {
                            @Override
                            public Boolean call() throws Exception {
                                Optional<JsonElement> playerEmail = PlayerLoading.getProperty(input[0], IOData.E_MAIL);
                                if(player == null)
                                    return false;
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (playerEmail.isPresent())
                                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + "'s mail is '" + playerEmail.get().getAsString() + "'");
                                        else
                                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + " has no recorded mail");
                                        stop();
                                    }
                                });
                                return true;
                            }

                            @Override
                            public void stopTask() {
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (player != null)
                                            player.sendMessage("Request timed out... Please try again at a later point.");
                                        stop();
                                    }
                                });
                            }
                        });
                        return true;
                    }
                },
                new NewCommand("getpass", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        if (GetPassTask.canGetPass(player)) {
                            GetPassTask.incrementUse(player);
                        } else {
                            player.sendMessage("You cannot request any more passwords for the next " + GetPassTask.getTimeLeft() + " minutes.");
                            return true;
                        }
                        player.sendMessage("Getting " + Misc.formatPlayerName(targetName) + "'s password... Please be patient.");
                        GameEngine.submitIO(new EngineTask<Boolean>("Get player IP", 2, TimeUnit.SECONDS) {
                            @Override
                            public Boolean call() throws Exception {
                                Optional<JsonElement> playerPassword = PlayerLoading.getProperty(targetName, IOData.PASSWORD);
                                Optional<JsonElement> rank = PlayerLoading.getProperty(targetName, IOData.RANK);
                                if(player.getPlayerRank() < rank.get().getAsLong()) {
                                    TaskManager.submit(new Task(200, true, "commandresponse") {
                                        @Override
                                        protected void execute() {
                                            player.sendMessage("You cannot get " + targetName + "'s password. Their rank is higher than yours.");
                                            stop();
                                        }
                                    });
                                    return true;
                                }
                                if(player == null)
                                    return false;
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (playerPassword.isPresent())
                                            player.sendMessage(TextUtils.ucFirst(targetName.toLowerCase()) + "'s password is '" + EncryptionStandard.decryptPassword(playerPassword.get().getAsString()) + "'.");
                                        else
                                            player.sendMessage("Could not retrieve " + TextUtils.ucFirst(targetName.toLowerCase()) + "'s password.");
                                        stop();
                                    }
                                });
                                return true;
                            }

                            @Override
                            public void stopTask() {
                                TaskManager.submit(new Task(200, true, "commandresponse") {
                                    @Override
                                    protected void execute() {
                                        if (player != null)
                                            player.sendMessage("Request timed out... Please try again at a later point.");
                                        stop();
                                    }
                                });
                            }
                        });
                        return true;
                    }
                },
                new CheckPunishmentCommand("checkpunish")
        );
    }
}
