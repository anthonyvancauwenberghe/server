package org.hyperion.rs2.sql;

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
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

/**
 * @author Arsen Max.
 */
public class SQLite {

	public static final String DB_FILE_NAME = "./data/database.db";

	private static SQLite singleton = new SQLite(DB_FILE_NAME);

	private String fileName;

	private Connection connection = null;

	private Statement statement = null;

	private long lastConnectionAttempt = 0;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public static SQLite getDatabase() {
		return singleton;
	}

	public SQLite(String fileName) {
		this.fileName = fileName;
		LoginDebugger.getDebugger().log("About to create conn SQLite");
		createConnection();
		LoginDebugger.getDebugger().log("About to init SQLite");
		init();
		LoginDebugger.getDebugger().log("Created SQLite");
	}

	public boolean createConnection() {
		if(System.currentTimeMillis() - lastConnectionAttempt < 5000)
			return false;
		try {
			lastConnectionAttempt = System.currentTimeMillis();
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
			statement = connection.createStatement();
			statement.setQueryTimeout(1000);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void init() {
		try {
			query("DELETE FROM playerips WHERE time < " + (System.currentTimeMillis() - Time.ONE_WEEK * 4));
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			statement.close();
			connection.close();
		} catch(SQLException e) {
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
	public ResultSet query(String query) throws SQLException {
		// System.out.println(s);
		try {
			if(query.toLowerCase().startsWith("select")) {
				ResultSet rs = statement.executeQuery(query);
				return rs;
			} else {
				statement.executeUpdate(query);
			}
		} catch(Exception e) {
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
				try {
					synchronized(this) {
						query(query);
					}
				} catch(SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void submitTask(Runnable runnable) {
		executor.submit(runnable);
	}

    public Connection getConnection(){
        return connection;
    }

	public static void main(String... args) throws Exception {
		SQLite db = SQLite.getDatabase();
		//db.query("CREATE TABLE playerips (name CHAR(12) NOT NULL, ip CHAR(16) NOT NULL);");
		int counter = 0;
		ResultSet rs = db.query("SELECT * FROM playerips WHERE 1");
		while(rs.next()) {
			System.out.println("Name: " + rs.getString("name") + ", Ip: " + rs.getString("ip"));
			counter++;
		}
		System.out.println("Rows: " + counter);
		db.close();
	}

	static {
		LoginDebugger.getDebugger().log("Static sqlite");
		CommandHandler.submit(new Command("alts", Rank.HELPER) {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public <K, V> LinkedHashMap<K, V> sortHashMapByValues(HashMap<K, V> passedMap, boolean ascending) {
			 	 List mapKeys = new ArrayList(passedMap.keySet());
			 	 List mapValues = new ArrayList(passedMap.values());
			 	 Collections.sort(mapValues);	
			 	 Collections.sort(mapKeys);	

			 	 if (!ascending)
			 		 Collections.reverse(mapValues);

			 	 LinkedHashMap someMap = new LinkedHashMap();
			 	 Iterator valueIt = mapValues.iterator();
				while (valueIt.hasNext()) {
					Object val = valueIt.next();
					Iterator keyIt = mapKeys.iterator();
					while (keyIt.hasNext()) {
						Object key = keyIt.next();
						if (passedMap.get(key).toString().equals(val.toString())) {
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
			public boolean execute(Player player, String input) throws Exception {

                String[] anonymousUsers = new String[]{"Hotshot gg", "Ferry", "Arre","Jet","Urapucywhale","Eagly eye","Wh1p"};

				String name = filterInput(input);
                for(String anonymous : anonymousUsers) {
                    if(name.equalsIgnoreCase(anonymous)) {
                        String offline = World.getWorld().getPlayer(anonymous) != null ? "[@gre@Online@bla@]" : "[@red@Offline@bla@]";
                        player.getActionSender().sendMessage(offline+"[@red@Name@bla@]:"+anonymous+" [@red@Last Login@bla@]: " + new Date().toString());
                        return true;
                    }
                }

                synchronized(SQLite.getDatabase()) {
					//Get all ips for name
					ResultSet rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE name = '" + name + "'");
					LinkedList<String> ips = new LinkedList<String>();
					while(rs.next()) {
						ips.add(rs.getString("ip"));
					}

					//Find all names for ip
					HashMap<String, Long> names = new HashMap<String, Long>();
					for(String ip : ips) {
						rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE ip = '" + ip + "'");
						while(rs.next()) {
							String alt = rs.getString("name");
							Long time = rs.getLong("time");
							names.put(alt, time);
						}
					}
					names.remove(name.toLowerCase());
					LinkedHashMap<String, Long> map = sortHashMapByValues(names, true);
                    int size = map.keySet().size();
                    final String[] alts = map.keySet().toArray(new String[size]);
					int idx = 1;
					for(int i = Math.max(size - 20, 0); i < size - 1; i++) {
                        String alt = alts[i];
						if(map.get(alt) == null)
							continue;
                        boolean skip = false;
                        for(String anonymous : anonymousUsers) {
                            if(alt.equalsIgnoreCase(anonymous))
                                skip = true;
                        }
                        if(skip)
                            continue;
						Date date = new Date(map.get(alt));
						String offline = World.getWorld().getPlayer(alt) != null ? "[@gre@Online@bla@]" : "[@red@Offline@bla@]";
                        if(alt.length() >= 20)
                            alt = alt.substring(0, 20);
						player.getActionSender().sendMessage(offline+"[@red@Name@bla@]:"+alt+" [@red@Last Login@bla@]: " + date.toString());
					}
					return true;
				}
			}
		});
		CommandHandler.submit(new Command("wipealts", Rank.OWNER) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				String name = filterInput(input);
				if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
					return false;
				synchronized(SQLite.getDatabase()) {
					//Get all ips for name
					ResultSet rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE name = '" + name + "'");
					LinkedList<String> ips = new LinkedList<String>();
					while(rs.next()) {
						ips.add(rs.getString("ip"));
					}

					//Find all names for ip
					HashMap<String, Long> names = new HashMap<String, Long>();
					for(String ip : ips) {
						rs = SQLite.getDatabase().query("SELECT * FROM playerips WHERE ip = '" + ip + "'");
						while(rs.next()) {
							String alt = rs.getString("name");
							Long time = rs.getLong("time");
							names.put(alt, time);
						}
					}
					names.remove(name.toLowerCase());
					int idx = 1;
					java.util.List<String> namesToDelete = new LinkedList<String>();
					for(String alt : names.keySet()) {
						Date date = new Date(names.get(alt));
						player.getActionSender().sendMessage("Alt " + idx++ + ": " + alt + ", time: " + date.toString());
						namesToDelete.add(alt);
					}
					for(String s : namesToDelete) {
						File file = new File("./data/characters/mergedchars/" + s + ".txt");
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
	

	


	public static class IpUpdateTask implements Runnable {

		private String name;

		private String ip;

		public IpUpdateTask(String name, String ip) {
			this.name = name.toLowerCase();
			this.ip = TextUtils.shortIp(ip);
		}

		@Override
		public void run() {
			try {
				synchronized(SQLite.getDatabase()) {
					//Get account from database
					ResultSet rs = SQLite.getDatabase().query(
							"SELECT * FROM playerips WHERE name = '" + name
									+ "' AND ip = '" + ip + "'");
					//If account doesn't exist, add into database
					if(rs == null || ! rs.next()) {
						SQLite.getDatabase().query(
								"INSERT INTO playerips (name, ip,time) VALUES ('"
										+ name + "','" + ip + "'," + System.currentTimeMillis() + ")");
					} else {
						//Update time of the account
						StringBuilder sb = new StringBuilder();
						sb.append("UPDATE playerips SET time = ");
						sb.append(System.currentTimeMillis());
						sb.append(" WHERE name = '");
						sb.append(name);
						sb.append("' AND ip = '");
						sb.append(ip);
						sb.append("'");
						//System.out.println(sb.toString());
						SQLite.getDatabase().query(sb.toString());
					}
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}

		}

	}
}
