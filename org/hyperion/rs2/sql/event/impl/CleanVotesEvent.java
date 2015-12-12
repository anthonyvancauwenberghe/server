package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

/**
 * Created by Gilles on 19/09/2015.
 */
public class CleanVotesEvent extends SQLRequest {

    public CleanVotesEvent() {
        super(SQLRequest.VOTE_REQUEST);
    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        if(!sql.isConnected()){
            return;
        }

        try{
            sql.query("DELETE FROM waitingVotes WHERE `timestamp` < DATE_SUB(NOW(), INTERVAL 14 DAY)");
            sql.query("DELETE FROM waitingVotes WHERE runelocus = 0 AND rspslist = 0 AND topg = 0");
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

}
