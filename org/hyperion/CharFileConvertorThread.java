package org.hyperion;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.saving.PlayerSaving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Gilles on 18/02/2016.
 */
public class CharFileConvertorThread extends Thread {
    private final static Queue<File> CHARFILES = new LinkedList<>(Arrays.asList(new File("./data/characters/mergedchars").listFiles()));
    private final int threadNumber;

    public CharFileConvertorThread(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            File charFile;
            synchronized (CHARFILES) {
                if (CHARFILES.isEmpty()) {
                    running = false;
                    continue;
                }
                charFile = CHARFILES.remove();
            }
            if (charFile == null || !charFile.exists())
                continue;
            String ip = null;
            int uid = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(charFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("Mac=")) {
                        uid = Integer.parseInt(line.replaceAll("Mac=", "").trim());
                        continue;
                    }
                    if (line.startsWith("IP=")) {
                        ip = line.replaceAll("IP=", "").trim();
                        break;
                    }
                    if (line.startsWith("Skills"))
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
                Player player = new Player(uid);
                player.setIP(ip);
                player.setName(charFile.getName().replace(".txt", ""));
                player.destroy();
                new PlayerSaving().load(player, MergedSaving.MERGED_DIR);
                if(org.hyperion.rs2.savingnew.PlayerSaving.save(player))
                    if (!charFile.delete()) {
                        charFile.deleteOnExit();
                    }
                System.out.println(CHARFILES.size() + " characters to go.");
        }
        System.out.println("Thread " + threadNumber + ": Done converting; shutting down.");
    }
}
