package org.hyperion.rs2.sql;

import org.hyperion.Configuration;
import org.hyperion.rs2.sql.event.impl.LogPlayercountEvent;
import org.hyperion.rs2.sql.event.impl.LogServerWealthEvent;

public class LogsSQLConnection extends MySQLConnection {

    public LogsSQLConnection(Configuration config) {
        super("LogsSQL", config.getString("logsurl"), config.getString("logsuser"), config.getString("logspass"), 30000,
                10000, 100);
    }


    public LogsSQLConnection(String url, String username, String password) {
        super("LogsSQL", url, username, password, 30000,
                10000, 100);
    }

    @Override
    public boolean init() {
        /*if(!Server.getConfig().getBoolean("sql"))
			return false;*/
        establishConnection();
        submit(new LogPlayercountEvent());
        submit(new LogServerWealthEvent());
        start();
        return true;
    }
}