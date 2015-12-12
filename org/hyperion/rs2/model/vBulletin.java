package org.hyperion.rs2.model;

/**
 * vBulletin class
 *
 * @author Mad Turnip
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class vBulletin implements Runnable {


    private static final Thread thread = null;
    private final String hostAddress;
    private final String username;
    private final String password;
    private final Type forum;
    private Connection connection = null;
    private Statement statement = null;
    private String[] tableNames = new String[6];
    private PreparedStatement preparedStatement;
    private PreparedStatement preparedStatement2;

    public vBulletin(final String url, final String database, final String username, final String password, final Type t) {
        this.hostAddress = "jdbc:mysql://" + url + "/" + database;
        this.username = username;
        this.password = password;
        this.forum = t;
        try{
            //connect();

			/*thread = new Thread(this);
            thread.start();*/
        }catch(final Exception e){
            connection = null;
            e.printStackTrace();
        }
    }

    private void setTables() {
        if(forum == Type.myBB){
            tableNames = new String[]{"mybb_users", "username", "password", "salt", "usergroup",};
        }else if(forum == Type.SMF){
            tableNames = new String[]{"smf_", "username", "password", "password", "usergroupid",};
        }else if(forum == Type.IPB){
            tableNames = new String[]{"members", "name", "converge_pass_hash", "converge_pass_salt", "mgroup",};
        }else if(forum == Type.vBulletin){//vbulletin
            tableNames = new String[]{"user", "username", "password", "salt", "usergroupid",};
        }else if(forum == Type.phpBB){//phpBB
            tableNames = new String[]{"users", "username", "user_password", "user_password", "group_id",};
        }
    }

    private void connect() {
        if(/*!Server.dedi*/true)
            return;
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }catch(final Exception e2){
            System.out.println("Cannot find mySql Driver.");
            return;
        }
        try{
            final Connection connection2 = DriverManager.getConnection(hostAddress, username, password);
            connection = connection2;
            statement = connection.createStatement();
            getStatement();
        }catch(final Exception e){
            System.out.println("Connetion rejected, Wrong username or password, or ip is banned, or host is down");
            connection = null;
            //e.printStackTrace();
        }
    }

    private void ping() {
        if(/*!Server.dedi*/true)
            return;
        try{
            String query = "SELECT * FROM " + tableNames[0] + " WHERE " + tableNames[1] + " = ?";
            if(preparedStatement == null)
                preparedStatement = connection.prepareStatement(query);

			/*query = "SELECT * FROM donation WHERE username = ?";
			if (preparedStatement2 == null)
				preparedStatement2 = connection.prepareStatement(query);*/

            query = "SELECT * FROM " + tableNames[0] + " WHERE " + tableNames[2] + " LIKE 'null312'";
            statement.executeQuery(query);
        }catch(final Exception e){
            connection = null;
            preparedStatement = null;
            connect();
            e.printStackTrace();
        }
    }

    public void run() {
        final boolean allowRun = true;
        while(allowRun){
            try{
                if(connection == null){
                    setTables();
                    connect();
                }else{
                    ping();
                }
                Thread.sleep(10000);
            }catch(final Exception e){
            }
        }
    }

    public boolean connectionOpen() {
        if(connection == null)
            return false;
        return true;
    }

    private PreparedStatement getStatement() {
        if(connection == null)
            return null;
        try{
            /*if(preparedStatement != null)
				if(preparedStatement.isClosed())
					preparedStatement = connection.prepareStatement(query);*/
            return preparedStatement;
        }catch(final Exception e){
            e.printStackTrace();
            connection = null;
            preparedStatement = null;
            //connect();
            //e.printStackTrace();
        }
        return null;
    }

    private PreparedStatement getStatement2() {
        if(connection == null)
            return null;
        try{
            return preparedStatement2;
        }catch(final Exception e){
            e.printStackTrace();
            connection = null;
            preparedStatement = null;
        }
        return null;
    }

    /**
     * returns 2 integers, the return code and the usergroup of the player
     */
    public int[] checkUser(final String name, final String password) {
        final int[] returnCodes = {8, 0};//return code for client, group id
        if(/*!Server.dedi*/true){
            //returnCodes[1] = 6;
            return returnCodes;
        }
        final long startTime = System.currentTimeMillis();
        try{
            ResultSet results = null;
            //String query = "SELECT * FROM "+tableNames[0]+" WHERE "+tableNames[1]+" LIKE '"+name+"'";
            /*if(statement == null)
				statement = connection.createStatement();
			} catch(Exception e5){
				statement = null;
				connection = null;
				connect();
				statement = connection.createStatement();
			}*/
            final PreparedStatement pStatement = getStatement();
            if(pStatement == null){
                returnCodes[0] = 8;
                return returnCodes;
            }
            pStatement.setString(1, name);

            results = pStatement.executeQuery();
            if(results.next()){
                final String salt = results.getString(tableNames[3]);
                final String pass = results.getString(tableNames[2]);
                final int group = results.getInt(tableNames[4]);
                returnCodes[1] = group;
                String pass2 = "";
                if(forum == Type.myBB){
                    //$md5pass = md5(md5($salt).md5($rawpass));
                    pass2 = MD5.MD5Compute(MD5.MD5Compute(salt) + MD5.MD5Compute(password));
                }else if(forum == Type.vBulletin){
                    pass2 = MD5.MD5Compute(password);
                    pass2 = MD5.MD5Compute(pass2 + salt);
                }else if(forum == Type.SMF){
                    pass2 = MD5.SHA((name.toLowerCase()) + password);
                }else if(forum == Type.phpBB){
                    pass2 = MD5.MD5Compute(password);
                }else if(forum == Type.IPB){
                    pass2 = MD5.MD5Compute(MD5.MD5Compute(salt) + MD5.MD5Compute(password));
                }
                if(System.currentTimeMillis() - startTime >= 10000){
                    preparedStatement = null;
                }
                if(pass.equals(pass2)){
                    returnCodes[0] = 2;
                    return returnCodes;
                }else{
                    returnCodes[0] = 3;
                    return returnCodes;
                }
            }else{
                //no user exists
                returnCodes[0] = 12;
                return returnCodes;
            }
        }catch(final Exception e){
            e.printStackTrace();
            preparedStatement = null;
            returnCodes[0] = 8;
            return returnCodes;
        }
    }

    public enum Type {
        myBB,
        SMF,
        IPB,
        vBulletin,
        phpBB,
    }
}
