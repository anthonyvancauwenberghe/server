package org.hyperion.rs2.sql;

import org.hyperion.rs2.util.RestarterThread;

import java.net.SocketTimeoutException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Arsen Maxyutov
 */
public abstract class MySQLConnection extends SQLConnection {

    static {
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The database url.
     */
    private final String url;
    /**
     * The database username.
     */
    private final String username;
    /**
     * The database password.
     */
    private final String password;
    private final long start = System.currentTimeMillis();
    private int queries_counter = 0;
    private Object lock;

    public MySQLConnection(final String name, final String url, final String username, final String password, final int create_connection_timer, final int max_cycle_sleep, final int min_cycle_sleep) {
        super(name, create_connection_timer, max_cycle_sleep, min_cycle_sleep);
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public double queriesPerSecond() {
        final long delta = (System.currentTimeMillis() - start) / 1000;
        return (double) queries_counter / (double) delta;
    }

    @Override
    public abstract boolean init();

    @Override
    public boolean createConnection() {
        if(System.currentTimeMillis() - lastConnectionCreated < create_connection_timer)
            return false;
        try{
            lastConnectionCreated = System.currentTimeMillis();
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected! " + this.getName());
            return true;
        }catch(final Exception e){
            System.out.format("URL %s - USER %s - PASS %s", url, username, password);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Processes the query string.
     *
     * @param s
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public synchronized ResultSet query(final String s) throws SQLException {


        lastQueryString = s;
        queries_counter++;
        if(queries_counter % 100 == 0){
            System.out.println(this.getName() + "- Queries speed: " + queriesPerSecond() + " q/s. Size queue: " + this.queue.size() + " sleep:" + sleep_time);
        }
        try{
            //World.getWorld().getGUI().setStatus("About to create statement for " + s);
            final Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);
            ResultSet rs = null;
            if(s.toLowerCase().startsWith("select")){
                rs = statement.executeQuery(s);
            }else{
                statement.executeUpdate(s);
                if(s.toLowerCase().startsWith("insert")){
                    rs = statement.getGeneratedKeys();
                }
            }
            //World.getWorld().getGUI().setStatus("Finished executing " + s);
            RestarterThread.getRestarter().updateSQLTimer();
            lastQuery = System.currentTimeMillis();
            return rs;
        }catch(final Exception e){
            if(e instanceof SocketTimeoutException){
                System.out.println("Can't process query: " + s);
                //e.printStackTrace();
            }else{
                System.out.println("Error:" + s);
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Destroys the SQL connection.
     */
    @Override
    public void close() {
        if(connection != null){
            try{
                connection.close();
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
    }

    public PreparedStatement prepare(final String sql) {
        try{
            return connection.prepareStatement(sql);
        }catch(final Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

}
