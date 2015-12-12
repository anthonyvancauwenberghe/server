package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gilles on 12/10/2015.
 */
public class LoadPoll extends SQLRequest {

    private final int poll;

    public LoadPoll(final int poll) {
        super(QUERY_REQUEST);
        this.poll = poll;
    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        if(!sql.isConnected()){
            System.out.println("Ingame polls are not loaded.");
            return;
        }

        ResultSet rs = null;
        final String query = "SELECT * FROM `polls` WHERE `index` = " + poll;
        try{
            rs = sql.query(query);
            while(rs.next()){
                new Poll(rs.getInt("index"), rs.getString("question"), rs.getString("explanation"), rs.getTimestamp("endDate"), rs.getBoolean("canChange"));
            }
        }catch(final Exception ex){
            ex.printStackTrace();
        }finally{
            if(rs != null){
                try{
                    rs.close();
                }catch(final SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
