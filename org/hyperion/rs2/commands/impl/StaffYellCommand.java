package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.net.ActionSender;

public class StaffYellCommand extends Command {

    public StaffYellCommand(final String startsWith, final Rank... rights) {
        super(startsWith, rights);
    }

    @Override
    public boolean execute(final Player player, String input) throws Exception {
        input = filterInput(input);
        ActionSender.yellModMessage("@mag@[Staff][" + player.getName() + "]: " + input);
        return true;
    }


}
