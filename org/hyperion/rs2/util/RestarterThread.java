package org.hyperion.rs2.util;

import org.hyperion.Server;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

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
    private static final boolean enabled = Server.getConfig().getBoolean("restarterthread");
    /**
     * The restarter thread.
     */
    private static final RestarterThread rt = new RestarterThread();
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
     * Constructor
     */
    public RestarterThread() {
        this.setDaemon(true);
        this.setName("RestarterThread");
        this.start();
    }

    /**
     * Use this to access the restarter.
     *
     * @return
     */
    public static RestarterThread getRestarter() {
        return rt;
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
        final int players = World.getWorld().getPlayers().size();
        counter++;
        if(counter >= 20){
            counter = 0;
            PlayercountLogger.getLogger().log(players);
        }
        if(players < MINIMUM_PLAYERS || timeSinceLastCombat() > 15000){
            fails++;
            sleep = 5000;
            if(fails > 5){
                if(players < MINIMUM_PLAYERS)
                    new Restart("Too few players").execute();
                else if(timeSinceLastCombat() > 30000)
                    new Restart("Time since last combat too high : " + timeSinceLastCombat()).execute();
            }
        }else{
            sleep = 15000;
            fails = 0;
        }
    }

    private void checkSQLThread() {
        if(!World.getWorld().getDonationsConnection().isRunning())
            return;
        if(true)
            return;
        //System.out.println("Time since last SQL: " + timeSinceLastSQL());
        if(timeSinceLastSQL() > 3 * 60 * 1000){
            /*SQL.getSQL().stopRunning();
            SQL.resetSQLObject();
			SQL.getSQL().start();*/
            updateSQLTimer();
            try{
                final BufferedWriter bw = new BufferedWriter(new FileWriter("./data/sqlrestartlog.log", true));
                bw.write("Restarted SQL Thread at : " + new Date().toString());
                bw.newLine();
                bw.close();
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * The run method of the thread.
     */
    @Override
    public void run() {
        while(enabled){
            if(Server.getUptime().minutesUptime() > 10){
                checkServerUptime();
                checkSQLThread();
                if(Server.getUptime().millisUptime() > MAX_UPTIME){
                    World.getWorld().update(30, "Uptime over MAX uptime.");
                    return;
                }
            }
            try{
                sleep(sleep);
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
    }

}
