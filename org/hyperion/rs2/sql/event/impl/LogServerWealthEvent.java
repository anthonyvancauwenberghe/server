package org.hyperion.rs2.sql.event.impl;

import java.sql.SQLException;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

public class LogServerWealthEvent extends SQLEvent{

    public LogServerWealthEvent(){
        super(Time.FIVE_MINUTES);
    }

    public void execute(final SQLConnection con) throws SQLException{
        con.query("INSERT INTO serverwealth (value) VALUES ((SELECT SUM(value) FROM accountvalues))");
    }
}
