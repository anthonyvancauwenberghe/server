package org.hyperion.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperion.Configuration;
import org.hyperion.rs2.savingnew.IOData;

import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hyperion.Configuration.ConfigurationObject.CHARACTER_FILE_CLEANUP;
import static org.hyperion.Configuration.ConfigurationObject.CHARACTER_FILE_CLEANUP_THREADS;

/**
 * Created by Gilles on 18/02/2016.
 */
public class CleanCharacterFiles implements Runnable {

    /**
     * The character files that still have to be processed.
     */
    private static Queue<File> characterFiles;

    /**
     * This can be turned off by a command just to make sure we can't break things.
     */
    private static boolean enabled = true;

    /**
     * The logger class
     */
    private final static Logger logger = Logger.getLogger("CharacterFileCleaner");

    /**
     * This method can be called at any time and will go through all the character files. It will use the default set directory.
     */
    public static void startup() {
        if (!Configuration.getBoolean(CHARACTER_FILE_CLEANUP) && enabled)
            return;

        File[] characterFilesFolder = new File(IOData.getCharFilePath()).listFiles();
        if (characterFilesFolder == null) {
            System.out.println("Could not find any character files in the " + IOData.getCharFilePath() + " folder.");
            return;
        }

        characterFiles = new LinkedList<>(Arrays.asList(characterFilesFolder));

        /**
         * This will start the threads, with the pre-configured settings.
         */
        ExecutorService application = Executors.newFixedThreadPool(Configuration.getInt(CHARACTER_FILE_CLEANUP_THREADS));
        for (int i = 0; i < Configuration.getInt(CHARACTER_FILE_CLEANUP_THREADS); i++)
            application.submit(new CleanCharacterFiles(i + 1));
    }

    private final int threadId;

    private CleanCharacterFiles(int threadId) {
        this.threadId = threadId;
    }

    @Override
    public void run() {
        while (characterFiles != null && !characterFiles.isEmpty() && enabled) {
            File characterFile = characterFiles.poll();
            if (characterFile == null)
                break;
            boolean willGetCleaned = true;
            try (FileReader fileReader = new FileReader(characterFile)) {
                JsonParser fileParser = new JsonParser();
                Gson builder = new Gson();
                JsonObject reader = (JsonObject) fileParser.parse(fileReader);

                /**
                 * If a player doesn't have a single rank we'll make him cleaned
                 */
                if (!reader.has(IOData.PREVIOUS_LOGIN.toString())) {
                    if(!LocalDateTime.ofInstant(Instant.ofEpochMilli(reader.get(IOData.PREVIOUS_LOGIN.toString()).getAsLong()), ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now())) {
                        if (!reader.has(IOData.RANK.toString()))
                            willGetCleaned = true;

                        if (reader.has(IOData.TUTORIAL_PROGRESS.toString())) {
                            if (reader.get(IOData.TUTORIAL_PROGRESS.toString()).getAsInt() != 28) {
                                willGetCleaned = true;
                            }
                        }

                        if(reader.has(IOData.LEVELS.toString())) {
                            builder.fromJson(reader.get(IOData.LEVELS.toString()).getAsJsonArray(), int[].class);
                        }
                    }
                } else {
                    willGetCleaned = true;
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "An error occurred while trying to read a character file.", e);
            }
        }
    }
}
