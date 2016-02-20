package org.hyperion.sql.db;

import org.hyperion.Configuration;
import org.hyperion.sql.impl.donation.Donations;
import org.hyperion.sql.impl.vote.Votes;

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
/*
        World.submit(new CheckPendingDonationsTask());
        World.submit(new CheckWaitingVotesTask());
        NewCommandHandler.submit(
                new NewCommand("voted", Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Voting has been automated. Votes will be processed in " + CheckWaitingVotesTask.getSecondLeft() + " seconds.");
                        return true;
                    }
                },
                new NewCommand("donated", Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Donating has been automated. Donations will be processed in " + CheckPendingDonationsTask.getSecondLeft() + " seconds.");
                        return true;
                    }
                }
        );*/
    }
}