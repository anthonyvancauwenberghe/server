package org.hyperion.rs2.sql;

import java.sql.DriverManager;

public abstract class SQLiteConnection extends SQLConnection {

    private final String fileName;

    public SQLiteConnection(final String name, final String fileName, final int create_connection_timer, final int max_cycle_sleep, final int min_cycle_sleep) {
        super(name, create_connection_timer, max_cycle_sleep, min_cycle_sleep);
        this.fileName = fileName;
    }

    public abstract boolean startupQueries();

    @Override
    public boolean init() {
        createConnection();
        startupQueries();
        start();
        return true;
    }

    @Override
    protected boolean createConnection() {
        if(System.currentTimeMillis() - lastConnectionCreated < create_connection_timer)
            return false;
        try{
            lastConnectionCreated = System.currentTimeMillis();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            return true;
        }catch(final Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
