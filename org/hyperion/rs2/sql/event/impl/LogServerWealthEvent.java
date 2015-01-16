package org.hyperion.rs2.sql.event.impl;

import java.sql.SQLException;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;

public class LogServerWealthEvent extends SQLEvent {
    public static final int CYCLETIME = 60000;

    public LogServerWealthEvent() {
        super(CYCLETIME);
    }

    public void execute(SQLConnection con) throws SQLException {
        con.query("INSERT INTO serverwealth (value) VALUES ((SELECT SUM(value) FROM accountvalues))");
        super.updateStartTime();
    }
}
