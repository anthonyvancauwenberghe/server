package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlayerPoints {

    /**
     * The player.
     */
    private final Player player;

    /**
     * The Pk Points.
     */
    private int pkPoints = 0;

    /**
     * The voting Points.
     */
    private int votingPoints = 0;

    /**
     * The honor Points.
     */
    private int honorPoints = 0;

    /**
     * The donator Points bought.
     */
    private int donatorPointsBought = 0;

    /**
     * The donator Points at the current moment.
     */
    private int donatorPoints = 0;

    /**
     * The minigame points at the moment
     */
    private int minigamePoints = 0;

    /**
     * The Elo Rating.
     */
    private int eloRating = EloRating.DEFAULT_ELO_START_RATING;

    /**
     * ELO Max
     *
     * @param opponentRating
     * @param resultType
     */

    private int eloPeak = EloRating.DEFAULT_ELO_START_RATING;

    /**
     * @param player
     */
    public PlayerPoints(final Player player) {
        this.player = player;
    }

    public void updateEloRating(final int opponentRating, final int resultType) {
        setEloRating(EloRating.getNewRating(eloRating, opponentRating, resultType));
        final String message = "You have defeaten an opponent with PvP Rating " + opponentRating;
        final String message2 = "Your new PvP Rating is: " + eloRating;
        if(resultType == EloRating.WIN){
            player.sendPkMessage(message);
            player.sendPkMessage(message2);
        }else if(resultType == EloRating.LOSE)
            player.sendPkMessage(message.replace("have defeaten", "were defeaten by"));
    }

    /**
     * Increases the players donator points and his
     * donatorpoints bought. Incase the donators bought amount has
     * reached certain limits, the player can receive special donator status.
     * This also notifies the player that his donatorpoints have been increased
     * and updates this to his questtab.
     *
     * @param amount
     */
    public void increaseDonatorPoints(final int amount, final boolean bought) {
        if(amount > 200000)
            return;
        donatorPoints += amount;
        if(bought){
            donatorPointsBought += amount;
            checkDonator();
            try{
                final File f = new File("./data/donate.txt");
                if(!f.exists())
                    f.createNewFile();
                final FileWriter writer = new FileWriter(f, true);
                writer.write(String.format("\nplayer=%s, oldDpBought=%d, amount=%d, newDpBought=%d, time=%s", player.getName(), donatorPointsBought - amount, amount, donatorPointsBought, new Date(System.currentTimeMillis())));
                writer.flush();
                writer.close();
            }catch(final Exception ex){
                System.out.println("Error saving donor points change: " + ex);
            }
        }
        player.getExpectedValues().changeDeltaOther("Donator points added", amount);
        player.sendServerMessage("You have been given " + amount + " donator points.");
        player.getQuestTab().sendDonatePoints();
    }

    public void checkDonator() {
        if(donatorPointsBought >= 10000)
            player.setPlayerRank(Rank.addAbility(player, Rank.SUPER_DONATOR));
        if(donatorPointsBought >= 2000)
            player.setPlayerRank(Rank.addAbility(player, Rank.DONATOR));
    }

    /**
     * Increases the players donator points and his
     * donatorpoints bought. Incase the donators bought amount has
     * reached certain limits, the player can receive special donator status.
     * This also notifies the player that his donatorpoints have been increased
     * and updates this to his questtab.
     *
     * @param amount
     */
    public void increaseDonatorPoints(final int amount) {
        increaseDonatorPoints(amount, true);
    }

    public void setDonatorsBought(final int am) {
        donatorPointsBought = am;
    }

    public void increaseVotingPoints(final int times) {
        int toAdd = 0;
        //int maxpoints = Server.getConfig().getInteger("votepoints") - 1;
        final int maxpoints = 1;
        for(int i = 0; i < times; i++){
            toAdd += Misc.random(maxpoints) + 1;
        }
        votingPoints += toAdd;
        player.getExpectedValues().changeDeltaOther("Voting points added", toAdd);
        player.sendServerMessage("Your voting points have been increased by " +
                toAdd + ", you now have " + votingPoints + " voting points!");
        player.getQuestTab().sendVotePoints();
    }

    public int increaseMinigamePoints(final int times) {
        minigamePoints += times;
        return minigamePoints;
    }

    public int setMinigamePoints(final int amount) {
        return minigamePoints = amount;
    }

    public void increasePkPoints(final int points) {
        increasePkPoints(points, true);
    }

    public void increasePkPoints(final int points, final boolean message) {
        pkPoints += points;
        if(message)
            player.sendPkMessage("Your " + Server.NAME + " points have been increased by " + points + "!");
        player.getQuestTab().sendPkPoints();
    }

    public void loginCheck() {
        final long currentTime = System.currentTimeMillis();
        final long delta = currentTime - player.getPreviousSessionTime();
        if(delta < Time.ONE_DAY){
            //System.out.println("Passed through one day check");
            if(currentTime - player.getLastHonorPointsReward() > Time.ONE_HOUR * 12){
                //System.out.println("Passed through reward check");
                double reward = 0;
                if(eloRating > 1500){
                    reward = (1511.26 / ((1 + 1639.28 * Math.pow(Math.E, -0.00412 * eloRating)))) / 7;
                }else{
                    reward = (0.22 * eloRating + 14) / 7;
                }
                if(Rank.hasAbility(player, Rank.SUPER_DONATOR))
                    reward *= 1.25;
                honorPoints += (int) (reward);

                player.sendPkMessage("You have been awarded " + (int) reward + " honor points!");
                player.getQuestTab().sendHonorPoints();
                player.setLastHonorPointsReward(System.currentTimeMillis());
            }
        }else if(delta > (Time.ONE_DAY * 1.5)){
            final int days = (int) (delta / Time.ONE_DAY);
            honorPoints -= days * 10;
            if(honorPoints < 0)
                honorPoints = 0;
            player.sendPkMessage("You've lost honor points due to inactivity!");
            player.getQuestTab().sendHonorPoints();
            player.setLastHonorPointsReward(System.currentTimeMillis());
        }
        player.getValueMonitor().setStartValues(player.getAccountValue().getTotalValue(), player.getAccountValue().getPkPointValue());
    }

    /**
     * @return
     */
    public int getEloRating() {
        return eloRating;
    }

    public void setEloRating(final int rating) {
        this.eloRating = rating;
        if(this.eloPeak < eloRating)
            eloPeak = Integer.valueOf(eloRating);
        final Rank newRank = eloRating >= 2200 ? Rank.LEGEND : eloRating >= 1900 ? Rank.HERO : Rank.PLAYER;
        if(newRank == Rank.PLAYER){
            if(Rank.getPrimaryRank(player) == Rank.LEGEND || Rank.getPrimaryRank(player) == Rank.HERO)
                player.setPlayerRank(Rank.setPrimaryRank(player, Rank.PLAYER));

            if(Rank.hasAbility(player, Rank.HERO)){
                player.setPlayerRank(Rank.removeAbility(player, Rank.LEGEND));
                player.setPlayerRank(Rank.removeAbility(player, Rank.HERO));
                player.sendPkMessage("Your PvP rating has dropped below the required threshold...");
                player.sendPkMessage("You have been stripped of your master/grandmaster title and abilities.");
            }
        }else{
            //annoying seeing this all the time
            if(!Rank.hasAbility(player, newRank))
                player.sendPkMessage("Congratulations! You have received " + newRank.toString() + "!");
            if(Rank.getPrimaryRank(player) == Rank.PLAYER){
                player.setPlayerRank(Rank.addAbility(player, newRank));
            }else
                player.setPlayerRank(Rank.addAbility(player, newRank));
        }
        player.getQuestTab().updateQuestTab();
    }

    public int getEloPeak() {
        return eloPeak;
    }

    public void setEloPeak(final int elo) {
        eloPeak = elo;
    }

    /**
     * Gets the amount of Pk Points.
     *
     * @return
     */
    public int getPkPoints() {
        return pkPoints;
    }

    /**
     * Sets the amount of Pk Points, updating the quest tab.
     *
     * @param points
     */
    public void setPkPoints(final int points) {
        pkPoints = points;
        player.getQuestTab().sendPkPoints();
    }

    /**
     * @return
     */
    public int getVotingPoints() {
        return votingPoints;
    }

    /**
     * @param points
     */
    public void setVotingPoints(final int points) {
        player.getExpectedValues().changeDeltaOther("Voting points set", points - votingPoints);
        votingPoints = points;
        player.getQuestTab().sendVotePoints();
    }

    /**
     * @return
     */
    public int getHonorPoints() {
        return honorPoints;
    }

    /**
     * @param value
     */
    public void setHonorPoints(final int value) {
        this.honorPoints = value;
        player.getQuestTab().sendHonorPoints();
    }

    /**
     * @return
     */
    public int getDonatorPointsBought() {
        return donatorPointsBought;
    }

    /**
     * @return
     */
    public int getDonatorPoints() {
        return donatorPoints;
    }

    public void setDonatorPoints(final int am) {
        player.getExpectedValues().changeDeltaOther("Donator points set", am - donatorPoints);
        donatorPoints = am;
        player.getQuestTab().sendDonatePoints();
    }

    public int getMinigamePoints() {
        return minigamePoints;
    }

    public int pkpBonus(final int originalPkp) {
        long minutes = TimeUnit.MINUTES.convert(player.getTotalOnlineTime(), TimeUnit.MILLISECONDS);
        //System.out.println("Minutes: "+minutes);
        if(minutes <= 10)
            minutes = 10;
        if(minutes > 100)
            return originalPkp;
        final double max_increase = 10.0;
        final double modifier = max_increase / (minutes / 10D);
        player.sendPkMessage("You get" + String.format("%.1f", modifier) + "x Pk points bonus for being new!");
        return (int) (originalPkp * modifier);
    }

}
