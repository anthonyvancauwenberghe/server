package org.hyperion.rs2.util;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.requests.QueryRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Jack Daniels.
 */
public class NewcomersLogging {

	/**
	 * The newcomers table name.
	 */
	public static final String NEWCOMERS_TABLE_NAME = "newcomersips";

	/**
	 * The player ips table name.
	 */
	public static final String PLAYER_IPS_TABLE_NAME = "playersips";

	/**
	 * The sql connection.
	 */
	private SQLConnection sql = World.getWorld().getLogsConnection();

	/**
	 * Holds the counter for newcomers.
	 */
	private int counter = 0;

	/**
	 * All IP's are stoped here.
	 */
	private HashMap<String, Object> ips = new HashMap<String, Object>();

	/**
	 * Singleton
	 */
	private static NewcomersLogging singleton = new NewcomersLogging();

	/**
	 * Use this to access the singleton.
	 *
	 * @returns singleton
	 */
	public static NewcomersLogging getLogging() {
		return singleton;
	}

	/**
	 * Gets the amount of newcomers.
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * Updates Newcomers logging when the specified player logs in.
	 *
	 * @param player
	 */
	public void loginCheck(Player player) {
		String ip = formatIp(player.getFullIP());
		if(! ips.containsKey(ip)) {
			add(ip);
			if(player.isNew()) {
				String query = "UPDATE " + NEWCOMERS_TABLE_NAME + " SET active = 1 WHERE " +
						"ip = '" + ip + "'";
				sql.offer(query);
				query = "INSERT INTO newcomers_stats (username) VALUES ('" + player.getName().toLowerCase() + "')";
				sql.offer(query);
				counter++;
			}
		}
	}


	/**
	 * Formats the IP to the format used to save newcomers ips.
	 *
	 * @param regularFormat
	 * @return
	 */
	public static String formatIp(String regularFormat) {
		return regularFormat.split(":")[0].replace("/", "");
	}

	/**
	 * Adds the IP to the logging system.
	 *
	 * @param ip
	 */
	public void add(String ip) {
		ips.put(ip, new Object());
		//writeLog(ip);
		String query = "INSERT INTO `" + PLAYER_IPS_TABLE_NAME + "`(`ip`) VALUES ('" + ip + "');";
		sql.offer(new QueryRequest(query));
	}

	/**
	 * Initializes the
	 *
	 * @throws SQLException
	 */
	public void init() {
		try {
			if(false) {
			long start = System.currentTimeMillis();
			ResultSet rs = sql.query("SELECT * FROM " + PLAYER_IPS_TABLE_NAME + " WHERE 1");
			if(rs == null)
				return;
			while(rs.next()) {

				String ip = rs.getString("ip");
				ips.put(ip, new Object());
			}
			long delta = System.currentTimeMillis() - start;
			System.out.println("Loaded NewcomersLogging in: " + delta + " ms.");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the specified <code>ip</code> to the Log file.
	 * @param ip
	 */
	/*private void writeLog(String ip) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE,true));
			bw.write(ip);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Loads all existing IP's from the Log file.
	 */
	/*private void loadLog() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(LOG_FILE));
			String line;
			while((line = br.readLine()) != null) {
				ips.put(line.replaceAll("/",""), new Object());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	static {
		/**
		 * Adds the command to the command handler
		 */
		CommandHandler.submit(new Command("howmanynewcomers", Rank.DEVELOPER) {
			@Override
			public boolean execute(Player player, String input) {
				player.getActionSender().sendMessage("Newcomers: " + getLogging().getCounter());
				return true;
			}
		});
	}
}
