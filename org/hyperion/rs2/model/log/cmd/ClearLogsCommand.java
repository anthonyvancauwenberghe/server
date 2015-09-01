package org.hyperion.rs2.model.log.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.util.PlayerFiles;

/**
 * Created by Jet on 12/18/2014.
 */
public class ClearLogsCommand extends Command {

    public ClearLogsCommand(){
        super("clearlogs", Rank.DEVELOPER);
    }

    public boolean execute(final Player player, final String input){
        final String targetName = filterInput(input).trim();
        if(!MergedSaving.exists(targetName)){
            player.sendf("%s does not exist", targetName);
            return false;
        }
        final Player target = World.getWorld().getPlayer(targetName);
        if(target == null){
            player.sendf("%s must be online for you to clear their logs", targetName);
            return false;
        }
        target.getLogManager().clear();
        player.sendf("Cleared %s's logs", targetName);
        return true;
    }
}
