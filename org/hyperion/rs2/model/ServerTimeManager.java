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

    private static final ServerTimeManager manager = new ServerTimeManager();

    static {
        CommandHandler.submit(new Command("dumpservtimes", Rank.MODERATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                ServerTimeManager.getSingleton().dumpValues();
                player.getActionSender().sendMessage("Dumped all values.");
                return true;
            }
        });
    }

    private final HashMap<String, Long> totalMap = new HashMap<String, Long>();

    private final HashMap<String, Long> incrementsMap = new HashMap<String, Long>();

    public static ServerTimeManager getSingleton() {
        return manager;
    }

    public long getRunningTime() {
        return System.currentTimeMillis() - START;
    }

    public void add(final String name, final long increment) {
        //Increments updating
        if(incrementsMap.containsKey(name)){
            final long prev = incrementsMap.get(name);
            if(increment > prev)
                incrementsMap.put(name, increment);
        }else{
            incrementsMap.put(name, increment);
        }
        //Total updating
        if(totalMap.containsKey(name)){
            final long prev = totalMap.get(name);
            totalMap.put(name, increment + prev);
        }else{
            totalMap.put(name, increment);
        }
    }

    public void dumpValues() {
        try{
            final BufferedWriter out = new BufferedWriter(new FileWriter("./data/servtimemanager.log"));
            out.write("Total map!");
            out.newLine();
            for(final Map.Entry<String, Long> entry : totalMap.entrySet()){
                final String name = entry.getKey();
                final long time = entry.getValue();
                final long increment = incrementsMap.get(name);
                final double pct = (double) (time * 100) / (double) this.getRunningTime();
                if(pct > 0.1 || increment > 10){
                    out.write(name + " - " + pct + "% - max inc: " + increment + " ms.");
                    out.newLine();
                }
            }
            out.close();
        }catch(final IOException e){
            e.printStackTrace();
        }
    }

}
