package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

import java.sql.SQLException;

public class LogServerWealthEvent extends SQLEvent {
    public static final long DELAY = Time.FIVE_MINUTES;

    public LogServerWealthEvent() {
        super(DELAY);
    }

    public void execute(SQLConnection con) throws SQLException {
        con.query("INSERT INTO serverwealth (value,pkvalue,activevalue,activepkvalue) \n" +
                "VALUES ((\n" +
                "SELECT SUM(value) FROM accountvalues WHERE value>100 AND name<>'thomas' AND name<>'rwt bank' ),\n" +
                "(SELECT SUM(pkvalue) FROM accountvalues WHERE value>100 AND name<>'thomas' AND name<>'rwt bank'),\n" +
                "(SELECT SUM(value) FROM accountvalues WHERE value>100 AND name<>'thomas' AND name<>'rwt bank' AND (`Timestamp` > DATE_SUB(now(), INTERVAL 14 DAY))),\n" +
                "(SELECT SUM(pkvalue) FROM accountvalues WHERE pkvalue>100 AND name<>'thomas' AND name<>'rwt bank' AND (`Timestamp` > DATE_SUB(now(), INTERVAL 14 DAY))))");
        super.updateStartTime();
    }
}
