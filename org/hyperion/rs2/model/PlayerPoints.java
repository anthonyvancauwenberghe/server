package org.hyperion.rs2.model;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import org.hyperion.Server;
import org.hyperion.rs2.model.combat.EloRating;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

public class PlayerPoints {

	/**
	 * The player.
	 */
	private Player player;

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
	 * The Elo Rating.
	 */
	private int eloRating = EloRating.DEFAULT_ELO_START_RATING;
	
	/**
	 * ELO Max
	 * @param opponentRating
	 * @param resultType
	 */
	
	private int eloPeak =EloRating.DEFAULT_ELO_START_RATING;

	public void updateEloRating(int opponentRating, int resultType) {
		setEloRating(EloRating.getNewRating(eloRating, opponentRating, resultType));
		String message = "@blu@You have defeaten an opponent with elo " + opponentRating + ", your new elo is: " + eloRating;
		message = message.replaceAll("elo", "PvP Rating");
		if(resultType == EloRating.WIN)
			player.getActionSender().sendMessage(message);
		else if(resultType == EloRating.LOSE)
			player.getActionSender().sendMessage(message.replace("have defeaten", "were defeaten by"));
	}

	public void setEloRating(int rating) {
		this.eloRating = rating;
		if(this.eloPeak < eloRating)
			eloPeak = Integer.valueOf(eloRating);
		Rank newRank = eloRating >= 2200 ? Rank.LEGEND : eloRating >= 1900 ? Rank.HERO : Rank.PLAYER;
		if(newRank == Rank.PLAYER) {
			if(Rank.getPrimaryRank(player) == Rank.LEGEND || Rank.getPrimaryRank(player) == Rank.HERO)
				player.setPlayerRank(Rank.setPrimaryRank(player, Rank.PLAYER));

			if(Rank.hasAbility(player, Rank.HERO)) {
				player.setPlayerRank(Rank.removeAbility(player, Rank.LEGEND));
				player.setPlayerRank(Rank.removeAbility(player, Rank.HERO));
				player.getActionSender().sendMessage("Your elo has dropped below the required threshold");
				player.getActionSender().sendMessage("you have been stripped of your master/grandmaster title and abilities.");
			}
		} else {
            //annoying seeing this all the time
            if(!Rank.hasAbility(player, newRank))
                player.getActionSender().sendMessage("Congratulations! You have received: "+newRank.toString());
			if(Rank.getPrimaryRank(player) == Rank.PLAYER) {
                player.setPlayerRank(Rank.addAbility(player, newRank));
			} else
				player.setPlayerRank(Rank.addAbility(player, newRank));
		}
		player.getQuestTab().sendAllInfo();
		player.getQuestTab().sendElo();
	}

	/**
	 * @param player
	 */
	public PlayerPoints(Player player) {
		this.player = player;
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
	public void increaseDonatorPoints(int amount, boolean bought) {
		if(amount > 200000)
			return;
		donatorPoints += amount;
		if(bought) {
			donatorPointsBought += amount;
			if(donatorPointsBought >= 10000)
				player.setPlayerRank(Rank.addAbility(player, Rank.SUPER_DONATOR));
			if(donatorPointsBought >= 1500)
				player.setPlayerRank(Rank.addAbility(player, Rank.DONATOR));
            try{
                final File f = new File("./data/donate.txt");
                if(!f.exists())
                    f.createNewFile();
                final FileWriter writer = new FileWriter(f, true);
                writer.write(String.format(
                        "\nplayer=%s, oldDpBought=%d, amount=%d, newDpBought=%d, time=%s",
                        player.getName(), donatorPointsBought - amount, amount, donatorPointsBought, new Date(System.currentTimeMillis())
                ));
                writer.flush();
                writer.close();
            }catch(Exception ex){
                System.out.println("Error saving donor points change: " + ex);
            }
		}
		player.getActionSender().sendMessage("You have been given " + amount + " donator points.");
		player.getQuestTab().sendDonatePoints();
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
	public void increaseDonatorPoints(int amount) {
		increaseDonatorPoints(amount, true);
	}

	public void setDonatorPoints(int am) {
		donatorPoints = am;
		player.getQuestTab().sendDonatePoints();
	}

	public void setDonatorsBought(int am) {
		donatorPointsBought = am;
	}

	/**
	 * @param value
	 */
	public void setHonorPoints(int value) {
		this.honorPoints = value;
		player.getQuestTab().sendHonorPoints();
	}

	/**
	 * @param points
	 */
	public void setVotingPoints(int points) {
		votingPoints = points;
		player.getQuestTab().sendVotePoints();
	}

	public void increaseVotingPoints(int times) {
		int toAdd = 0;
		//int maxpoints = Server.getConfig().getInteger("votepoints") - 1;
        int maxpoints = 2;
		for(int i = 0; i < times; i++) {
			toAdd += Misc.random(maxpoints) + 1;
		}
		votingPoints += toAdd;
		player.getActionSender().sendMessage("Your voting points have been increased by " +
				toAdd + ", you now have " + votingPoints + " voting points!");
		player.getQuestTab().sendVotePoints();
	}


	/**
	 * Sets the amount of Pk Points, updating the quest tab.
	 *
	 * @param points
	 */
	public void setPkPoints(int points) {
		pkPoints = points;
		player.getQuestTab().sendPkPoints();
	}

	public void inceasePkPoints(int points) {
		increasePkPoints(points, true);
	}
	
	public void increasePkPoints(int points, boolean message) {
		pkPoints += points;
		if(message)
		player.getActionSender().sendMessage("Your " + Server.NAME + " points have been increased by "
				+ points + "!");
		player.getQuestTab().sendPkPoints();
	}

	public void loginCheck() {
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - player.getPreviousSessionTime();
		if(delta < Time.ONE_DAY) {
			//System.out.println("Passed through one day check");
			if(currentTime - player.getLastHonorPointsReward() > Time.ONE_HOUR * 12) {
				//System.out.println("Passed through reward check");
				double reward = 0;
				if(eloRating > 1500) {
					reward = (1511.26 / ((1 + 1639.28 * Math.pow(Math.E, - 0.00412 * eloRating)))) / 7;
				} else {
					reward = (0.22 * eloRating + 14) / 7;
				}
                double multiplier = 1.50;
                reward *= multiplier;
				honorPoints += (int) reward;

				player.getActionSender().sendMessage("@blu@You have been awarded " + (int) reward + " honor points!");
                player.getActionSender().sendMessage("You have received a 25% honor point bonus from santa!");
				player.getQuestTab().sendHonorPoints();
				player.setLastHonorPointsReward(System.currentTimeMillis());
			}
		} else if(delta > (Time.ONE_DAY * 1.5)) {
			int days = (int) (delta / Time.ONE_DAY);
			honorPoints -= days * 10;
			if(honorPoints < 0)
				honorPoints = 0;
			player.getActionSender().sendMessage("@blu@You've lost Honor Points due to inactivity!");
			player.getQuestTab().sendHonorPoints();
			player.setLastHonorPointsReward(System.currentTimeMillis());
		}
	}

	public void setEloPeak(int elo) {
		eloPeak = elo;
	}
	
	/**
	 * @return
	 */
	public int getEloRating() {
		return eloRating;
	}
	
	public int getEloPeak() {
		return eloPeak;
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
	 * @return
	 */
	public int getVotingPoints() {
		return votingPoints;
	}

	/**
	 * @return
	 */
	public int getHonorPoints() {
		return honorPoints;
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

}
