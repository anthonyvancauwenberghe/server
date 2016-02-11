package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class GiveDonatorPointsCommand extends Command {

    public GiveDonatorPointsCommand(String startsWith) {
        super(startsWith, Rank.OWNER);
    }

    @Override
    public boolean execute(Player player, String input) {
        String name = filterInput(input);
        String[] split = name.split(",");
        Player donator = World.getPlayer(split[0]);
        int amount = Integer.parseInt(split[1]);
        boolean bought = false;
        if (split.length > 2)
            bought = Boolean.parseBoolean(split[2]);
        if (donator != null) {
            donator.getPoints().increaseDonatorPoints(amount, bought);
            player.getActionSender().sendMessage("You give " + donator.getSafeDisplayName() + " " + amount + " donator points.");
        } else {
            player.getActionSender().sendMessage("Please use the format: ::givedp name,amount");
        }
        return true;
    }
}
