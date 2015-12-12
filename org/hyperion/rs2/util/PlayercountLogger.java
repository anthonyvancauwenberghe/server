package org.hyperion.rs2.util;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class PlayercountLogger {

    /**
     * Holds the first time in ms that playercount logging has started.
     */
    private static final long BEGIN_TIME = 1321649384735L; // 19 November

    private static final PlayercountLogger singleton = new PlayercountLogger();

    private PlayercountLogger() {

    }

    public static PlayercountLogger getLogger() {
        return singleton;
    }

    /**
     * Returns time in minutes from <code>BEGIN_TIME</code> up to now.
     *
     * @return
     */
    private int getTime() {
        final long mstime = System.currentTimeMillis() - BEGIN_TIME;
        final long stime = mstime / 1000;
        final long minutetime = stime / 60;
        return (int) minutetime;
    }

    /**
     * Writes the playercounter log.
     *
     * @param players
     */
    public void log(final int players) {
        try{
            final BufferedWriter bw = new BufferedWriter(new FileWriter("./data/playercountlog.txt", true));
            bw.write(players + "," + getTime());
            bw.newLine();
            bw.close();
        }catch(final Exception e){
            e.printStackTrace();
        }

    }
}
