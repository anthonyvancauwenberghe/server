package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Password;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.util.PasswordEncryption;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * @author Arsen Maxyutov
 */

public class SQLPlayerSaving extends PlayerSaving {

    /**
     * The table holding all player properties.
     */
    public static final String PLAYERS_TABLE = "playerfiles";

    /**
     * The table holding all inventory items.
     */
    public static final String INVENTORY_TABLE = "player_inventory";

    /**
     * The table holding all bank items.
     */
    public static final String BANK_TABLE = "player_bank";

    /**
     * The table holding all equipment.
     */
    public static final String EQUIPMENT_TABLE = "player_equipment";

    /**
     * The table holding all friendlists.
     */
    public static final String FRIENDS_TABLE = "player_friends";

    /**
     * The table holding all skills data.
     */
    public static final String SKILLS_TABLE = "player_skills";

    static {

    }

    /**
     * The sql connection.
     */
    private final SQLConnection sql;
    private LinkedList<SaveSingleValue> savingFields;
    private PreparedStatement createPlayer;

    /**
     * @param sql
     */
    public SQLPlayerSaving(final SQLConnection sql) {
        this.sql = sql;
        try{
            createPlayer = this.getCreatePlayerStatement();
        }catch(final SQLException e1){
            e1.printStackTrace();
        }
        try{
            final ResultSet rs = sql.query("SELECT * FROM " + PLAYERS_TABLE + "  LIMIT 1");
            if(rs == null)
                return;
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int columnCount = rsmd.getColumnCount();
            for(final Entry<String, SaveObject> entry : this.saveData.entrySet()){
                final String key = entry.getKey().toLowerCase();
                final SaveObject so = entry.getValue();
                if(so instanceof SaveSingleValue){
                    boolean found = false;
                    for(int i = 1; i <= columnCount; i++){
                        if(key.equalsIgnoreCase(rsmd.getColumnName(i))){
                            found = true;
                            break;
                        }
                    }
                    if(!found){
                        System.out.println("Field " + key + " was not added!");
                        String query = null;
                        if(so instanceof SaveInteger){
                            query = "ALTER TABLE `playerfiles` ADD `" + key + "` INT NOT NULL DEFAULT '0'";
                        }else if(so instanceof SaveString){
                            query = "ALTER TABLE  `playerfiles` ADD  `" + key + "` VARCHAR( 50 ) NOT NULL DEFAULT '';";
                        }else if(so instanceof SaveBoolean){
                            query = "ALTER TABLE  `playerfiles` ADD  `" + key + "` BOOLEAN NOT NULL DEFAULT FALSE ;";
                        }else if(so instanceof SaveLong){
                            query = "ALTER TABLE `playerfiles` ADD `" + key + "` BIGINT NOT NULL DEFAULT '0'";
                        }
                        System.out.println(query);
                        if(query != null)
                            sql.query(query);
                        else
                            System.out.println("Query in SQLSaving is null !hax");
                    }
                }
            }
            rs.close();
        }catch(final SQLException e){
            e.printStackTrace();
        }
    }

    public void save(final Player player, final String message) {
        System.out.println(message);
        save(player);
    }

    /**
     * Saves player properties, player items and player skills to a SQL database.
     *
     * @param player
     * @return
     */
    public boolean save(final Player player) {
        /*
        long start = System.nanoTime();
        try {
            saveSingleValues(player);
        } catch(Exception e) {
            e.printStackTrace();
        }
        long delta = System.nanoTime() - start;
        if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
            System.out.println("Save Delta: " + delta + " nanoseconds.");
            */
        return true;
    }

    /**
     * @param player
     * @return
     */
    public void saveSingleValues(final Player player) {
        if(player.getDatabaseId() < 0)
            return;
        //System.out.println("Save single values calling");
        final StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + PLAYERS_TABLE + " SET ");
        boolean first = true;
        if(player.getSavedValues() == null)
            return;
        for(final SavedSingleValue ssv : player.getSavedValues()){
            final SaveSingleValue so = ssv.getSavedSingleValue();
            final String name = so.getName().toLowerCase();
            //System.out.println(so.getName() + " --- " + ssv.getPreviousValue() + "/" + so.getValue(player));
            final Object value = so.getValue(player);
            if(ssv.getPreviousValue().equals(so.getValue(player)))
                continue;
            if(name.equals("pass"))
                continue;
            //ssv.setPreviousValue(value); RETURN THIS

			/*if(name.equals("pass")) {
				World.getWorld().getLogsConnection().offer("INSERT INTO logs(username,message) VALUES('%s','%s')",player.getName(),"saving pass: " + value);
			}*/
            if(!first)
                sb.append(",");
            sb.append("`").append(name).append("`");
            sb.append(" = ");
            if(so instanceof SaveString){
                sb.append("'").append(value).append("'");
            }else{
                sb.append(value);
            }
            first = false;
        }
        if(!first){
            sb.append(" WHERE player_id = ").append(player.getDatabaseId());
            System.out.println(sb);
            sql.offer(sb.toString());
        }
    }

    /**
     * Loads the player.
     */
    public boolean load(final Player player) {
        final String player_name = player.getName().toLowerCase();
        System.out.println("Loading player: " + player_name);

        try{
            final String query = String.format("SELECT * FROM " + PLAYERS_TABLE + " WHERE name = '%s'", player_name);
            final ResultSet rs = sql.query(query);
            int database_id = -1;
            player.getSavedValues().clear();
            if(rs != null && rs.next()){
                database_id = rs.getInt("player_id");
                System.out.println("Found in playerfiles. id is " + database_id);
                player.setDatabaseId(database_id);
                for(final SaveObject so : saveList){
                    if(so instanceof SaveSingleValue){
                        final SaveSingleValue ssv = (SaveSingleValue) so;
                        final String columnName = so.getName().toLowerCase();
                        if(columnName.equals("salt") || columnName.equals("pass"))
                            continue;
                        final Object value = ssv.getValue(columnName, rs);
                        final SavedSingleValue sv = new SavedSingleValue(ssv, value);
                        player.getSavedValues().add(sv);
                    }
                }

            }else{
                System.out.println("Did not find you in playerfiles. Lets add you");

                create(player);
                for(final SaveObject so : saveList){
                    if(so instanceof SaveSingleValue){
                        final SaveSingleValue s = (SaveSingleValue) so;
                        final SavedSingleValue sv = new SavedSingleValue(s, s.getValue(player));
                        player.getSavedValues().add(sv);
                    }
                }
            }

        }catch(final Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Inserts the player into the SQL database.
     *
     * @param player
     * @return
     */
    public boolean create(final Player player) {
        try{
            final long start = System.currentTimeMillis();
            final boolean success = createPlayer(player);
            if(success){
                final int id = player.getDatabaseId();
                final long delta = System.currentTimeMillis() - start;
                System.out.println("Create Delta is " + delta + " ms.");
            }else{
                //World.getWorld().getLogsConnection().offer("INSERT INTO logs(username,message) VALUES('%s','%s')",player.getName(),"tried to create player but failed");

            }
        }catch(final Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public PreparedStatement getCreatePlayerStatement() throws SQLException {
        final StringBuilder sb = new StringBuilder();
        savingFields = new LinkedList<SaveSingleValue>();
        sb.append("INSERT INTO " + PLAYERS_TABLE + " (");
        boolean first = true;
        for(final SaveObject so : saveList){
            if(so instanceof SaveSingleValue){
                if(!first)
                    sb.append(",");
                sb.append("`").append(so.getName().toLowerCase()).append("`");
                savingFields.add((SaveSingleValue) so);
                first = false;
            }
        }
        first = true;
        sb.append(")VALUES(");
        for(final SaveSingleValue ssv : savingFields){
            if(!first)
                sb.append(",");
            sb.append("?");
            first = false;
        }
        sb.append(")");
        final String query = sb.toString();
        System.out.println(query);
        return sql.getConnection().prepareStatement(query);
    }

    /**
     * @param player
     * @throws java.sql.SQLException
     */
    private boolean createPlayer(final Player player) throws SQLException {
        if(player.getPassword().getSalt() == null){
            player.getPassword().setSalt(PasswordEncryption.generateSalt());
            final String enc = Password.encryptPassword(player.getPassword().getRealPassword(), player.getPassword().getSalt());
            player.getPassword().setEncryptedPass(enc);
        }else{
            //World.getWorld().getLogsConnection().offer("INSERT INTO logs(username,message) VALUES('%s','%s')",player.getName(),"creating player but salt already set");
        }
        int index = 1;
        for(final SaveSingleValue ssv : savingFields){
            if(ssv instanceof SaveString && ssv != null && player != null){
                createPlayer.setString(index, ssv.getValue(player).toString());
            }else if(ssv instanceof SaveBoolean){
                if((Boolean) ssv.getValue(player)){
                    createPlayer.setInt(index, 1);
                }else{
                    createPlayer.setInt(index, 0);
                }
            }else if(ssv instanceof SaveLong){
                createPlayer.setLong(index, (long) ssv.getValue(player));
            }else{
                createPlayer.setInt(index, (int) ssv.getValue(player));
            }
            index++;
        }
        createPlayer.executeUpdate();
        final ResultSet rs = createPlayer.getGeneratedKeys();
        int id = -1;
        if(rs.next()){
            id = rs.getInt(1);
        }
        System.out.println("Created new player");
        if(id >= 0){
            System.out.println("Id of player is : " + id);
            player.setDatabaseId(id);
            return true;
        }
        return false;
    }

}
