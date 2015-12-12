package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.util.Time;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
    public static final String[] UNBANNABLE_NAMES = {"dr house", "rsgp donate", "graham"};

    /**
     * The maximum ban duration.
     */
    public static final long MAX_DURATION = Time.ONE_WEEK * 2;

    static {
        CommandHandler.submit(new Command("warn", Rank.HELPER) {

            @Override
            public boolean execute(final Player player, String input) throws Exception {
                input = filterInput(input);
                final String[] args = input.split(",");
                if(args.length != 2){
                    player.getActionSender().sendMessage("You give black marks as such: ::warn name,amount");
                    return false;
                }
                final Player marked = World.getWorld().getPlayer(args[0]);
                if(marked != null){
                    try{
                        final int amount = Integer.parseInt(args[1]);
                        marked.blackMarks += amount;
                        marked.getActionSender().sendMessage("@red@You've just received: " + amount + " black marks from: " + player.getName());
                    }catch(final Exception e){
                        player.getActionSender().sendMessage("Incorrect mark amount");
                        return false;
                    }
                }
                return true;
            }

        });
        CommandHandler.submit(new Command("checkmarks", Rank.HELPER) {

            @Override
            public boolean execute(final Player player, String input) throws Exception {
                input = filterInput(input);
                final Player marked = World.getWorld().getPlayer(input);
                if(marked != null){
                    final StringBuilder builder = new StringBuilder().append("Black marks for ").append(marked.getName());
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

    /**
     * A HashMap to store the bans.
     */
    private final Map<String, Ban> bans = new HashMap<String, Ban>();
    /**
     * The sql connection.
     */
    private final SQLConnection sql;

    /**
     * Constructs a new BanManager.
     *
     * @param sql
     */
    public BanManager(final SQLConnection sql) {
        this.sql = sql;
    }

    /**
     * Checks if the specified name is bannable.
     *
     * @param name
     * @return true is bannable, false if not.
     */
    public static boolean isBannable(final String name) {
        for(final String unbannableName : UNBANNABLE_NAMES){
            if(unbannableName.equalsIgnoreCase(name))
                return false;
        }
        return true;
    }

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
    public void add(final Ban ban) {
        // System.out.println("Adding ban: " + ban);
        bans.put(ban.getName(), ban);
    }

    /**
     * Gets the ban status for the specified name or ip.
     *
     * @param key
     * @return
     */
    public int getStatus(String key) {
        key = key.toLowerCase();
        final Ban ban = bans.get(key);
        final int returncode;
        if(ban == null){
            returncode = NO_BAN;
        }else if(ban.getExpirationTime() > System.currentTimeMillis()){
            returncode = ban.getType();
        }else{
            unmoderate("Server", ban.getName(), ban.getType(), "expired");
            returncode = NO_BAN;
        }
        return returncode;
    }

    /**
     * Applies the punishment to the player for the specified ban type.
     *
     * @param player
     * @param type
     */
    private void applyPunishment(final Player player, final int type) {
        if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
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
    public void moderate(final String modName, final Player player, final int type, final boolean byIp, final long expiration_time, final String reason) {
        if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
            return;
        final String name = player.getName().toLowerCase();
        final String ip = player.getShortIP();
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
    public void moderate(final String modName, final String playerName, final String ip, final int type, final boolean byIp, long expiration_time, final String reason) {
        if(!isBannable(playerName))
            return;
        final long max_expiration_time = System.currentTimeMillis() + MAX_DURATION;
        if(expiration_time > max_expiration_time)
            expiration_time = max_expiration_time;
        final Ban ban = new Ban(playerName, ip, byIp, expiration_time, type, modName + ": " + reason);
        Ban previous = bans.get(playerName);
        if(previous != null){
            if(previous.getType() == ban.getType() && ban.getExpirationTime() - previous.getExpirationTime() < Time.ONE_HOUR){
                return;
            }
        }
        if(ban.isByIp() && ip != null){
            previous = bans.get(ip);
            if(previous != null){
                if(previous.getType() == ban.getType() && ban.getExpirationTime() - previous.getExpirationTime() < Time.ONE_HOUR){
                    return;
                }
            }
        }
        bans.put(playerName, ban);
        if(byIp && ip != null)
            bans.put(ip, ban);
        save(ban);
        //writeLog(modName, "Punished " + playerName + ", type: " + type);
        final String query = "INSERT INTO moderations (modname,targetname,punishment,type,reason) VALUES('" + modName + "','" + playerName + "',1," +
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
    public String unmoderate(final String modName, final String name, final int type, final String reason) {
        final Ban ban = bans.get(name);
        if(ban == null)
            return "Player was not punished.";
        if(ban.getType() != type){
            return ban.getType() + "," + type + "Player has a different type of punishment";
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
    public void save(final Ban ban) {
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO `bans`(`name`, `ip`, `byip`, `type`, `expiration`, `reason`) VALUES (");
        query.append("'").append(ban.getName()).append("',");
        query.append("'").append(ban.getIp()).append("',");
        query.append(ban.isByIp() && ban.getIp() != null ? 1 : 0).append(",");
        query.append(ban.getType()).append(",");
        query.append(ban.getExpirationTime()).append(",");
        query.append("'").append(ban.getReason()).append("'");
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
    private void writeLog(final String modName, final String message) {
        final File saveFile = new File("./logs/bans/" + modName.toLowerCase() + ".log");
        try{
            final BufferedWriter out = new BufferedWriter(new FileWriter(saveFile, true));
            out.write(Time.getGMTDate() + "\t" + message);
            out.newLine();
            out.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Loads all previously set bans.
     *
     * @throws SQLException
     */
    public void init() {
        try{
            final long start = System.currentTimeMillis();
            final ResultSet rs = sql.query("SELECT * FROM bans WHERE 1");
            if(rs == null)
                return;
            while(rs.next()){
                final long expiration_time = rs.getLong("expiration");
                final String uncut = rs.getString("name");
                String name = "";
                String reason = "";
                if(uncut.indexOf("~") > 0){
                    name = uncut.substring(0, uncut.indexOf("~"));
                    reason = uncut.substring(uncut.indexOf("~") + 1, uncut.length());
                }else{
                    name = uncut;
                    reason = "Undefiend";
                }
                if(expiration_time < System.currentTimeMillis()){
                    final String query = "DELETE FROM bans WHERE name = '" + name + "'";
                    sql.offer(new QueryRequest(query));
                    continue;
                }
                final String ip = rs.getString("ip");
                final boolean byip = rs.getBoolean("byip");
                final int type = rs.getInt("type");
                final Ban ban = new Ban(name, ip, byip, expiration_time, type);
                bans.put(name, ban);
                if(byip)
                    bans.put(ip, ban);
            }
            final long delta = System.currentTimeMillis() - start;
            System.out.println("Loaded BanManager in: " + delta + " ms.");
        }catch(final SQLException e){
            e.printStackTrace();
        }
    }
}
