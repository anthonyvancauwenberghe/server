package org.hyperion.rs2.sqlv2.db;

import org.hyperion.Configuration;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.sqlv2.impl.donation.Donations;
import org.hyperion.rs2.sqlv2.impl.donation.cmd.DonatedCommand;
import org.hyperion.rs2.sqlv2.impl.vote.Votes;
import org.hyperion.rs2.sqlv2.impl.vote.cmd.VotedCommand;

import static org.hyperion.Configuration.ConfigurationObject.*;

public class DonationsDb extends Db {

    private Donations donations;
    private Votes votes;

    public Donations donations() {
        return donations;
    }

    public Votes votes() {
        return votes;
    }

    @Override
    public boolean isEnabled() {
        return Configuration.getBoolean(DONATION_DB_ENABLED);
    }

    @Override
    public String getUrl() {
        return Configuration.getString(DONATION_DB_URL);
    }

    @Override
    public String getUsername() {
        return Configuration.getString(DONATION_DB_USER);
    }

    @Override
    public String getPassword() {
        return Configuration.getString(DONATION_DB_PASSWORD);
    }

    @Override
    protected void postInit() {
        donations = new Donations(this);
        votes = new Votes(this);

        CommandHandler.submit(new VotedCommand());
        CommandHandler.submit(new DonatedCommand("donated"), new DonatedCommand("getpoints"));
    }
}
