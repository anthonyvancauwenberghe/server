package org.hyperion.rs2.sqlv2.db;

import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.sqlv2.donation.Donations;
import org.hyperion.rs2.sqlv2.donation.cmd.DonatedCommand;
import org.hyperion.rs2.sqlv2.keyword.Keywords;
import org.hyperion.rs2.sqlv2.vote.Votes;
import org.hyperion.rs2.sqlv2.vote.cmd.VotedCommand;

public class DonationsDb extends Db {

    private Donations donations;
    private Votes votes;
    private Keywords keywords;

    public DonationsDb(final DbConfig config) {
        super(config);
    }

    public Donations donations() {
        return donations;
    }

    public Votes votes() {
        return votes;
    }

    public Keywords keywords() {
        return keywords;
    }

    @Override
    protected void postInit() {
        donations = new Donations(this);
        votes = new Votes(this);
        keywords = new Keywords(this);

        Keywords.initCache();

        CommandHandler.submit(new VotedCommand());
        CommandHandler.submit(new DonatedCommand());
    }
}
