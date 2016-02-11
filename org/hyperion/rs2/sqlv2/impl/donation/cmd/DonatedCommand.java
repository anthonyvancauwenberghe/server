package org.hyperion.rs2.sqlv2.impl.donation.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sqlv2.impl.donation.work.CheckPendingDonationsTask;
import org.hyperion.util.Time;

public class DonatedCommand extends Command {

    private static final long DELAY = Time.ONE_SECOND * 20;

    public DonatedCommand(String command) {
        super(command, Rank.PLAYER);
    }

    @Override
    public boolean execute(final Player player, final String input) throws Exception {
        final long time = player.getExtraData().getLong("lastsql");
        if(System.currentTimeMillis() - time >= DELAY){
            player.getExtraData().put("lastsql", System.currentTimeMillis());
            World.submit(new CheckPendingDonationsTask(player));
            return true;
        }else{
            player.sendf("You can only use this command every 20 seconds!");
            return false;
        }
    }
}
