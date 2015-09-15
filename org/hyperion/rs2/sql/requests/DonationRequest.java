package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.util.TextUtils;

import java.sql.ResultSet;

/**
 * @author Arsen Maxyutov.
 */
public class DonationRequest extends SQLRequest {

	/**
	 * Constructs a new DonationRequest.
	 */
	public DonationRequest(Player player) {
		super(SQLRequest.DONATION_REQUEST);
		super.setPlayer(player);
	}

	/**
	 * Processes ::getpoints command
	 *
	 * @param
	 */
	@Override
	public void process(final SQLConnection sql) {
		try {
			if(!sql.isConnected()) {
				World.getWorld().submit(new Event(0, "Reconnecting SQL") {
					@Override
					public void execute() {
						synchronized(sql) {
							if(!sql.isConnected())
								sql.establishConnection();
						}
					}
				});
				player.getActionSender().sendMessage("Donations are offline right now. Try again later.");
				return;
			}

			String name = player.getName().toLowerCase().replaceAll("_", " ");
			ResultSet rs = sql.query("SELECT * FROM donator WHERE finished = 0 AND name = '" + name + "'");
			if(rs == null) {
				player.getActionSender().sendMessage("There are no points available.");
				return;
			}
			int amount = 0;
            boolean didSurvey = false;
			int donations = 0;
			String orderId = "";

			while(rs.next()) {
				String amountString = rs.getString("amount");
				try {
					int toAdd = (int) (Double.parseDouble(amountString) * 100);

                    if(rs.getString("method").equals("survey")){
                        player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + toAdd);
                        player.sendServerMessage(toAdd + " donator points have been added to your account for surveys");
                        didSurvey = true;
                    } else {
						amount += toAdd;
						if(donations == 0) {
							orderId = rs.getString("TOKEN_ID");
						}
						donations++;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			sql.query("UPDATE donator SET finished = 1 WHERE name = '"
					+ name + "'");
			if(amount >= 200000) {
				sql.stopRunning();
				final String hackerName = player.getName();
				World.getWorld().submit(getHackEvent(hackerName));
				return;
			}
			if(amount < 0) {
                player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + amount);
			}
			if(amount > 0) {
				if(donations == 1) {
					player.sendf("Alert##You have received your points from 1 donation.##Order ID: %s##Amount: $%d (%,d donator points)", orderId, amount/100, amount);
				} else {
					player.sendf("Alert##You have received your points from %d donations.##Total amount: $%d (%,d donator points)", donations, amount/100, amount);
				}
				player.getPoints().increaseDonatorPoints(amount);
				if(!Rank.isStaffMember(player)) {
					if (Rank.hasAbility(player, Rank.DONATOR) && !Rank.hasAbility(player, Rank.SUPER_DONATOR))
						Rank.setPrimaryRank(player, Rank.DONATOR);
					else if (Rank.hasAbility(player, Rank.SUPER_DONATOR))
						Rank.setPrimaryRank(player, Rank.SUPER_DONATOR);
				}
			} else {
				if(!didSurvey)
                    player.getActionSender().sendMessage("There are no points available. It can take up to 24h to receive your points!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static Event getHackEvent(final String name) {
		return new Event(1000) {
			@Override
			public void execute() {
				System.out.println("Hack attempt, too many donatorpoints by player " + name);
			}
		};
	}


}
