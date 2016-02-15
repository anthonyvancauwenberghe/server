package org.hyperion.rs2.sqlv2.db;

import org.hyperion.Configuration;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sqlv2.impl.donation.Donations;
import org.hyperion.rs2.sqlv2.impl.donation.work.CheckPendingDonationsTask;
import org.hyperion.rs2.sqlv2.impl.vote.Votes;

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
    protected boolean isEnabled() {
        return Configuration.getBoolean(DONATION_DB_ENABLED);
    }

    @Override
    protected String getUrl() {
        return Configuration.getString(DONATION_DB_URL);
    }

    @Override
    protected String getUsername() {
        return Configuration.getString(DONATION_DB_USER);
    }

    @Override
    protected String getPassword() {
        return Configuration.getString(DONATION_DB_PASSWORD);
    }

    @Override
    protected void postInit() {
        donations = new Donations(this);
        votes = new Votes(this);

        World.submit(new CheckPendingDonationsTask());
    }
}
