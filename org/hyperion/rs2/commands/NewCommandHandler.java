package org.hyperion.rs2.commands;

import org.hyperion.rs2.commands.impl.CommandResult;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.savingnew.PlayerLoading;
import org.hyperion.rs2.savingnew.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 10/02/2016.
 */
public final class NewCommandHandler {

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

        World.submit(new Event(delay) {
            @Override
            public void execute() throws IOException {
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
            parts = input.replace(key + " ", "").toLowerCase().split(":");
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
            System.out.println(commandResult);
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

    /**
     * SILY EXAMPLECOMMAND JUST TO GIVE YOU THE IDEA, REMOVE IT AFTER SEEING.
     * ADD ALL COMMAND IN THIS STATIC METHOD TEMPORARILY, I'LL ADD A PROPER INITIALIZER LATER.
     * ~ GLIS
     */
    static {
        //Got it?
        //the library jar?
        //I sent it on skype
//question was exactly is this
        //It's a lamba method reference, it fills the input in bc it only has one choice, basically it's  this;
        //+1 was right then xd
        //Just ot test, might be wonky
        NewCommandHandler.submit(
                new NewCommand("isonline", Rank.DEVELOPER, Time.FIVE_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Player " + TextUtils.optimizeText(input[0]) + " is currently " + (World.getPlayerByName(input[0]) == null ? "offline" : "online"));
                        return true;
                    }
                },

                new NewCommand("shop", Rank.DEVELOPER, Time.FIVE_SECONDS, new CommandInput<Integer>(integer -> integer >= 0, "ShopId", "The ID of the shop, has to be a positive number.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ShopManager.open(player, Integer.parseInt(input[0]));
                        return true; //You haove mftgo fbmeg ffugmcking kiding me
                        //im a kms irl fuck sake jaghax
                        //Ffs >.<
                        //(My code is cleaned up not tho xD)
                        //??FUCKING CRYing irl
                    }//Just use password as pass, this is a testserver

                    //thought that was its output :x
                    //none working :x Where is the server? xD
                });
        //I'll Skype it you
        //I may have made a mistake with new player creation (I'm still working on this, keep that in mind :)

        /*

        //Now, shop needs 1 input, it needs the shop ID, soooo, we create 1 commandInput (It has to be an integer, bc number)
                //ShopId is the name of the argument, if it gives the player feedback it will tell it to use the command as
                //::shop ShopId
                //The last String is a so called information string, which tells the player about what exactly this requirement is, so in this case
                //Now we have the constructor ready, we put in the ranks, the key, the time, and the extra input it may require (input is optional btw and you can have max 5, so you can have commands with no input and some commands with 5 inputs
                //Bc execute is abstract we have to overwrite it

        //Here comes what the command DOES, String[] input is the input after it's been checked to be valid, so in this case;
                        //Let's test it out
                        //This'll take a second, still here? yea
                        //Following? defi\o
                        //so say we have ::item val val, are we splitting it via ,?
                        //Yes, I chose , bc spaces don't work with playernames that well, it gives issues
                        //yea thats wy i chose to change my commands
                        //Basically, because you have the above predicate (in the new COmmandInput constructor) you KNOW this argument will always be a positive int
                        //Otherwise it wouldn't have gotten this far
                        //so say we do ;;shop Stringnotint
                        //commanDresult would give us the error ooption yeaa?
                        //yep
                        //What's in your pc?
                        //in what aspect
                        //Hardware
                        // i need a graphics card
                        //You need a new pc :p Holxy fudck man
                        //seems to work fairly well considering i dont really play games other than rsps

                        //Still, you'd work so much faster if your pc could keep up :p I like having a lot open so I can do a lot of things at a time and be very
                        //productive, wouldn't work on your pc for e :p
                        //yea im constantly closing processes :s
                        //sec, netbeans always uses alot of mem
                        //I'm sure if you prove useful arre may help you out, he did with Joshy too
                        //i heard something about that, wasnt josh homeless at one point?
                        //Not that I know, as far as I know the dude lives with his parents :p Idk rlly, I know him decently well, but more as a... Person I guess? Not rlly what he had in his life
                        //ahh i feel you
                        //You're missing a lib, sec
                        //i'll push iotkk*/
    }
}
