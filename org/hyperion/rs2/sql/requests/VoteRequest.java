package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */
public class VoteRequest extends SQLRequest {

    /**
     * The votes table name.
     */
    public static final String VOTES_TABLE = Server.getConfig().getString("votestable");


    /**
     * Constructs a new vote request.
     *
     * @param player
     */
    public VoteRequest(Player player) {
        super(SQLRequest.VOTE_REQUEST);
        super.setPlayer(player);
    }

    public void process(final SQLConnection sql) {
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
                final int freeSlots = player.getInventory().freeSlots();
                if(freeSlots >= thisClaim) {
                    player.getInventory().add(new Item(3062, thisClaim));
                } else {
                    Bank.addToBank(player, new BankItem(0, 3062, thisClaim));
                }
                player.getActionSender().sendMessage(
                        String.format("@blu@Thanks for voting! @red@%d@blu@ Vote box have been added to your %s",
                                thisClaim, (freeSlots >= thisClaim ? "inventory" : "bank"
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
