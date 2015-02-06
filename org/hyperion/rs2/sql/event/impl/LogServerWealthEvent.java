package org.hyperion.rs2.sql.event.impl;

import java.sql.SQLException;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

public class LogServerWealthEvent extends SQLEvent {
    public static final long DELAY = Time.FIVE_MINUTES;

    public LogServerWealthEvent() {
        super(DELAY);
    }

    public void execute(SQLConnection con) throws SQLException {
        con.query("INSERT INTO serverwealth (value) VALUES ((SELECT SUM(value) FROM accountvalues WHERE value>100))");
        super.updateStartTime();
    }
}
