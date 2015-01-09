package org.hyperion.rs2.sql;

import org.hyperion.rs2.sql.event.impl.LogServerWealthEvent;

public class LocalServerSQLConnection extends MySQLConnection {

    public LocalServerSQLConnection() {
        super("LocalServerSQL", "jdbc:mysql://localhost/server", "root", "pjr9yGgw4Mjv", 30000, 60000, 100);
    }

    public boolean init() {
        establishConnection();
        submit(new LogServerWealthEvent());
        start();
        return true;
    }
}
