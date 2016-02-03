package org.hyperion.rs2.sqlv2.impl.keyword.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.sqlv2.impl.keyword.Keywords;

public class ReloadKeywordsCommand extends Command {

    public ReloadKeywordsCommand() {
        super("reloadkeywords", Rank.DEVELOPER);
    }

    @Override
    public boolean execute(final Player player, final String input) throws Exception {
        if(!DbHub.initialized() || !DbHub.getDonationsDb().enabled()){
            player.sendf("Keywords are temporarily down at this time");
            return false;
        }
        if(!Keywords.reloadCache()){
            player.sendf("Unable to reload keywords");
            return false;
        }
        player.sendf("Keywords have been reloaded. There are %,d keywords.", Keywords.cacheSize());
        return true;
    }
}
