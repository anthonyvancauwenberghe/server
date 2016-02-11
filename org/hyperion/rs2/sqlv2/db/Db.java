package org.hyperion.rs2.sqlv2.db;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.hyperion.Server;
import org.hyperion.rs2.sqlv2.DbHub;
import org.skife.jdbi.v2.DBI;

import java.util.logging.Level;

public abstract class Db {

    public DBI dbi;
    public abstract boolean isEnabled();
    public abstract String getUrl();
    public abstract String getUsername();
    public abstract String getPassword();

    public void init() {
        if(!isEnabled()){
            if(DbHub.isConsoleDebug())
                Server.getLogger().log(Level.INFO, "Db is not enabled - Not initializing: " + getClass().getSimpleName());
            return;
        }
        final MysqlConnectionPoolDataSource pool = new MysqlConnectionPoolDataSource();
        pool.setUrl(getUrl());
        pool.setUser(getUsername());
        pool.setPassword(getPassword());
        dbi = new DBI(pool);

        if(DbHub.isConsoleDebug())
            Server.getLogger().log(Level.INFO, "Successfully connected to " + getClass().getSimpleName() + ".");
        postInit();
    }

    protected abstract void postInit();
}
