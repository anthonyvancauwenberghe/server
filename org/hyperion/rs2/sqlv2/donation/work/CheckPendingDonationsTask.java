package org.hyperion.rs2.sqlv2.donation.work;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.sqlv2.db.DbConfig;
import org.hyperion.rs2.sqlv2.donation.Donation;
import org.hyperion.rs2.task.Task;

import java.util.List;

public class CheckPendingDonationsTask implements Task {

    private final Player player;

    public CheckPendingDonationsTask(final Player player) {
        this.player = player;
    }

    @Override
    public void execute(final GameEngine context) {
        List<Donation> donations = null;
        if(DbHub.initialized() && DbHub.donationsDb().enabled())
            donations = DbHub.donationsDb().donations().unfinished(player);
        if(donations == null){
            if(DbConfig.playerDebug)
                player.sendf("Unable to retrieve donation info at this time. Try again later.");
            return;
        }
        if(donations.isEmpty()){
            player.sendf("You don't have any pending donations! Type ::donate to donate");
            return;
        }
        int processedCount = 0;
        int totalDollars = 0;
        int totalPoints = 0;
        for(final Donation d : donations){
            if(!DbHub.donationsDb().donations().finish(d))
                continue;
            if(d.method().equals("survey")){
                player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + d.points());
                player.sendf("%,d Donator Points have been added to your account for surveys", d.points());
                player.getQuestTab().sendDonatePoints();
            }else{
                if(d.points() > 0){
                    player.sendf("Donation Processed! Order ID: %s | $%,d (%,d Donator Points)", d.tokenId(), d.dollars(), d.points());
                    totalDollars += d.dollars();
                    totalPoints += d.points();
                }else if(d.points() != 0){
                    player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() - d.points());
                    player.sendf("%,d Donator Points have been deducted from your account!", d.points());
                    player.getQuestTab().sendDonatePoints();
                }
            }
            ++processedCount;
        }
        if(totalPoints == 0){
            if(processedCount != donations.size())
                player.sendf("There was an error processing all of your donations");
            else
                player.sendf("You don't have any pending donations! Type ::donate to donate");
            return;
        }
        player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + totalPoints);
        player.getPoints().setDonatorsBought(player.getPoints().getDonatorPointsBought() + totalPoints);
        player.getQuestTab().sendDonatePoints();
        player.sendf("Alert##Thank you for donating $%,d##%,d donator points have been added to your account", totalDollars, totalPoints);
        check(player, Donation.DP_FOR_DONATOR, Rank.DONATOR);
        check(player, Donation.DP_FOR_SUPER_DONATOR, Rank.SUPER_DONATOR);
        if(processedCount != donations.size()){
            player.sendf("Note: Not all of your pending donations were processed!");
            player.sendf("Please contact an administrator if you believe there is an error");
        }
    }

    private static void check(final Player player, final int requirement, final Rank rank) {
        if(player.getPoints().getDonatorPointsBought() < requirement || Rank.hasAbility(player, rank))
            return;
        player.setPlayerRank(Rank.addAbility(player, rank));
        player.setPlayerRank(Rank.setPrimaryRank(player, rank));
        player.getQuestTab().sendRankInfo();
        player.sendf("You have been given %s status!", rank);
    }
}
