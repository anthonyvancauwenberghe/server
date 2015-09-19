package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLStartupEvent;

/**
 * Created by Gilles on 19/09/2015.
 */
public class CleanVotesEvent extends SQLStartupEvent {

    @Override
    public void run(SQLConnection sql) {
        if (!sql.isConnected()) {
            return;
        }

        try {
            sql.query(String.format("DELETE FROM waitingVotes WHERE `timestamp` < DATE_SUB(NOW(), INTERVAL 14 DAY)"));
            System.out.println("Cleaned the votes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
