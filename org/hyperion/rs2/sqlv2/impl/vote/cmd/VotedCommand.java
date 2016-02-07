package org.hyperion.rs2.sqlv2.impl.vote.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sqlv2.impl.vote.work.CheckWaitingVotesTask;
import org.hyperion.util.Time;

public class VotedCommand extends Command {

    public static final long DELAY = Time.ONE_SECOND * 20;

    public VotedCommand() {
        super("voted", Rank.PLAYER);
    }

    @Override
    public boolean execute(final Player player, final String input) throws Exception {
        final long time = player.getExtraData().getLong("lastsql");
        if(System.currentTimeMillis() - time >= DELAY){
            player.getExtraData().put("lastsql", System.currentTimeMillis());
            World.getWorld().submit(new CheckWaitingVotesTask(player));
            return true;
        }else{
            player.sendf("You can only use this command every 20 seconds!");
            return false;
        }
    }
}
