package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Arsen Maxyutov.
 * @author Gilles.
 */
public class VoteRequest extends SQLRequest {

    /**
     * The votes table name.
     */
    public static final SimpleDateFormat FORMAT_PLAYER = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd");
    private int bonus = -1;
    private int votingPoints = 0;
    private int streak;

    /**
     * Constructs a new vote request.
     *
     * @param player
     */
    public VoteRequest(Player player) {
        super(SQLRequest.VOTE_REQUEST);
        super.setPlayer(player);
    }

    private String doBonus() {
        switch (bonus) {
            case 0:
                votingPoints *= 2;
                return ("You get double voting reward!");
            case 1:
                long time;
                switch (streak) {
                    case 10:
                        time = 2 * Time.ONE_HOUR;
                        break;
                    case 5:
                        time = 90 * Time.ONE_MINUTE;
                        break;
                    case 3:
                        time = 1 * Time.ONE_HOUR;
                        break;
                    case 2:
                        time = 45 * Time.ONE_MINUTE;
                        break;
                    case 1:
                        time = 30 * Time.ONE_MINUTE;
                        break;
                    default:
                        time = 15 * Time.ONE_MINUTE;
                        break;
                }
                player.getPermExtraData().put("doubleExperience", System.currentTimeMillis() + time);
                return "You received double experience for " + time / Time.ONE_MINUTE + " minutes!";
            case 2:
                double multiplier;
                switch(streak) {
                    case 10:
                        multiplier = 1.5;
                        break;
                    case 5:
                        multiplier = 1.2;
                        break;
                    case 3:
                        multiplier = 1.1;
                        break;
                    case 2:
                        multiplier = 1.05;
                        break;
                    case 1:
                        multiplier = 1.02;
                        break;
                    default:
                        multiplier = 1.01;
                        break;
                }
                player.getPermExtraData().put("increasedDroprate", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getPermExtraData().put("dropRateMultiplier", multiplier);
                return "You received increased droprates for one hour!";
            case 3:
                double reducement;
                switch (streak) {
                    case 10:
                        reducement = 0.5;
                        break;
                    case 5:
                        reducement = 0.8;
                        break;
                    case 3:
                        reducement = 0.9;
                        break;
                    case 2:
                        reducement = 0.95;
                        break;
                    case 1:
                        reducement = 0.98;
                        break;
                    default:
                        reducement = 0.99;
                        break;
                }
                player.getPermExtraData().put("loweredYellTimer", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getPermExtraData().put("yellReduction", reducement);
                return "You received a reduced yelldelay for one hour!";
            case 4:
                if (Misc.random(120 / streak) == 1) {
                    int donatorPoints = 1000;
                    player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + donatorPoints);
                    for (Player p : World.getWorld().getPlayers()) {
                        p.sendServerMessage(player.getSafeDisplayName() + " has just received " + donatorPoints + " donator points for voting!");
                    }
                    return "You receive " + donatorPoints + " donator points as a rare bonus!";
                } else {
                    doBonus();
                }
                break;
        }
        return "";
    }

    public void process(final SQLConnection sql) {
        if (!sql.isConnected()) {
            World.getWorld().submit(new Event(0, "Reconnecting SQL") {
                @Override
                public void execute() {
                    synchronized (sql) {
                        if (!sql.isConnected())
                            sql.establishConnection();
                    }
                }
            });
            player.getActionSender().sendMessage("Voting is offline right now. Try again later.");
            return;
        }
        int currentStreak = player.getPermExtraData().getInt("votingStreak");
        boolean runelocus = false;
        boolean rspslist = false;
        boolean topg = false;
        int runelocusVotes = 0;
        int rspslistVotes = 0;
        int topgVotes = 0;

        ResultSet rs = null;
        try {
            rs = sql.query(String.format("SELECT * FROM waitingVotes WHERE realUsername='%s'", player.getName()));
            while (rs.next()) {
                //if it hasn't been processed yet it will simply just give them their points
                if (rs.getByte("processed") == 0) {
                    if (rs.getByte("runelocus") == 1 && rs.getByte("runelocusProcessed") == 0) {
                        runelocusVotes += 2;
                        sql.query("UPDATE waitingVotes SET runelocusProcessed=1 WHERE waitingVotes.index=" + rs.getInt("index"));
                    }
                    if (rs.getByte("topg") == 1 && rs.getByte("topgProcessed") == 0) {
                        topgVotes++;
                        sql.query("UPDATE waitingVotes SET topgProcessed=1 WHERE waitingVotes.index=" + rs.getInt("index"));
                    }
                    if (rs.getByte("rspslist") == 1 && rs.getByte("rspslistProcessed") == 0) {
                        rspslistVotes++;
                        sql.query("UPDATE waitingVotes SET rspslistProcessed=1 WHERE waitingVotes.index=" + rs.getInt("index"));
                    }
                    //Simply will set it so it won't ever be processed again, but it doesn't need deletion if it is not an old vote.
                    sql.query("UPDATE waitingVotes SET processed=1 WHERE waitingVotes.index=" + rs.getInt("index"));
                }
                //If the vote was today it will tell the loader that the user has voted for a certain site today
                if (rs.getDate("timestamp").toString().equalsIgnoreCase(FORMAT_SQL.format(Calendar.getInstance().getTime()).toString())) {
                    if (rs.getByte("runelocus") == 1)
                        runelocus = true;
                    if (rs.getByte("topg") == 1)
                        topg = true;
                    if (rs.getByte("rspslist") == 1)
                        rspslist = true;
                } else {
                    //If it's an old vote it will be removed after adding the points.
                    sql.query(String.format("DELETE FROM waitingVotes WHERE waitingVotes.index=%d", rs.getInt("index")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //Checks if the player actually voted at any point in time
        if (runelocusVotes == 0 && rspslistVotes == 0 && topgVotes == 0) {
            player.sendMessage("You have no votes to claim. Use ::vote to vote.");
            return;
        }

        //Builds the calendar to get yesterday
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = FORMAT_PLAYER.format(cal.getTime());

        //This will check if they voted yesterday too, if so; receive streak.
        String lastVoted = player.getPermExtraData().getString("lastVoted");
        //If they never voted there is nothing that should happen
        if (lastVoted != null) {
            if (lastVoted.equalsIgnoreCase(yesterday)) {
                currentStreak++;
                //On the condition that his last vote was not today it'll reset. Otherwise it means he already received his bonus today & he doesn't need a bonus anymore
            } else if (!lastVoted.equalsIgnoreCase(FORMAT_PLAYER.format(Calendar.getInstance().getTime()))) {
                player.sendMessage("Your voting streak has been reset!");
                currentStreak = 0;
            }
        }

        //If the player voted for all 3 websites today he'll receive the bonus & they get the streak bonus, depending on the streak we just calculated.
        //It will also check if he didn't receive the streak yet today
        if (runelocus && topg && rspslist && !FORMAT_PLAYER.format(Calendar.getInstance().getTime()).equalsIgnoreCase(lastVoted)) {
            player.getPermExtraData().put("lastVoted", FORMAT_PLAYER.format(Calendar.getInstance().getTime()));
            player.getAchievementTracker().voted();
            if (currentStreak >= 31) {
                streak = 10;
            } else if (currentStreak >= 14) {
                streak = 5;
            } else if (currentStreak >= 7) {
                streak = 3;
            } else if (currentStreak >= 4) {
                streak = 2;
            } else if (currentStreak >= 2) {
                streak = 1;
            }
            votingPoints += streak;

            //The bonus gets set
            bonus = Misc.random(4);
        }

        //Now it's time to get howmany voting points the player has to receive.
        votingPoints += runelocusVotes + rspslistVotes + topgVotes;

        //Now we do the bonus if the player needs one
        StringBuilder sb = new StringBuilder();
        if (runelocus && topg && rspslist) {
            sb.append(doBonus());
        } else {
            //Now all the processing is done, it's time to add the points and tell him if he can still vote for the streak
            sb.append("You can still vote on ");
            if (!runelocus)
                sb.append("Runelocus & ");
            if (!rspslist)
                sb.append("RSPSList & ");
            if (!topg)
                sb.append("TopG");
            if (sb.toString().endsWith(" & ")) {
                sb.replace(sb.length() - 3, sb.length(), "");
            }
            sb.append(".");
        }

        if (!runelocus || !rspslist || !topg) {
            player.sendf(
                    "Alert##Thank you for voting!##You received %d voting point(s)##Remember to vote on all 3 sites to %s streak!##%s",
                    votingPoints, currentStreak != 0 ? "keep your" : "get a", sb.toString());
        } else {
            if (bonus == -1) {
                player.sendMessage("Alert##Thank you for voting again!##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + " ##ArteroPK appreciates your support!");
            } else {
                if (currentStreak != 0) {
                    player.sendMessage("Alert##Thank you for voting " + currentStreak + " " + (currentStreak == 1 ? "day" : "days") + " in a row.##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + ".##" + sb.toString());
                } else {
                    player.sendMessage("Alert##Thank you for voting.##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + ".##" + sb.toString());
                }
            }
        }

        //This will add the points and update the last time the player voted
        final int freeSlots = player.getInventory().freeSlots();
        if (freeSlots >= 1) {
            player.getInventory().add(new Item(3062, votingPoints));
        } else {
            player.getBank().add(new BankItem(0, 3062, votingPoints));
            player.sendMessage((votingPoints == 1 ? "A" : votingPoints) + " Strange Box" + (votingPoints == 1 ? " has" : "es have") + " been added to your bank.");
        }
        player.setLastVoted(System.currentTimeMillis());
        player.getPermExtraData().put("votingStreak", currentStreak);
        votingPoints = 0;
        streak = 0;
        bonus = -1;

        //This will update the total amount of votes a player has done.
        final int rl = runelocusVotes;
        final int t100 = rspslistVotes;
        final int tg = topgVotes;
        try {
            sql.query(String.format("INSERT INTO votes (name, runelocus, top100, topg) VALUES ('%s', %d, %d, %d) ON DUPLICATE KEY UPDATE runelocus = runelocus + %d, top100 = top100 + %d, topg = topg + %d", player.getName().toLowerCase(), rl, t100, tg, rl, t100, tg));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*

        player.sendMessage("Attempting to retrieve vote points...");
        if (!sql.isConnected()) {
            player.getActionSender().sendMessage("Your request could not be processed. Try again later.");
            return;
        }
        int votesClaimed = 0;
        ResultSet rs = null;
        try {
            rs = sql.query(String.format("SELECT * FROM waitingVotes WHERE voted=0 AND realUsername = '%s'", player.getName()));
            while (rs.next()) {
                final boolean runelocus = rs.getByte("runelocus") == 1;
                final boolean topg = rs.getByte("topg") == 1;
                final boolean top100 = rs.getByte("top100") == 1;
                if (!runelocus && !topg && !top100) {
                    player.getActionSender().sendMessage("You haven't voted yet!");
                    continue;
                }
                if (topg && !runelocus && !top100) {
                    player.getActionSender().sendMessage("You cannot just vote on topg!");
                    continue;
                }
                int thisClaim = 0;
                if (runelocus)
                    thisClaim += 2;
                if (top100)
                    thisClaim += 1;
                if (topg)
                    thisClaim++;
                thisClaim *= 2;
                final int freeSlots = player.getInventory().freeSlots();
                if(freeSlots >= thisClaim) {
                    player.getInventory().add(new Item(3062, thisClaim));
                } else {
                    player.getBank().add(new BankItem(0, 3062, thisClaim));
                }
                player.sendServerMessage(
                        String.format("Thank you for voting! %d vote %s have been added to your %s.",
                                thisClaim, thisClaim == 1 ? "box" : "boxes", (freeSlots >= thisClaim ? "inventory" : "bank"
                                ))
                );
                player.setLastVoted(System.currentTimeMillis());
                votesClaimed += thisClaim;
                final int rl = runelocus ? 1 : 0;
                final int t100 = top100 ? 1 : 0;
                final int tg = topg ? 1 : 0;
                sql.query(String.format(
                        "INSERT INTO votes (name, runelocus, top100, topg) VALUES ('%s', %d, %d, %d) ON DUPLICATE KEY UPDATE runelocus = runelocus + %d, top100 = top100 + %d, topg = topg + %d",
                        player.getName().toLowerCase(), rl, t100, tg, rl, t100, tg)
                );
            }
            if (votesClaimed == 0) {
                player.getActionSender().sendMessage("You do not have any votes to claim");
                return;
            }
            sql.query(String.format("DELETE FROM waitingVotes WHERE realUsername!=fakeUsername AND realUsername = '%s'", player.getName()));
            sql.query(String.format("UPDATE waitingVotes SET runelocus=0,top100=0,topg=0,voted=1 WHERE realUsername ='%s'", player.getName()));
            //sql.query(String.format("UPDATE %s SET claimed = claimed + %d WHERE name = '%s'", VOTES_TABLE, votesClaimed, player.getName()));
            sql.query("DELETE FROM waitingVotes WHERE fakeUsername = realUsername AND timestamp <= date_sub(now(), interval 12 hour)");
        } catch (Exception ex) {
            player.getActionSender().sendMessage("Error processing vote request. Try again later.");
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Processes the vote request.
     */
    /*@Override
    public void process(SQLConnection sql) {
        if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
            player.getActionSender().sendMessage("processing vote before try");
		long currentTime = System.currentTimeMillis();
		try {
			if(! sql.isConnected()) {
				player.getActionSender().sendMessage(
						"Your request could not be processed. Try again later.");
				return;
			}
			String input = "SELECT * FROM " + VOTES_TABLE
					+ " WHERE name = '" + player.getName().toLowerCase() + "'";
			ResultSet rs = sql.query(input);
			if(rs == null) {
				player.getActionSender().sendMessage(
						"Your username does not have any registered votes.");
				return;
			}
			if(rs.next()) {
				int waiting = rs.getInt("waiting");
				//Comment when not betaing

				//can't believe this hasn't been commented out yet guess no owner/devs vote :-(
				//if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
					//waiting = Combat.random(50);
				if(waiting == 0) {
					player.getActionSender().sendMessage(
							"Your username does not have any registered votes.");
					return;
				}
                if(waiting > 1000) {
                    sql.stopRunning(); //dunno about this
                    final String hackerName = player.getName();
                    World.getWorld().submit(getHackEvent(hackerName));
                    return;
                }
                if(!Rank.hasAbility(player,Rank.MODERATOR) && waiting > 5){
                    waiting=5;
                }
                if(player.getFirstVoteTime() < 1 || currentTime - player.getFirstVoteTime() > Time.ONE_HOUR * 12){
                    if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
                        player.getActionSender().sendMessage("[voting] resetting first vote time + vote count to 0");
                    player.setFirstVoteTime(currentTime);
                    player.setVoteCount(0);
                }
                if(!Rank.hasAbility(player, Rank.MODERATOR) && player.getVoteCount() >= 5){
                    player.getActionSender().sendMessage("You are only allowed to claim a maximum of 5 voteboxes every 12 hours");
                    return;
                }
                //at this point, we know that waiting > 0 and voteCount < 10
                player.setLastVoted(currentTime);
                int claimed = 0;
                while(waiting > 0 && player.getVoteCount() < 10){
                    --waiting;
                    ++claimed;
                    player.setVoteCount(player.getVoteCount() + 1);
                    final int freeSlots = player.getInventory().freeSlots();
                    (freeSlots > 0 ? player.getInventory() : player.getBank()).add(new Item(3062));
                    player.getActionSender().sendMessage("@blu@Thanks for voting! Vote box added to your " + (freeSlots > 0 ? "inventory" : "bank"));
                }
				if(System.currentTimeMillis() - Server.lastServerVote > 25000) {// 10 secs
					for(Player other : World.getWorld().getPlayers()) {
						if(System.currentTimeMillis() - other.getLastVoted() > Time.ONE_HOUR * 12) {
							if(Rank.isStaffMember(other))
								continue;
							other.getActionSender().sendMessage("@or3@" + player.getName() + " has just received a voting box for voting! Type ::vote to start voting!");
						}
					}
				}
				Server.lastServerVote = System.currentTimeMillis();
                final String query = String.format(
                        "UPDATE %s SET claimed = claimed + %d, waiting = waiting - %d WHERE name = '%s'",
                        VOTES_TABLE, claimed, claimed, player.getName().toLowerCase()
                );
                sql.query(query);
                sql.query("DELETE FROM waitingVotes WHERE fakeUsername = realUsername AND timestamp <= date_sub(now(), interval 12 hour)");
				//sql.query("UPDATE " + VOTES_TABLE + " SET claimed = claimed + waiting, waiting = 0 WHERE name = '" + player.getName().toLowerCase() + "'");
			}else{
                if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
                    player.getActionSender().sendMessage("[voting] rs.next returned false");
            }
		} catch(Exception e) {
            if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
                player.getActionSender().sendMessage("Error voting: " + e);
			e.printStackTrace();
		}
	}

	public static Event getHackEvent(final String name) {
		return new Event(1000) {
			@Override
			public void execute() {
				System.out.println("Hack Attempt too many votepoints!!! " + name);
			}
		};
	}*/


}
