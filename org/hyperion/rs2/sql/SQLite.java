package org.hyperion.rs2.sql;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Arsen Max.
 */
public class SQLite {

    public static final String DB_FILE_NAME = "./data/database.db";

    private static final SQLite singleton = new SQLite(DB_FILE_NAME);

    static {
        LoginDebugger.getDebugger().log("Static sqlite");
        CommandHandler.submit(new Command("alts", Rank.HELPER) {

            @SuppressWarnings({"rawtypes", "unchecked"})
            public <K, V> LinkedHashMap<K, V> sortHashMapByValues(final HashMap<K, V> passedMap, final boolean ascending) {
                final List mapKeys = new ArrayList(passedMap.keySet());
                final List mapValues = new ArrayList(passedMap.values());
                Collections.sort(mapValues);
                Collections.sort(mapKeys);

                if(!ascending)
                    Collections.reverse(mapValues);

                final LinkedHashMap someMap = new LinkedHashMap();
                final Iterator valueIt = mapValues.iterator();
                while(valueIt.hasNext()){
                    final Object val = valueIt.next();
                    final Iterator keyIt = mapKeys.iterator();
                    while(keyIt.hasNext()){
                        final Object key = keyIt.next();
                        if(passedMap.get(key).toString().equals(val.toString())){
                            passedMap.remove(key);
                            mapKeys.remove(key);
                            someMap.put(key, val);
                            break;
                        }
                    }
                }
                return someMap;
            }

            @Override
            public boolean execute(final Player player, final String input) throws Exception {

                final String[] anonymousUsers = new String[]{"Hotshot gg", "Ferry", "Arre", "Jet", "Urapucywhale",
                        "Eagly eye", "Wh1p"};

                final String name = filterInput(input);
                for(final String anonymous : anonymousUsers){
                    if(name.equalsIgnoreCase(anonymous)){
                        final String offline = World.getWorld().getPlayer(anonymous) != null ? "[@gre@Online@bla@]" : "[@red@Offline@bla@]";
                        player.getActionSender().sendMessage(offline + "[@red@Name@bla@]:" + anonymous + " [@red@Last Login@bla@]: " + new Date().toString());
                        return true;
                    }
                }

                synchronized(SQLite.getDatabase()){
                    //Get all ips for name
                    ResultSet rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE name = '" + name + "'");
                    final LinkedList<String> ips = new LinkedList<String>();
                    while(rs.next()){
                        ips.add(rs.getString("ip"));
                    }

                    //Find all names for ip
                    final HashMap<String, Long> names = new HashMap<String, Long>();
                    for(final String ip : ips){
                        rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE ip = '" + ip + "'");
                        while(rs.next()){
                            final String alt = rs.getString("name");
                            final Long time = rs.getLong("time");
                            names.put(alt, time);
                        }
                    }
                    names.remove(name.toLowerCase());
                    final LinkedHashMap<String, Long> map = sortHashMapByValues(names, true);
                    final int size = map.keySet().size();
                    final String[] alts = map.keySet().toArray(new String[size]);
                    final int idx = 1;
                    for(int i = Math.max(size - 20, 0); i < size - 1; i++){
                        String alt = alts[i];
                        if(map.get(alt) == null)
                            continue;
                        boolean skip = false;
                        for(final String anonymous : anonymousUsers){
                            if(alt.equalsIgnoreCase(anonymous))
                                skip = true;
                        }
                        if(skip)
                            continue;
                        final Date date = new Date(map.get(alt));
                        final String offline = World.getWorld().getPlayer(alt) != null ? "[@gre@Online@bla@]" : "[@red@Offline@bla@]";
                        if(alt.length() >= 20)
                            alt = alt.substring(0, 20);
                        player.getActionSender().sendMessage(offline + "[@red@Name@bla@]:" + alt + " [@red@Last Login@bla@]: " + date.toString());
                    }
                    return true;
                }
            }
        });
        CommandHandler.submit(new Command("wipealts", Rank.OWNER) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                final String name = filterInput(input);
                if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
                    return false;
                synchronized(SQLite.getDatabase()){
                    //Get all ips for name
                    ResultSet rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE name = '" + name + "'");
                    final LinkedList<String> ips = new LinkedList<String>();
                    while(rs.next()){
                        ips.add(rs.getString("ip"));
                    }

                    //Find all names for ip
                    final HashMap<String, Long> names = new HashMap<String, Long>();
                    for(final String ip : ips){
                        rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE ip = '" + ip + "'");
                        while(rs.next()){
                            final String alt = rs.getString("name");
                            final Long time = rs.getLong("time");
                            names.put(alt, time);
                        }
                    }
                    names.remove(name.toLowerCase());
                    int idx = 1;
                    final java.util.List<String> namesToDelete = new LinkedList<String>();
                    for(final String alt : names.keySet()){
                        final Date date = new Date(names.get(alt));
                        player.getActionSender().sendMessage("Alt " + idx++ + ": " + alt + ", time: " + date.toString());
                        namesToDelete.add(alt);
                    }
                    for(final String s : namesToDelete){
                        final File file = new File("./data/characters/mergedchars/" + s + ".txt");
                        player.getActionSender().sendMessage("You are attemptng to move " + file.toString());
                        if(file.renameTo(new File("./data/characters/mergedchars/wiped/" + file.getName())))
                            player.getActionSender().sendMessage("You have just moved " + file.toString());
                        else
                            player.getActionSender().sendMessage("Failed to move file " + file.toString());
                    }
                    return true;
                }
            }
        });
    }

    private final String fileName;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Connection connection = null;
    private Statement statement = null;
    private long lastConnectionAttempt = 0;

    public SQLite(final String fileName) {
        this.fileName = fileName;
        LoginDebugger.getDebugger().log("About to create conn SQLite");
        createConnection();
        LoginDebugger.getDebugger().log("About to init SQLite");
        init();
        LoginDebugger.getDebugger().log("Created SQLite");
    }

    public static SQLite getDatabase() {
        return singleton;
    }

    public static void main(final String... args) throws Exception {
        final SQLite db = SQLite.getDatabase();
        //db.query("CREATE TABLE playerips (name CHAR(12) NOT NULL, ip CHAR(16) NOT NULL);");
        int counter = 0;
        final ResultSet rs = db.query("SELECT * FROM playerips WHERE 1");
        while(rs.next()){
            System.out.println("Name: " + rs.getString("name") + ", Ip: " + rs.getString("ip"));
            counter++;
        }
        System.out.println("Rows: " + counter);
        db.close();
    }

    public boolean createConnection() {
        if(System.currentTimeMillis() - lastConnectionAttempt < 5000)
            return false;
        try{
            lastConnectionAttempt = System.currentTimeMillis();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            statement = connection.createStatement();
            statement.setQueryTimeout(1000);
            return true;
        }catch(final Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void init() {
        try{
            query("DELETE FROM playerips WHERE time < " + (System.currentTimeMillis() - Time.ONE_WEEK * 4));
        }catch(final SQLException e){
            e.printStackTrace();
        }
    }

    public void close() {
        try{
            statement.close();
            connection.close();
        }catch(final SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Executes a query, it is suggested to use the <code>submitQuery</code>
     * method if you are using update queries because they are executed
     * parallel.
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet query(final String query) throws SQLException {
        // System.out.println(s);
        try{
            if(query.toLowerCase().startsWith("select")){
                final ResultSet rs = statement.executeQuery(query);
                return rs;
            }else{
                statement.executeUpdate(query);
            }
        }catch(final Exception e){
            e.printStackTrace();
            createConnection();
        }
        return null;
    }

    /**
     * Submits a query to the seperate SQLite thread which will execute in the
     * near future.
     *
     * @param query
     */
    public void submitQuery(final String query) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    synchronized(this){
                        query(query);
                    }
                }catch(final SQLException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void submitTask(final Runnable runnable) {
        executor.submit(runnable);
    }

    public Connection getConnection() {
        return connection;
    }

    public static class IpUpdateTask implements Runnable {

        private final String name;

        private final String ip;

        public IpUpdateTask(final String name, final String ip) {
            this.name = name.toLowerCase();
            this.ip = TextUtils.shortIp(ip);
        }

        @Override
        public void run() {
            try{
                synchronized(SQLite.getDatabase()){
                    //Get account from database
                    final ResultSet rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE name = '" + name + "' AND ip = '" + ip + "'");
                    //If account doesn't exist, add into database
                    if(rs == null || !rs.next()){
                        SQLite.getDatabase().query("INSERT INTO playerips (name, ip,time) VALUES ('" + name + "','" + ip + "'," + System.currentTimeMillis() + ")");
                    }else{
                        //Update time of the account
                        //System.out.println(sb.toString());
                        SQLite.getDatabase().query("UPDATE playerips SET time = " + System.currentTimeMillis() + " WHERE name = '" + name + "' AND ip = '" + ip + "'");
                    }
                }
            }catch(final SQLException e){
                e.printStackTrace();
            }

        }

    }
}
