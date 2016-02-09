package org.hyperion;

import org.hyperion.rs2.RS2Server;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.skill.dungoneering.RoomDefinition;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.net.security.CharFileEncryption;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.util.CharFilesCleaner;
import org.hyperion.rs2.util.RestarterThread;
import org.madturnip.tools.DumpNpcDrops;

import java.io.Console;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to start both the file and game servers.
 *
 * @authors Graham Edgecombe and Arsen Maxyutov
 */
public class Server {

    /**
     * The server configuration.
     */
    private static final Configuration config = new Configuration();

    /**
     * The old school flag.
     */
    public static final boolean OLD_SCHOOL = config.getBoolean("oldschool");

    /**
     * The server name.
     */
    public static final String NAME = config.getString("name");

    /**
     * The spawn server flag.
     */
    public static final boolean SPAWN = config.getBoolean("spawn");

    /**
     * The update version.
     */
    public static final double UPDATE = 6.72;


    public static final boolean DEBUG_CLEAN = false;
    /**
     * Server uptime instance
     */
    private static final Uptime uptime = new Uptime();

    /**
     * The server statistics.
     */
    private static final ServerStatistics stats = new ServerStatistics();

    /**
     * Logger instance
     */
    private static final Logger logger = Logger.getLogger(Server.class.getName());


    /**
     * The Encryption instance, holding the used key on startup.
     */
    private static CharFileEncryption charFileEncryption;

    /**
     * Server uptime.
     *
     * @return the server uptime
     */
    public static Uptime getUptime() {
        return uptime;
    }

    public static Configuration getConfig() {
        return config;
    }

    public static ServerStatistics getStats() {
        return stats;
    }

    public static CharFileEncryption getCharFileEncryption() {
        return charFileEncryption;
    }


    public static final boolean DEBUG = false;

    public static final boolean inDebug() {
        return DEBUG;
    }

    public static final java.io.OutputStream OUTPUT = System.out;
    public static final java.io.OutputStream ERR = System.err;

    /**
     * Last server vote claim
     */
    public static long lastServerVote = 0L;

    private static final String checkString = "ZL0Rw+jTUzQ7OBep3Z/Cgg\u003d\u003d";

    /**
     * The entry point of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) throws Exception {
        launchServer();
    }

    public static World launchServer() {

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
        File[] files = new File("./data/characters/mergedchars").listFiles();
        System.out.println("Started converting char files, count: " + files.length);

        final long currentTime = System.currentTimeMillis();
        RestartTask.submitRestartTask();
        long start = System.currentTimeMillis();
        new Thread(new CharFilesCleaner()).start();
        System.out.println("-- Starting " + NAME + "  -- " + UPDATE);
        System.out.println("Spawn server: " + SPAWN);
        World.getWorld(); // this starts off background loading
        try {
            //new FileServer().bind().start();

            new RS2Server().start();

            DumpNpcDrops.startDump2();
            PossibleHacksHolder.init();
            RoomDefinition.load();
            ClanManager.load();
//			ItemInfo.init();
            System.out.println("Fully loaded server in : " + (System.currentTimeMillis() - start) + " ms.");
/*
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
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, "Error starting Hyperion.", ex);
            System.exit(1);
        }
        RestarterThread.getRestarter();
        //SQL.getSQL();
        //ShopManager.dumpShops();
        return World.getWorld();
    }

}
