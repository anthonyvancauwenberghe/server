package org.hyperion.rs2.model.log.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.savingnew.PlayerSaving;

/**
 * Created by Jet on 12/18/2014.
 */
public class ClearLogsCommand extends Command {

    public ClearLogsCommand(){
        super("clearlogs", Rank.DEVELOPER);
    }

    public boolean execute(final Player player, final String input){
        final String targetName = filterInput(input).trim();
        if(!PlayerSaving.playerExists(targetName)){
            player.sendf("%s does not exist", targetName);
            return false;
        }
        final Player target = World.getPlayer(targetName);
        if(target == null){
            player.sendf("%s must be online for you to clear their logs", targetName);
            return false;
        }
        target.getLogManager().clear();
        player.sendf("Cleared %s's logs", targetName);
        return true;
    }
}
