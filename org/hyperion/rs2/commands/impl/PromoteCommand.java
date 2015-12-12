package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class PromoteCommand extends Command {

    public PromoteCommand(final String commandName) {
        super(commandName, Rank.ADMINISTRATOR);
    }

    @Override
    public boolean execute(final Player player, String input) {
        input = filterInput(input);
        final Player promoted = World.getWorld().getPlayer(input);
        if(promoted != null){
            if(Rank.hasAbility(promoted, Rank.HEAD_MODERATOR) && Rank.hasAbility(player, Rank.OWNER)){
                promoted.setPlayerRank(Rank.addAbility(promoted, Rank.DEVELOPER));
                player.getActionSender().sendMessage(input + " has been promoted to head moderator");
            }else if(Rank.hasAbility(promoted, Rank.MODERATOR) && Rank.hasAbility(player, Rank.DEVELOPER)){
                promoted.setPlayerRank(Rank.addAbility(promoted, Rank.HEAD_MODERATOR));
                player.getActionSender().sendMessage(input + " has been promoted to head moderator");
            }else if(Rank.hasAbility(promoted, Rank.HELPER) && Rank.hasAbility(player, Rank.DEVELOPER)){
                promoted.setPlayerRank(Rank.addAbility(promoted, Rank.MODERATOR));
                player.getActionSender().sendMessage(input + " has been promoted to moderator");
            }else{
                promoted.setPlayerRank(Rank.addAbility(promoted, Rank.HELPER));
                player.getActionSender().sendMessage(input + " has been promoted to helper");
            }
            return true;
        }else{
            player.getActionSender().sendMessage("This player is not online.");
            return false;
        }
    }

}
