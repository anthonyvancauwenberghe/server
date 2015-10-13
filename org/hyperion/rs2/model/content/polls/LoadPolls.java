package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gilles on 10/10/2015.
 */
public class LoadPolls extends SQLRequest {

    public LoadPolls() {
        super(QUERY_REQUEST);
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        if (!sql.isConnected()) {
            System.out.println("Ingame polls are not loaded.");
            return;
        }

        ResultSet rs = null;
        String query = "SELECT * FROM `polls` WHERE `active` = 1";
        try {
            rs = sql.query(query);
            Poll.getPolls().clear();
            while (rs.next()) {
                new Poll(rs.getInt("index"), rs.getString("question"), rs.getString("explanation"), rs.getTimestamp("endDate"), rs.getBoolean("canChange"), true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
