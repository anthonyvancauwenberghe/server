package org.hyperion.rs2.util;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.requests.QueryRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Jack Daniels.
 */
public class NewcomersLogging {


    /**
     * Singleton
     */
    private static final NewcomersLogging singleton = new NewcomersLogging();

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
        CommandHandler.submit(new Command("howmanynewcomers", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                player.getActionSender().sendMessage("Newcomers: " + getLogging().getCounter());
                return true;
            }
        });
    }

    /**
     * All IP's are stoped here.
     */
    private final HashMap<String, Object> ips = new HashMap<String, Object>();
    /**
     * Holds the counter for newcomers.
     */
    private int counter = 0;

    /**
     * Use this to access the singleton.
     *
     * @returns singleton
     */
    public static NewcomersLogging getLogging() {
        return singleton;
    }

    /**
     * Formats the IP to the format used to save newcomers ips.
     *
     * @param regularFormat
     * @return
     */
    public static String formatIp(final String regularFormat) {
        return regularFormat.split(":")[0].replace("/", "");
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
    public void loginCheck(final Player player) {
        final String ip = formatIp(player.getFullIP());
        if(!ips.containsKey(ip)){
            add(ip);
            if(player.isNew()){
                final String query = "UPDATE marketing SET active = 1 WHERE ip = '" + ip + "'";
                World.getWorld().getDonationsConnection().offer(query);
                counter++;
            }
        }
    }

    /**
     * Adds the IP to the logging system.
     *
     * @param ip
     */
    public void add(final String ip) {
        ips.put(ip, new Object());
        //writeLog(ip);
        final String query = "INSERT INTO playersips (`ip`) VALUES ('" + ip + "');";
        World.getWorld().getLogsConnection().offer(new QueryRequest(query));
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
     * Initializes the
     *
     * @throws SQLException
     */
    public void init() {
        try{
            final long start = System.currentTimeMillis();
            final ResultSet rs = World.getWorld().getLogsConnection().query("SELECT * FROM playersips");
            if(rs == null)
                return;
            while(rs.next()){
                final String ip = rs.getString("ip");
                ips.put(ip, new Object());
            }
            final long delta = System.currentTimeMillis() - start;
            System.out.println("Loaded NewcomersLogging in: " + delta + " ms.");

        }catch(final SQLException e){
            e.printStackTrace();
        }
    }
}
