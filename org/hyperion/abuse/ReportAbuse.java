package org.hyperion.abuse;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ReportAbuse {// extends Thread {

    private final List<Abuse> cachedMessages = new LinkedList<Abuse>();

    private ReportAbuse() {

    }

    private void cacheMessage(final Player player, final String message) {
        cachedMessages.add(new Abuse(message, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
    }

    private boolean reportAbuse(final Player player, final String reported, final int rule) {
        try{
            String filename = new Date() + "";
            filename = filename.replace(":", "-");
            final BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter("./logs/abuse/" + filename + ".txt", true));
            writeToFile("", bufferedwriter);
            writeToFile("" + new Date(), bufferedwriter);
            writeToFile(reported, bufferedwriter);
            writeToFile(player.getName(), bufferedwriter);
            writeToFile(player.getLocation().getX() + ":" + player.getLocation().getY() + ":" + player.getLocation().getZ(), bufferedwriter);
            writeToFile("", bufferedwriter);
            writeToFile("", bufferedwriter);
            writeToFile("", bufferedwriter);
            for(final Abuse a : cachedMessages){
                final Location location = Location.create(a.xCoord(), a.yCoord(), a.zCoord());
                if(location == null){
                    System.out.println("Location is null");
                }
                if(player.getLocation() == null){
                    System.out.println("Player Location is null");
                }
                if(player.getLocation().isWithinDistance(location, 16) || a.getMessage().contains(reported)){
                    writeToFile(a.getMessage(), bufferedwriter);
                }
            }
            bufferedwriter.flush();
            bufferedwriter.close();
        }catch(final IOException ioexception){
            ioexception.printStackTrace();
            System.out.println("Critical error while writing log file!");
        }
        return true;
    }

    private void writeToFile(final String message, final BufferedWriter bufferedwriter) {
        try{
            bufferedwriter.write(message);
            bufferedwriter.newLine();
        }catch(final IOException ioexception){
            ioexception.printStackTrace();
            System.out.println("Critical error while writing log file!");
        }
    }

    private void clearOutList() {
        final long time = System.currentTimeMillis();
        for(final Iterator<Abuse> it$ = cachedMessages.iterator(); it$.hasNext(); )
            if((it$.next().getTime() + 180000) < time)
                it$.remove();
    }

    private void run() {
        try{
            while(true){
                Thread.sleep(1000);
                clearOutList();
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public static class Abuse {
        private final long timeStamp;
        private final String message;
        private final int xCoord;
        private final int yCoord;
        private final int zCoord;

        public Abuse(final String message, final int xCoord, final int yCoord, final int zCoord) {
            this.timeStamp = System.currentTimeMillis();
            this.message = message;
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.zCoord = zCoord;
        }

        public long getTime() {
            return timeStamp;
        }

        public String getMessage() {
            return message;
        }

        public int xCoord() {
            return xCoord;
        }

        public int yCoord() {
            return yCoord;
        }

        public int zCoord() {
            return zCoord;
        }
    }


}
