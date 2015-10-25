package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.RichWhitelistRequest;
import org.hyperion.util.Time;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 25/10/2015.
 */
public class RichWhitelistEvent extends SQLEvent {

    public static boolean enabled = true;
    public static List<String> whitelist = new ArrayList();

    public RichWhitelistEvent(){
        super(Time.ONE_MINUTE);
    }

    @Override
    public void execute(SQLConnection sql) throws SQLException {
        if(!enabled)
            this.stop();
        sql.offer(new RichWhitelistRequest());
    }
}
