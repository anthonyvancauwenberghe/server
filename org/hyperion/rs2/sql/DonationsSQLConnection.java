package org.hyperion.rs2.sql;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.event.impl.KeepConnectionAliveEvent;
import org.hyperion.rs2.sql.requests.DonationRequest;
import org.hyperion.rs2.sql.requests.VoteRequest;
import org.hyperion.util.Time;

public class DonationsSQLConnection extends MySQLConnection {

	public DonationsSQLConnection(Configuration config) {
		super("DonationsSQL", config.getString("donationsurl"), config.getString("donationsuser"), config.getString("donationspass"), 30000, 3000, 300);
	}

	public DonationsSQLConnection(String url, String username, String password) {
		super("DonationsSQL", url, username, password, 30000, 3000, 300);
	}

	@Override
	public boolean init() {
		if(! Server.getConfig().getBoolean("sql"))
			return false;
		establishConnection();
		try {
			submit(new KeepConnectionAliveEvent());
			start();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		Command voteCommand = new Command("voted", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				long lastTime = player.getExtraData().getLong("lastsql");
				if(System.currentTimeMillis() - lastTime < Time.ONE_SECOND * 20) {
					player.getActionSender().sendMessage("You may only use this command every 20 seconds!");
					return false;
				}
				World.getWorld().getDonationsConnection()
						.offer(new VoteRequest(player));
				// System.out.println("Voted for: " + player.getName());
				player.getExtraData().put("lastsql", System.currentTimeMillis());
				return true;
			}
		};
		Command donationCommand = new Command("getpoints",
				Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				long lastTime = player.getExtraData().getLong("lastsql");
				if(System.currentTimeMillis() - lastTime < Time.ONE_SECOND * 20) {
					player.getActionSender().sendMessage("You may only use this command every 20 seconds!");
					return false;
				}
				World.getWorld().getDonationsConnection()
						.offer(new DonationRequest(player));
				player.getExtraData().put("lastsql", System.currentTimeMillis());
				return true;
			}
		};
		CommandHandler.submit(voteCommand, donationCommand);

		return true;
		/*
		 * CommandHandler.submit(new Command("debugsql", Command.ADMIN_RIGHTS) {
		 * 
		 * @Override public void execute(Player player, String input) {
		 * SQL.getSQL().changeDebug(); player.getActionSender().sendMessage(
		 * "SQL debugging is now : " + getSQL().debug); } });
		 */
		/*
		 * CommandHandler.submit(new Command("stopsql", Command.ADMIN_RIGHTS) {
		 * 
		 * @Override public void execute(Player player, String input) {
		 * player.getActionSender().sendMessage("SQL has been stopped.");
		 * SQL.getSQL().stopRunning(); } });
		 */
		/*
		 * CommandHandler.submit(new Command("resetsql", Command.ADMIN_RIGHTS) {
		 * 
		 * @Override public void execute(Player player, String input) {
		 * player.getActionSender().sendMessage(
		 * "Interrupting, resetting and restarting.."); getSQL().stopRunning();
		 * resetSQLObject(); getSQL().start();
		 * player.getActionSender().sendMessage("Done!"); } });
		 */
	}

}
