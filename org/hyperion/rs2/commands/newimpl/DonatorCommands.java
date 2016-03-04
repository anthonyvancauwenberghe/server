package org.hyperion.rs2.commands.newimpl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class DonatorCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("suicide", Rank.DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getLocation().inFunPk()) {
                            player.cE.hit(player.getSkills().getLevel(Skills.HITPOINTS), player, true, Constants.MELEE);
                            return true;
                        }
                        player.sendMessage("You cannot use the suicide command outside of funpk.");
                        return true;
                    }
                },
                new NewCommand("dp", Rank.DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 158);
                        return true;
                    }
                }
        );
    }

}
