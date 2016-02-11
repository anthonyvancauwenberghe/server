package org.hyperion.rs2.util;

import org.hyperion.Server;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

/**
 * @author Arsen Maxyutov.
 */
public class RestarterThread extends Thread {

	/**
	 * The minimum amount of players that should be online.
	 */
	private static final int MINIMUM_PLAYERS = Server.getConfig().getInteger("minplayers");

	/**
	 * The maximum uptime. - 2 days, nah
	 */
	private static final long MAX_UPTIME = Time.ONE_DAY;

	/**
	 * The enabled flag, changing this will stop the thread.
	 */
	private static boolean enabled = Server.getConfig().getBoolean("restarterthread");

	/**
	 * Holds the last time that a combatEntity hit another one.
	 */
	private static long lastCombat = System.currentTimeMillis();

	/**
	 * Holds the amount of cycles in a row, in which something went wrong.
	 */
	private int fails = 0;

	/**
	 * Counts the amount of cycles done.
	 */
	private int counter = 0;

	/**
	 * Holds the amount of time it should sleep every cycle, this value can be changed during process.
	 */
	private int sleep = 15000;

	/**
	 * Holds the last the the SQL thread processed a query.
	 */
	private long lastSQLUpdate = System.currentTimeMillis();
	/**
	 * The restarter thread.
	 */
	private static RestarterThread rt = new RestarterThread();

	/**
	 * Use this to access the restarter.
	 *
	 * @return
	 */
	public static RestarterThread getRestarter() {
		return rt;
	}

	/**
	 * Constructor
	 */
	public RestarterThread() {
		this.setDaemon(true);
		this.setName("RestarterThread");
		this.start();
	}

	/**
	 * Updates the last time the SQL thread processed a query.
	 */
	public void updateSQLTimer() {
		lastSQLUpdate = System.currentTimeMillis();
	}

	/**
	 * @returns Time since last SQL query process.
	 */
	public long timeSinceLastSQL() {
		return System.currentTimeMillis() - lastSQLUpdate;
	}

	/**
	 * Updates the last time that a combatEntity hit an other one.
	 */
	public void updateCombatTimer() {
		lastCombat = System.currentTimeMillis();
	}


	/**
	 * Use this to get the time since last combat.
	 *
	 * @return
	 */
	private long timeSinceLastCombat() {
		return System.currentTimeMillis() - lastCombat;
	}


	/**
	 * Checks if the server is running fine, if not, an update will happen.
	 */
	private void checkServerUptime() {
		int players = World.getPlayers().size();
		counter++;
		if(counter >= 20) {
			counter = 0;
			PlayercountLogger.getLogger().log(players);
		}
		if(players < MINIMUM_PLAYERS || timeSinceLastCombat() > 15000) {
			fails++;
			sleep = 5000;
			if(fails > 5) {
				if(players < MINIMUM_PLAYERS)
					new Restart("Too few players").execute();
				else if(timeSinceLastCombat() > 30000)
					new Restart("Time since last combat too high : " + timeSinceLastCombat()).execute();
			}
		} else {
			sleep = 15000;
			fails = 0;
		}
	}

	/**
	 * The run method of the thread.
	 */
	@Override
	public void run() {
		while(enabled) {
			if(Server.getUptime().minutesUptime() > 10) {
				checkServerUptime();
				if(Server.getUptime().millisUptime() > MAX_UPTIME) {
					World.update(30, "Uptime over MAX uptime.");
					return;
				}
			}
			try {
				sleep(sleep);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
