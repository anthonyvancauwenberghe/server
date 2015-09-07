package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.bank.Bank;
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
 */
public class VoteRequest extends SQLRequest {

    /**
     * The votes table name.
     */
    public static final String VOTES_TABLE = Server.getConfig().getString("votestable");
    private static int bonus = -1;
    private static int votingPoints = 0;
    private static int streak;


    /**
     * Constructs a new vote request.
     *
     * @param player
     */
    public VoteRequest(Player player) {
        super(SQLRequest.VOTE_REQUEST);
        super.setPlayer(player);
    }

    private void doBonus() {
        switch(bonus) {
            case 0:
                player.sendMessage("... And get double voting points!");
                votingPoints *= 2;
                break;
            case 1:
                long time;
                switch(streak) {
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
                player.getExtraData().put("doubleExperience", System.currentTimeMillis() + time);
                player.sendMessage("... And received double experience for " + time/Time.ONE_MINUTE + " minutes!");
                break;
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
                player.getExtraData().put("increasedDroprate", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getExtraData().put("dropRateMultiplier", multiplier);
                player.sendMessage("... And received increased droprates for one hour!");
                break;
            case 3:
                double reducement;
                switch(streak) {
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
                player.getExtraData().put("loweredYellTimer", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getExtraData().put("yellReduction", reducement);
                player.sendMessage("... And received a reduced yelldelay for one hour!");
                break;
            case 4:
                if(Misc.random(50/streak) == 1) {
                    int donatorPoints = 0;
                    player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + donatorPoints);
                    player.sendMessage("... And receive " + donatorPoints + " donator points as a rare bonus!");
                    for(Player p : World.getWorld().getPlayers()) {
                        p.sendServerMessage(player.getSafeDisplayName() + " has just received " + donatorPoints + " donator points for voting!");
                    }
                } else {
                    doBonus();
                }
                break;
        }
    }

    public void process(final SQLConnection sql) {
        /*
        if (!sql.isConnected()) {
            player.getActionSender().sendMessage("Voting is offline right now. Try again later.");
            return;
        }
        int currentStreak = player.getPermExtraData().getInt("votingStreak");
        boolean voted = false;

        ResultSet rs = null;
        try {
            //If voted is 1, it means they voted for at least one site
            rs = sql.query(String.format("SELECT * FROM waitingVotes WHERE realUsername = '%s' AND voted = 1", player.getName()));
            while (rs.next()) {
                final boolean runelocus = rs.getByte("runelocus") == 1;
                final boolean topg = rs.getByte("topg") == 1;
                final boolean top100 = rs.getByte("top100") == 1;
                //If the player didn't vote for any of them
                if (!runelocus && !topg && !top100)
                    continue;
                //If player voted on all 3 he receives a bonus
                if (runelocus && topg && top100) {
                    bonus = Misc.random(4);

                    //This will check if they voted yesterday too, if so; receive streak.
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    String yesterday = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());

                    if (player.getPermExtraData().getString("lastVoted").equalsIgnoreCase(yesterday)) {
                        player.getPermExtraData().put("lastVoted", new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
                        currentStreak++;
                    }
                }

                //- When a player votes 2 days in a row his streak becomes 1.
                //- When a player votes 4 days in a row his streak becomes 2.
                //- When a player votes 7 days in a row his streak becomes 3 and he gains an achievement.
                //- When a player votes 14 days in a row his streak becomes 5 and he gains another achievement.
                //- When a player votes 31 days in a row his streak becomes 10 and he gains another achievement.

                int streak = 0;
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
                int votingPoints = 1 + streak;

                if (bonus != -1) {
                    player.sendMessage("You get a bonus for voting on all 3 voting sites...");
                    doBonus();
                }

                player.getPoints().setVotingPoints(player.getPoints().getVotingPoints() + votingPoints);
                player.sendMessage("Thank you for voting. You received " + votingPoints + " voting " + (votingPoints == 1 ? "point" : "points") + ".");
                player.setLastVoted(System.currentTimeMillis());
                player.getExtraData().put("lastVoteDate", Calendar.DATE);
                voted = true;

                //This will let the player know where he can still vote.
                if (bonus == -1) {
                    StringBuilder sb = new StringBuilder("You can still vote on ");
                    if (!runelocus)
                        sb.append("runelocus &");
                    if (!top100)
                        sb.append("top100 &");
                    if (!topg)
                        sb.append("topg");
                    if (sb.toString().endsWith(" &")) {
                        sb.replace(sb.length() - 2, sb.length(), "");
                    }
                    sb.append(".");
                    player.sendMessage(sb.toString());
                } else if (currentStreak != 0) {
                    player.sendMessage("You are now on a " + currentStreak + " " + (currentStreak == 1 ? "day" : "days") + " voting streak!");
                }
                final int rl = runelocus ? 1 : 0;
                final int t100 = top100 ? 1 : 0;
                final int tg = topg ? 1 : 0;
                sql.query(String.format(
                                "INSERT INTO votes (name, runelocus, top100, topg) VALUES ('%s', %d, %d, %d) ON DUPLICATE KEY UPDATE runelocus = runelocus + %d, top100 = top100 + %d, topg = topg + %d",
                                player.getName().toLowerCase(), rl, t100, tg, rl, t100, tg)
                );
            }
            sql.query(String.format("DELETE FROM waitingVotes WHERE realUsername!=fakeUsername AND realUsername = '%s'", player.getName()));
            sql.query(String.format("UPDATE waitingVotes SET runelocus=0,top100=0,topg=0,voted=1 WHERE realUsername ='%s'", player.getName()));
            sql.query("DELETE FROM waitingVotes WHERE fakeUsername = realUsername AND timestamp <= date_sub(now(), interval 12 hour)");
        } catch (Exception e) {
            player.getActionSender().sendMessage("Something went wrong with the voting... Try again later!");
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
        if (!voted)
            player.sendMessage("You have no votes to claim. Use ::vote to vote.");
    }


*/
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
