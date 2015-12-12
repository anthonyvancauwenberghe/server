package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

import java.sql.SQLException;

public class ServerEconomyEvent extends SQLEvent {

    public ServerEconomyEvent() {
        super(Time.FIVE_MINUTES);
    }

    public void execute(final SQLConnection sql) throws SQLException {

    }
}
