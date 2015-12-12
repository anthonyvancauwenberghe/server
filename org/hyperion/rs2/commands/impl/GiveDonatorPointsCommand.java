package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class GiveDonatorPointsCommand extends Command {

    public GiveDonatorPointsCommand(final String startsWith) {
        super(startsWith, Rank.OWNER);
    }

    @Override
    public boolean execute(final Player player, final String input) {
        final String name = filterInput(input);
        final String[] split = name.split(",");
        final Player donator = World.getWorld().getPlayer(split[0]);
        final int amount = Integer.parseInt(split[1]);
        boolean bought = false;
        if(split.length > 2)
            bought = Boolean.parseBoolean(split[2]);
        if(donator != null){
            donator.getPoints().increaseDonatorPoints(amount, bought);
            player.getActionSender().sendMessage("You give " + donator.getSafeDisplayName() + " " + amount + " donator points.");
        }else{
            player.getActionSender().sendMessage("Please use the format: ::givedp name,amount");
        }
        return true;
    }
}
