package org.hyperion.rs2.sql.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

import java.sql.SQLException;

public class LogPlayercountEvent extends SQLEvent {

    public static final long DELAY = Time.ONE_MINUTE;

    public LogPlayercountEvent() {
        super(DELAY);
    }

    @Override
    public void execute(final SQLConnection sql) throws SQLException {
        if(Server.getUptime().minutesUptime() >= 20){
            final int playerCount = World.getWorld().getPlayers().size();
            final int staffOnline = World.getWorld().getStaffManager().getOnlineStaff().size();
            sql.query("INSERT INTO playercount(count,staffonline) VALUES(" + playerCount + "," + staffOnline + ")");
        }
        super.updateStartTime();
    }

}
