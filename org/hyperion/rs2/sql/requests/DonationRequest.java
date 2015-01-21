package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

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
			if(! sql.isConnected()) {
				World.getWorld().submit(new Event(0, "Reconnecting SQL") {
					@Override
					public void execute() {
						synchronized(sql) {
							if(!sql.isConnected())
								sql.establishConnection();
						}
					}
				});
				player.getActionSender().sendMessage(
						"Your request could not be processed, Attempting to fix this, Please Try again later.");
				return;
			}
			String name = player.getName().toLowerCase().replaceAll("_", " ");
			ResultSet rs = sql.query("SELECT * FROM donator WHERE finished = 0 AND name = '"
					+ name + "'");
			if(rs == null) {
				player.getActionSender().sendMessage("There are no points available. It can take up to 24h to receive your points!");
				return;
			}
			int amount = 0;
            boolean didSurvey = false;
			while(rs.next()) {
				String amountString = rs.getString("amount");
				try {
					int toAdd = (int) (Double.parseDouble(amountString) * 100);
                    if(rs.getString("method").equals("survey")){
                        player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + toAdd);
                        player.sendf("%,d donator points have been added to your account for surveys", toAdd);
                        didSurvey = true;
                    }else
                        amount += toAdd;
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
				player.getActionSender().sendMessage(Math.abs(amount) + " donator points have been removed from your account.");
				player.getQuestTab().sendDonatePoints();
			}
			if(amount > 0) {
				player.getPoints().increaseDonatorPoints(amount);
                //player.getActionSender().sendMessage("You have received a 25% donation bonus from santa!");
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
				System.out.println("Hack Attempt too many donatorpoints!!! " + name);
			}
		};
	}


}
