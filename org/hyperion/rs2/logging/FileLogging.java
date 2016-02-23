package org.hyperion.rs2.logging;

import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.rs2.model.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gilles on 6/02/2016.
 */
public class FileLogging {
    private final static String DEFAULT_LOGGING_PATH = ".logs/";
    private final static DateFormat FILE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss | ");

    public static void savePlayerLog(final Player player, final String... lines) {
        writeToFile("characters/" + player.getName().toLowerCase(), lines);
    }

    public static void saveGameLog(final String filePath, final String... lines) {
        writeToFile(filePath, lines);
    }

    public static void writeError(String filename, Exception ex) {
        Server.getLoader().getEngine().submitLogic(new EngineTask("Write error to file", 2, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                StringBuilder sb = new StringBuilder();
                if (ex.getCause() != null)
                    sb.append("	cause: ").append(ex.getCause().toString()).append("\n");
                if (ex.getClass() != null)
                    sb.append("	class: ").append(ex.getClass().toString()).append("\n");
                if (ex.getMessage() != null)
                    sb.append("	message: ").append(ex.getMessage()).append("\n");
                if (ex.getStackTrace() == null)
                    ex.fillInStackTrace();
                if (ex.getStackTrace() != null) {
                    for (StackTraceElement s : ex.getStackTrace()) {
                        sb.append("	at ")
                                .append(s.getClassName())
                                .append(".")
                                .append(s.getMethodName())
                                .append("(")
                                .append(s.getFileName())
                                .append(":")
                                .append(s.getLineNumber())
                                .append(")")
                                .append("\n");
                    }
                }
                sb.append("================================");
                writeToFile(filename, sb.toString().split("\n"));
                return true;
            }
        });
    }

    private static void writeToFile(String filePath, String... lines) {
        Server.getLoader().getEngine().submitLogic(new EngineTask("Write log to file", 1, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                File file = new File(DEFAULT_LOGGING_PATH, filePath);

                if (!file.getParentFile().exists()) {
                    try {
                        if (!file.getParentFile().mkdirs())
                            return true;
                    } catch (SecurityException e) {
                        System.out.println("Unable to create directory for list file!");
                    }
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                    for (String line : lines)
                        bw.append(FILE_DATE_FORMAT.format(System.currentTimeMillis())).append(line).append(System.lineSeparator());
                    bw.flush();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return true;
            }
        });
    }
}
