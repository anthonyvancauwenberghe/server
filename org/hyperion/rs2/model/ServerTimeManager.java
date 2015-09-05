package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SaosinHax on 05.09.2015.
 */
public class ServerTimeManager {

    private static final long START = System.currentTimeMillis();

    private static ServerTimeManager manager = new ServerTimeManager();

    public static ServerTimeManager getSingleton() {
        return manager;
    }

    private HashMap<String, Long> totalMap = new HashMap<String, Long>();

    private HashMap<String, Long> incrementsMap = new HashMap<String, Long>();

    public long getRunningTime() {
        return System.currentTimeMillis() - START;
    }

    public void add(String name, long increment) {
        //Increments updating
        if(incrementsMap.containsKey(name)) {
            long prev = incrementsMap.get(name);
            if(increment > prev)
                incrementsMap.put(name, increment);
        } else {
            incrementsMap.put(name, increment);
        }
        //Total updating
        if(totalMap.containsKey(name)) {
            long prev = totalMap.get(name);
            totalMap.put(name, increment + prev);
        } else {
            totalMap.put(name, increment);
        }
    }

    public void dumpValues() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("./data/servtimemanager.log"));
            out.write("Total map!"); out.newLine();
            for(Map.Entry<String,Long> entry: totalMap.entrySet()) {
                String name = entry.getKey();
                long time = entry.getValue();
                long increment = incrementsMap.get(name);
                double pct = (double) (time * 100) / (double) this.getRunningTime();
                out.write(name + " - " + pct + "% - max inc: " + increment + " ms.");
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        CommandHandler.submit(new Command("dumpservtimes", Rank.MODERATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                ServerTimeManager.getSingleton().dumpValues();
                player.getActionSender().sendMessage("Dumped all values.");
                return true;
            }
        });
    }

}
