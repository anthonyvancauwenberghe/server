package org.hyperion.rs2.sql.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

import java.sql.SQLException;

public class PlayerStatisticsUpdate extends SQLEvent {

    public static final long DELAY = Time.FIVE_MINUTES;

    public PlayerStatisticsUpdate() {
        super(DELAY);
    }


    @Override
    public void execute(final SQLConnection sql) throws SQLException {
        sql.query(Server.getStats().getQuery());
        super.updateStartTime();
    }

}
