package org.hyperion.rs2.model.content.grandexchange;

import org.hyperion.rs2.event.impl.UpdateEvent;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.Restart;
import org.madturnip.tools.SQLAdminManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ServerDatabase extends Thread {


    private static final String[] tables = {
            //"DROP TABLE hyp_grandexchange2",
            //"DROP TABLE hyp_grandexchange",
            //"DROP TABLE hyp_bans_mac",
            //"DROP TABLE `hyp_bans`",
            "CREATE TABLE `hyp_bans` (`username` varchar(32) NOT NULL,`modname` varchar(32) NOT NULL,`moderatelevel` int(2) NOT NULL,`length` int(32) NOT NULL,`date` long(32) NOT NULL)",
            "CREATE TABLE `hyp_bans_mac` (`macId` int(32) NOT NULL,`username` varchar(32) NOT NULL,`modname` varchar(32) NOT NULL,`moderatelevel` int(2) NOT NULL,`length` int(32) NOT NULL,`date` long(32) NOT NULL)",
            "CREATE TABLE `hyp_grandexchange2` (`username` varchar(30) NOT NULL,`itemId` int(6) NOT NULL,`itemAm` int(20) NOT NULL,`price` int(20) NOT NULL,`time` long(9) NOT NULL,`itemName` varchar(30) NOT NULL,`type` int(10) NOT NULL)",
            //"CREATE TABLE `hyp_grandexchange` (`id` int(11) NOT NULL,`username` varchar(30) NOT NULL,`itemId` int(6) NOT NULL,`itemAm` int(20) NOT NULL,`price` int(20) NOT NULL,`day` int(5) NOT NULL,`hour` int(5) NOT NULL,`itemName` varchar(30) NOT NULL,`type` int(10) NOT NULL,PRIMARY KEY (`id`))",
            "CREATE TABLE `hyp_bloodlust` (`clanname` varchar(32) NOT NULL,`player1` varchar(32) NOT NULL,`player2` varchar(32) NOT NULL,`player3` varchar(32) NOT NULL,`player4` varchar(32) NOT NULL,`player5` varchar(32) NOT NULL,`password` varchar(32) NOT NULL,`kills` int(32) NOT NULL,`deaths` int(32) NOT NULL,`count` int(32) NOT NULL)",
            "CREATE TABLE `hyp_grandmoney` (`username` varchar(32) NOT NULL,`money` bigint(255) NOT NULL,PRIMARY KEY (`username`))",};
    private static Statement statement = null;
    private static Connection connection = null;
    private static String database;
    private int counter = 0;


    public ServerDatabase(final String database) {
        this.database = database;
        ServerDatabase.connect();
        new SQLAdminManager();
    }

    public static boolean isClosed() throws SQLException {
        if(connection == null || connection.isClosed())
            return true;
        return false;
    }

    private static void connect() {
        /*if(!Server.dedi)
            return;*/
        try{
            //Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("SQLite.JDBCDriver");
            Class.forName("org.sqlite.JDBC");
        }catch(final Exception e2){
            //System.out.println("Cannot find mySql Driver.");
            return;
        }
        try{
            //connection = (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1/darkstar", "root","YahooNimbus100");
            connection = (Connection) DriverManager.getConnection("jdbc:sqlite:/data/" + database + ".db");
            statement = (Statement) connection.createStatement();
            //query("CREATE TABLE `hyp_bans` (`username` varchar(32) NOT NULL,`modname` varchar(32) NOT NULL,`moderatelevel` int(2) NOT NULL,`length` int(32) NOT NULL,`date` Timestamp(32) NOT NULL)");
        }catch(final Exception e){
            //System.out.println("Connetion rejected, Wrong username or password, or ip is banned, or host is down.");
            connection = null;
            e.printStackTrace();
            //e.printStackTrace();
        }
    }

    public static ResultSet query(final String query) {
        if(connection == null)
            return null;
        try{
            statement = (Statement) connection.createStatement();
            if(query.startsWith("SELECT")){
                return (ResultSet) statement.executeQuery(query);
            }else if(query.startsWith("UPDATE")){
                statement.executeUpdate(query);
            }else{
                statement.execute(query);
            }
        }catch(final Exception e){
            statement = null;
            connection = null;
            e.printStackTrace();
        }
        return null;
    }

    public static void main(final String[] args) {
        final ServerDatabase db = new ServerDatabase("database");
        db.start();
        for(int i = 0; i < tables.length; i++){
            try{
                query(tables[i]);
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updatePlayersOnline() {
        try{
            //System.out.println("Running DeviousPK Player Updating Code");
            final int playercount = World.getWorld().getPlayers().size();
            final URL url = new URL("http://www.DeviousPK.com/setplayercount.php?players=" + playercount);
            final HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.getContent();
            con.disconnect();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                counter++;
                if(counter == 5){
                    //updatePlayersOnline();
                    counter = 0;
                }
                if(UpdateEvent.shutDown()){
                    System.out.println("Shutting down Server Database class: " + System.currentTimeMillis());
                    new Restart("UpdateEvent has stopped").execute();
                }
                if(connection == null || connection.isClosed()){
                    connect();
                }else{
                    //connection.ping();
                }
                Thread.sleep(10000);
            }catch(final Exception e){
                connection = null;
                statement = null;
                e.printStackTrace();
            }
        }
    }
}
