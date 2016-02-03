package org.hyperion.rs2.sqlv2.db;

import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.sqlv2.impl.donation.Donations;
import org.hyperion.rs2.sqlv2.impl.donation.cmd.DonatedCommand;
import org.hyperion.rs2.sqlv2.impl.vote.Votes;
import org.hyperion.rs2.sqlv2.impl.vote.cmd.VotedCommand;

public class DonationsDb extends Db {

    private Donations donations;
    private Votes votes;

    public DonationsDb(final DbConfig config) {
        super(config);
    }

    public Donations donations() {
        return donations;
    }

    public Votes votes() {
        return votes;
    }

    @Override
    protected void postInit() {
        donations = new Donations(this);
        votes = new Votes(this);

        CommandHandler.submit(new VotedCommand());
        CommandHandler.submit(new DonatedCommand());
    }
}
