package org.hyperion.rs2.sql.impl.donation.work;

import org.hyperion.Configuration;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.DbHub;
import org.hyperion.rs2.sql.impl.donation.Donation;
import org.hyperion.util.Time;

import java.util.List;

public class CheckPendingDonationsTask extends Task {

    private final double MULTIPLIER = 1.5;
    private final String SPECIAL_EVENT_NAME = "Christmas";

    /**
     * The instance, so we can request the time left.
     */
    private static CheckPendingDonationsTask INSTANCE;

    public CheckPendingDonationsTask() {
        super(Time.FIVE_MINUTES);
        if(INSTANCE != null)
            throw new IllegalStateException("There is already an instance of " + getClass().getSimpleName() + " running.");
        INSTANCE = this;
    }

    @Override
    public void execute() {
        if (!DbHub.initialized() || !DbHub.getDonationsDb().isInitialized()){
            stop();
            return;
        }

        World.getPlayers().stream().filter(player -> player != null).forEach(player -> {
            List<Donation> donations = DbHub.getDonationsDb().donations().unfinished(player);
            if (donations == null || donations.isEmpty()) {
                return;
            }
            int processedCount = 0;
            int totalDollars = 0;
            int totalPoints = 0;
            for (final Donation d : donations) {
                if (!DbHub.getDonationsDb().donations().finish(d))
                    continue;
                if (d.method().equals("survey")) {
                    player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + d.points());
                    player.sendf("%,d Donator Points have been added to your account for surveys", d.points());
                } else {
                    if (d.points() > 0) {
                        player.sendf("Donation Processed! Order ID: %s | $%,d (%,d Donator Points)", d.tokenId(), d.dollars(), d.points());
                        totalDollars += d.dollars();
                        totalPoints += d.points();
                    } else if (d.points() != 0) {
                        player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() - d.points());
                        player.sendf("%,d Donator Points have been deducted from your account!", d.points());
                    }
                }
                ++processedCount;
            }
            if (totalPoints == 0) {
                if (processedCount != donations.size())
                    player.sendf("There was an error processing all of your donations");
                else
                    player.sendf("You don't have any pending donations! Type ::donate to donate");
                return;
            }
            player.getPoints().setDonatorPoints((int) Math.round(player.getPoints().getDonatorPoints() + totalPoints * MULTIPLIER));
            player.getPoints().setDonatorsBought(player.getPoints().getDonatorPointsBought() + totalPoints);
            player.sendf("Alert##Thank you for donating $%,d##%,d donator points have been added to your account", totalDollars, totalPoints);
            if (MULTIPLIER > 1)
                player.sendf(SPECIAL_EVENT_NAME + " SPECIAL! You received an extra " + (MULTIPLIER - 1) * 100 + "% Donator Points for " + SPECIAL_EVENT_NAME + "!");
            check(player, Donation.DP_FOR_DONATOR, Rank.DONATOR);
            check(player, Donation.DP_FOR_SUPER_DONATOR, Rank.SUPER_DONATOR);
            if (processedCount != donations.size()) {
                player.sendf("Note: Not all of your pending donations were processed!");
                player.sendf("Please contact an administrator if you believe there is an error");
            }
        });
    }

    private static void check(final Player player, final int requirement, final Rank rank) {
        if(player.getPoints().getDonatorPointsBought() < requirement || Rank.hasAbility(player, rank))
            return;
        player.setPlayerRank(Rank.addAbility(player, rank));
        player.setPlayerRank(Rank.setPrimaryRank(player, rank));
        player.sendf("You have been given %s status!", rank);
    }

    public static int getSecondLeft() {
        if(INSTANCE == null)
            return -1;
        return (int)((INSTANCE.getCountdown() * Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY)) / 1000);
    }
}
