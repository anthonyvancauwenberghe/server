package org.hyperion.rs2.model.log.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.log.LogManager;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.util.PlayerFiles;

import java.util.Set;

/**
 * Created by Jet on 12/18/2014.
 */
public class ViewLogStatsCommand extends Command {

    public ViewLogStatsCommand(){
        super("viewlogstats", Rank.DEVELOPER);
    }

    public boolean execute(final Player player, final String input){
        final String targetName = filterInput(input).trim();
        if(!MergedSaving.existsMain(targetName)){
            player.sendf("%s does not exist", targetName);
            return false;
        }
        final Player target = World.getWorld().getPlayer(targetName);
        final LogManager manager = target != null ? target.getLogManager() : new LogManager(targetName);
        player.sendf("@red@%s@blu@ Log Stats", targetName);
        for(final LogEntry.Category category : LogEntry.Category.values()){
            final Set<LogEntry> logs = manager.getLogs(category);
            player.sendf("@gre@%s @blu@Logs: @red@%,d", category, logs == null ? 0 : logs.size());
        }
        return false;
    }
}
