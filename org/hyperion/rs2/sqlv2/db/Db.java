package org.hyperion.rs2.sqlv2.db;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.skife.jdbi.v2.DBI;

public abstract class Db {

    private final DbConfig config;

    protected DBI dbi;

    public Db(final DbConfig config) {
        this.config = config;
    }

    public DbConfig config() {
        return config;
    }

    public boolean enabled() {
        return config.enabled();
    }

    public void init() {
        if(!enabled()){
            if(DbConfig.consoleDebug)
                System.out.println("Db is not enabled - Not initializing: " + getClass().getSimpleName());
            return;
        }
        final MysqlConnectionPoolDataSource pool = new MysqlConnectionPoolDataSource();
        pool.setUrl(config.url());
        pool.setUser(config.user());
        pool.setPassword(config.pass());
        dbi = new DBI(pool);

        postInit();
    }

    protected abstract void postInit();
}
