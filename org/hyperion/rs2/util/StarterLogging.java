package org.hyperion.rs2.util;

import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.util.Time;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class StarterLogging {

    /**
     * The delay after which a player can receive a starter again.
     */
    public static final long STARTER_MAX_DELAY = Time.ONE_DAY;

    public static final long STARTER_MIN_DELAY = Time.ONE_HOUR * 5;

    public static final File SAVE_FILE = new File("./logs/starters.log");
    private static final StarterLogging singleton = new StarterLogging();

    static {
        if(SAVE_FILE.exists())
            loadData();
    }

    private final Map<String, Long> starters = new HashMap<String, Long>();

    public static StarterLogging getLogging() {
        return singleton;
    }

    public static void loadData() {
        try{
            final BufferedReader in = new BufferedReader(new FileReader(SAVE_FILE));
            String line;
            final long currentTime = System.currentTimeMillis();
            while((line = in.readLine()) != null){
                final String[] parts = line.split(":");
                final String ip = parts[0];
                final long time = Long.parseLong(parts[1]);
                if(currentTime - time < STARTER_MAX_DELAY)
                    getLogging().starters.put(ip, time);
            }
            in.close();
            final BufferedWriter out = new BufferedWriter(new FileWriter(SAVE_FILE));
            for(final Map.Entry<String, Long> entry : getLogging().starters.entrySet()){
                out.write(entry.getKey() + ":" + entry.getValue());
                out.newLine();
            }
            out.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public void save(final String ip, final long time) {
        starters.put(ip, time);
        PlayerSaving.getSaving().saveLog(SAVE_FILE, ip + ":" + time);
    }

    public long lastStarterReceived(final String ip) {
        final Long time = starters.get(ip);
        if(time == null)
            return 0;
        return time;
    }

}
