package org.hyperion.rs2.logging;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Gilles on 6/02/2016.
 */
public class FileLogging {
    private final static String DEFAULT_LOGGING_PATH = ".logs/";
    private final static DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss | ");

    public static void savePlayerLog(final Player player, final String... lines) {
        World.getEngine().pushTask(context -> writeToFile("characters/" + player.getName().toLowerCase(), lines));
    }

    public static void saveGameLog(final String filePath, final String... lines) {
        World.getEngine().pushTask(context -> writeToFile(filePath, lines));
    }

    private static void writeToFile(String filePath, String... lines) {
        File file = new File(DEFAULT_LOGGING_PATH, filePath);

        if (!file.getParentFile().exists()) {
            try {
                if(!file.getParentFile().mkdirs())
                    return;
            } catch (SecurityException e) {
                System.out.println("Unable to create directory for list file!");
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            for(String line : lines)
                bw.append(FILE_DATE_FORMAT.format(System.currentTimeMillis())).append(line).append(System.lineSeparator());
            bw.flush();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
