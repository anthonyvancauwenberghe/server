package org.hyperion.rs2.commands;

import org.hyperion.rs2.commands.impl.CommandResult;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.jsoup.helper.StringUtil;

import java.util.regex.Pattern;

import static org.hyperion.rs2.commands.impl.CommandResult.*;

/**
 * A command is made so only the execute part can be overwritten, the rest should be done in the constructor.
 *
 * Created by Gilles on 10/02/2016.
 */
public abstract class NewCommand {
    private final String key;
    private final Rank rank;
    private final CommandInput[] requiredInput;

    public NewCommand(String key, Rank rank, CommandInput... requiredInput) {
        this.key = key;
        this.rank = rank;
        this.requiredInput = requiredInput;
    }

    public NewCommand(String key, CommandInput... requiredInput) {
        this(key, null, requiredInput);
    }

    public final String getKey() {
        return key;
    }

    public final Rank getRank() {
        return rank;
    }

    public final CommandInput[] getRequiredInput() {
        return requiredInput;
    }

    protected abstract boolean execute(Player player, String[] input);

    public final CommandResult doCommand(Player player, String[] input) {
        //First we'll check the rank requirement.
        if (!Rank.hasAbility(player, getRank()))
            return NEED_ERROR_MESSAGE;
        //After this we see if each argument is valid.
        try {
            for (int i = 0; i < input.length; i++) {
                //Over here we need to test what the argument is.
                if (StringUtil.isNumeric(input[i])) {
                    if (Pattern.matches("([0-9]*)\\.([0-9]*)", input[i]) && !getRequiredInput()[i].testInput(player, Double.parseDouble(input[i]))) {
                        return GOT_ERROR_MESSAGE;
                    } else if (!getRequiredInput()[i].testInput(player, Integer.parseInt(input[i]))) {
                        return GOT_ERROR_MESSAGE;
                    }
                } else if ((input[i].equals("true") || input[i].equals("false")) && !getRequiredInput()[i].testInput(player, Boolean.parseBoolean(input[i]))) {
                    return GOT_ERROR_MESSAGE;
                } else if (!getRequiredInput()[i].testInput(player, input[i])) {
                    return GOT_ERROR_MESSAGE;
                }
            }
        } catch (Exception e) {
            return NEED_ERROR_MESSAGE;
        }
        //If we get here it means that it did pass the tests.
        if (execute(player, input))
            return SUCCESSFUL;
        return NEED_ERROR_MESSAGE;
    }

    public final String getModelInput() {
        String modelInput = "::" + getKey() + " ";
        for (int i = 0; i < requiredInput.length; i++)
            modelInput += requiredInput[i].getShortDescription() + ", ";
        modelInput = modelInput.substring(0, modelInput.length() - 2);
        return modelInput;
    }

    public final String filterInput(String input) {
        return input.replace(key + " ", "").toLowerCase();
    }
}
