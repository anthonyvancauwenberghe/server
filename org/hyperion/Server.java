package org.hyperion;

import org.hyperion.engine.Update;
import org.hyperion.rs2.net.security.CharFileEncryption;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.util.ShutdownHook;

import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hyperion.Configuration.ConfigurationObject.NAME;
import static org.hyperion.Configuration.ConfigurationObject.PORT;

public class Server {

    private final static Logger logger = Logger.getLogger(Configuration.getString(NAME));
    private final static GameLoader loader = new GameLoader(Configuration.getInt(PORT));
    private final static Uptime uptime = new Uptime();
    private final static ServerStatistics stats = new ServerStatistics();
    private static CharFileEncryption charFileEncryption;
    private static boolean updating = false;

    private static final String checkString = "ZL0Rw+jTUzQ7OBep3Z/Cgg\u003d\u003d";

    public static Uptime getUptime() {
        return uptime;
    }

    public static ServerStatistics getStats() {
        return stats;
    }

    public static CharFileEncryption getCharFileEncryption() {
        return charFileEncryption;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static GameLoader getLoader() {
        return loader;
    }

    public static boolean isUpdating() {
        return updating;
    }

    public static void setUpdating(boolean updating) {
        Server.updating = updating;
    }

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        if (console == null) {
            logger.log(Level.WARNING, "Using default password.");
            charFileEncryption = new CharFileEncryption("Glis1234Glis1234");
        } else {
            boolean correctPass = false;
            while(!correctPass) {
                char passwordArray[] = console.readPassword("Enter password: ");
                charFileEncryption = new CharFileEncryption(String.valueOf(passwordArray));
                if(EncryptionStandard.encrypt("randomstring", charFileEncryption.getKey()).equals(checkString)) {
                    correctPass = true;
                } else {
                    System.out.println("Password incorrect.");
                }
            }
        }
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        try {
            logger.info("Started loading the server...");
            loader.init();
            loader.finish();
            logger.info(Configuration.getString(NAME) + " is now online on port " + Configuration.getInt(PORT) + ".");

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Could not start " + Configuration.getString(NAME) + "!", ex);
            System.exit(1);
        }
    }

    public static void update(int time, final String reason) {
        setUpdating(true);
        getLoader().getEngine().submitWork(new Update(time, reason));
    }
/*
        File[] files = new File("./data/characters/mergedchars").listFiles();
        System.out.println("Started converting char files, count: " + files.length);

        final long currentTime = System.currentTimeMillis();
        long start = System.currentTimeMillis();
        new Thread(new CharFilesCleaner()).start();
        System.out.println("-- Starting " + NAME + "  -- ");
        System.out.println("Spawn server: " + Configuration.getString(NAME));
        try {

            for(File file : files) {
                String ip = null;
                int uid = 0;
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while((line = br.readLine()) != null) {
                        if(line.startsWith("Mac=")) {
                            uid = Integer.parseInt(line.replaceAll("Mac=", "").trim());
                            continue;
                        }
                        if(line.startsWith("IP=")) {
                            ip = line.replaceAll("IP=", "").trim();
                            break;
                        }
                        if(line.startsWith("Skills"))
                            break;
                    }
                    br.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Player player = new Player(uid);
                player.setIP(ip);
                player.setName(file.getName().replaceAll(".txt", ""));
                new PlayerSaving().load(player, MergedSaving.MERGED_DIR);
                org.hyperion.rs2.savingnew.PlayerSaving.save(player);
            }*/
}
