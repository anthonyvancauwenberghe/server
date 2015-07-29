package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class DemoteCommand extends Command {

	public DemoteCommand() {
		super("demote", Rank.ADMINISTRATOR);
	}

	@Override
	public boolean execute(Player player, String input) {
		input = filterInput(input);
		Player beingDemoted = World.getWorld().getPlayer(input);

		if(beingDemoted != null) {
            if(Rank.getPrimaryRankIndex(beingDemoted) > Rank.getPrimaryRankIndex(player)) {
                player.sendMessage("You cannot demote this user");
                return false;
            }
			for(Rank rank : Rank.values()) {
				if(rank.ordinal() < Rank.EVENT_MANAGER.ordinal())
					continue;
				beingDemoted.setPlayerRank(Rank.removeAbility(beingDemoted, rank));
			}
            beingDemoted.getQuestTab().sendRank();

            player.getActionSender().sendMessage(beingDemoted.getName()+" is demoted. current abilities:");
            for(Rank rank : Rank.values()) {
                if(Rank.hasAbility(beingDemoted, rank)){
                    player.getActionSender().sendMessage("@whi@"+rank.toString()+(Rank.isAbilityToggled(beingDemoted, rank) ? "" : " [I]"));
                }
            }

			return true;
		} else {
			player.getActionSender().sendMessage("This player is not online.");
			return false;
		}
	}
}
