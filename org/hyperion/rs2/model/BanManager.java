package org.hyperion.rs2.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.BanCommand;
import org.hyperion.rs2.commands.impl.UnbanCommand;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.util.Time;

/**
 * @author Arsen Maxyutov.
 */
public class BanManager {

	/**
	 * The maximum amount of saved bans that will load on restart.
	 */
	public static final int MAX_SAVED_BANS = 80;

	/**
	 * The type of ban.
	 */
	public static final int NO_BAN = 0, MUTE = 1, BAN = 2, YELL = 3, BLACK_MARK = 4;

	/**
	 * A list of unbannable usernames.
	 */
	public static final String[] UNBANNABLE_NAMES = {"dr house",
			"rsgp donate", "graham"};

	/**
	 * The maximum ban duration.
	 */
	public static final long MAX_DURATION = Time.ONE_WEEK * 2;

	/**
	 * A HashMap to store the bans.
	 */
	private Map<String, Ban> bans = new HashMap<String, Ban>();

	/**
	 * The sql connection.
	 */
	private SQLConnection sql;

	/**
	 * Gets the bans map.
	 *
	 * @return the bans map.
	 */
	public Map<String, Ban> getBans() {
		return bans;
	}

	/**
	 * Adds a ban to the manager.
	 *
	 * @param ban
	 */
	public void add(Ban ban) {
		// System.out.println("Adding ban: " + ban);
		bans.put(ban.getName(), ban);
	}

	/**
	 * Constructs a new BanManager.
	 *
	 * @param sql
	 */
	public BanManager(SQLConnection sql) {
		this.sql = sql;
	}

	/**
	 * Gets the ban status for the specified name or ip.
	 *
	 * @param key
	 * @return
	 */
	public int getStatus(String key) {
		key = key.toLowerCase();
		Ban ban = bans.get(key);
		int returncode;
		if(ban == null) {
			returncode = NO_BAN;
		} else if(ban.getExpirationTime() > System.currentTimeMillis()) {
			returncode = ban.getType();
		} else {
			unmoderate("Server", ban.getName(), ban.getType(), "expired");
			returncode = NO_BAN;
		}
		return returncode;
	}

	/**
	 * Checks if the specified name is bannable.
	 *
	 * @param name
	 * @return true is bannable, false if not.
	 */
	public static boolean isBannable(String name) {
		for(String unbannableName : UNBANNABLE_NAMES) {
			if(unbannableName.equalsIgnoreCase(name))
				return false;
		}
		return true;
	}

	/**
	 * Applies the punishment to the player for the specified ban type.
	 *
	 * @param player
	 * @param type
	 */
	private void applyPunishment(Player player, int type) {
		if(Rank.hasAbility(player, Rank.DEVELOPER))
			return;
		if(type == BAN)
			player.getSession().close(false);
		else if(type == MUTE)
			player.isMuted = true;
		else if(type == YELL)
			player.yellMuted = true;
		else if(type == BLACK_MARK)
			player.blackMarks++;
	}


	/**
	 * Moderates the specified player with the specified type of punishment.
	 *
	 * @param modName
	 * @param player
	 * @param type
	 * @param byIp
	 * @param expiration_time
	 */
	public void moderate(String modName, Player player, int type, boolean byIp, long expiration_time, String reason) {
		if(Rank.hasAbility(player, Rank.DEVELOPER))
			return;
		String name = player.getName().toLowerCase();
		String ip = player.getShortIP();
		moderate(modName, name, ip, type, byIp, expiration_time, reason);
		applyPunishment(player, type);
	}


	/**
	 * @param modName
	 * @param playerName
	 * @param ip
	 * @param type
	 * @param byIp
	 * @param expiration_time
	 */
	public void moderate(String modName, String playerName, String ip, int type, boolean byIp, long expiration_time, String reason) {
		if(! isBannable(playerName))
			return;
		long max_expiration_time = System.currentTimeMillis() + MAX_DURATION;
		if(expiration_time > max_expiration_time)
			expiration_time = max_expiration_time;
		Ban ban = new Ban(playerName, ip, byIp, expiration_time, type, modName + ": " + reason);
		Ban previous = bans.get(playerName);
		if(previous != null) {
			if(previous.getType() == ban.getType() && ban.getExpirationTime() - previous.getExpirationTime() < Time.ONE_HOUR) {
				return;
			}
		}
		if(ban.isByIp() && ip != null) {
			previous = bans.get(ip);
			if(previous != null) {
				if(previous.getType() == ban.getType() && ban.getExpirationTime() - previous.getExpirationTime() < Time.ONE_HOUR) {
					return;
				}
			}
		}
		bans.put(playerName, ban);
		if(byIp && ip != null)
			bans.put(ip, ban);
		save(ban);
		//writeLog(modName, "Punished " + playerName + ", type: " + type);
		String query = "INSERT INTO moderations (modname,targetname,punishment,type,reason) VALUES('" + modName + "','" + playerName + "',1," +
				type + ",'" + reason + "')";
		System.out.println(query);
		sql.offer(new QueryRequest(query));
	}

	/**
	 * Unmoderates the player with the specified name for the specified type.
	 *
	 * @param mod
	 * @param name
	 * @param type
	 * @return
	 */
	public String unmoderate(String modName, String name, int type, String reason) {
		Ban ban = bans.get(name);
		if(ban == null)
			return "Player was not punished.";
		if(ban.getType() != type) {
			return ban.getType() + "," + type
					+ "Player has a different type of punishment";
		}
		bans.remove(name);
		if(ban.isByIp())
			bans.remove(ban.getIp());
		//writeLog(modName, "Punishment " + type + " removed");
		String query = "INSERT INTO moderations (modname,targetname,punishment,type,reason) VALUES('" + modName + "','" + name + "',0," +
				type + ",'" + reason + "')";
		System.out.println(query);
		sql.offer(new QueryRequest(query));
		query = "DELETE FROM bans WHERE name = '" + name + "' AND type = " + type;
		sql.offer(new QueryRequest(query));
		return "Player was unpunished.";
	}

	/**
	 * Saves the specified ban.
	 *
	 * @param ban
	 */
	public void save(Ban ban) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO `bans`(`name`, `ip`, `byip`, `type`, `expiration`, `reason`) VALUES (");
		query.append("'" + ban.getName() + "',");
		query.append("'" + ban.getIp() + "',");
		query.append((ban.isByIp() && ban.getIp() != null ? 1 : 0) + ",");
		query.append(ban.getType() + ",");
		query.append(ban.getExpirationTime()+ ",");
        query.append("'" + ban.getReason() + "'");
		query.append(")");
		System.out.println(query.toString());
		//PlayerSaving.getSaving().saveLog("./banqueries.log", query.toString());
		sql.offer(new QueryRequest(query.toString()));
	}

	/**
	 * Writes a log for the mod.
	 *
	 * @param modName
	 * @param message
	 */
	private void writeLog(String modName, String message) {
		File saveFile = new File("./logs/bans/" + modName.toLowerCase()
				+ ".log");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(saveFile,
					true));
			out.write(Time.getGMTDate() + "\t" + message);
			out.newLine();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all previously set bans.
	 *
	 * @throws SQLException
	 */
	public void init() {
		try {
			long start = System.currentTimeMillis();
			ResultSet rs = sql.query("SELECT * FROM bans WHERE 1");
			if(rs == null)
				return;
			while(rs.next()) {
				long expiration_time = rs.getLong("expiration");
				String uncut = rs.getString("name");
				String name = "";
				String reason = "";
				if(uncut.indexOf("~") > 0) {
					name = uncut.substring(0, uncut.indexOf("~"));
					reason = uncut.substring(uncut.indexOf("~")+1, uncut.length());
				} else {
					name = uncut;
					reason = "Undefiend";
				}
				if(expiration_time < System.currentTimeMillis()) {
					String query = "DELETE FROM bans WHERE name = '" + name + "'";
					sql.offer(new QueryRequest(query));
					continue;
				}
				String ip = rs.getString("ip");
				boolean byip = rs.getBoolean("byip");
				int type = rs.getInt("type");
				Ban ban = new Ban(name, ip, byip, expiration_time, type);
				bans.put(name, ban);
				if(byip)
					bans.put(ip, ban);
			}
			long delta = System.currentTimeMillis() - start;
			System.out.println("Loaded BanManager in: " + delta + " ms.");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	static {
		CommandHandler.submit(new Command("warn", Rank.HELPER) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				input = filterInput(input);
				String[] args = input.split(",");
				if(args.length != 2) {
					player.getActionSender().sendMessage("You give black marks as such: ::warn name,amount");
					return false;
				}
				Player marked = World.getWorld().getPlayer(args[0]);
				if(marked != null) {
					try {
						int amount = Integer.parseInt(args[1]);
						marked.blackMarks += amount;
						marked.getActionSender().sendMessage("@red@You've just received: "+amount+" black marks from: "+player.getName());
					}catch(Exception e) {
						player.getActionSender().sendMessage("Incorrect mark amount");
						return false;
					}
				}
				return true;
			}
			
		});
		CommandHandler.submit(new Command("checkmarks", Rank.HELPER) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				input = filterInput(input);
				Player marked = World.getWorld().getPlayer(input);
				if(marked != null) {
					StringBuilder builder = new StringBuilder().append("Black marks for ").append(marked.getName());
					builder.append(" ,").append(marked.blackMarks);
					player.getActionSender().sendMessage(builder.toString());
				}
				return true;
			}
			
		});
		/*CommandHandler.submit(
				new BanCommand("yellmute", YELL, false, Rank.MODERATOR),
				new BanCommand("ban", BAN, false, Rank.MODERATOR),
				new BanCommand("ipban", BAN, true, Rank.GLOBAL_MODERATOR),
				new BanCommand("ipmute", MUTE, true, Rank.MODERATOR),
				new BanCommand("mute", MUTE, false, Rank.MODERATOR),
				new UnbanCommand("unyellmute", YELL, Rank.MODERATOR),
				new UnbanCommand("unban", BAN, Rank.MODERATOR),
				new UnbanCommand("unmute", MUTE, Rank.MODERATOR));
		CommandHandler.submit(new Command("listbans", Rank.FORUM_MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				Set<Entry<String, Ban>> bans = World.getWorld().getBanManager().getBans().entrySet();
				for(Map.Entry<String, Ban> entry : bans) {
					player.getActionSender().sendMessage(entry.getValue().toString());
					player.getActionSender().sendMessage("Reason: "+entry.getValue().getReason());
				}
				player.getActionSender().sendMessage("@red@Type 1: Mute, Type 2: Ban Type 3: Yellmute");
				return true;
			}
		});*/

	}
}
