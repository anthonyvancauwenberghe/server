package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Trade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class Restart {

    public static final File LOG_FILE = new File("./data/restarts.log");

    public Restart(final String reason) {
        writeLog(reason);
    }

    public void execute() {
        for(final Player player : World.getWorld().getPlayers()){
            Trade.declineTrade(player);
            PlayerFiles.saveGame(player);
        }
        System.exit(0);
    }

    private void writeLog(final String reason) {
        for(int i = 0; i < 10; i++){
            System.out.println("Restarting server: " + reason);
        }
        try{
            final BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true));
            bw.write(new Date() + "\t" + reason);
            bw.newLine();
            bw.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

}
