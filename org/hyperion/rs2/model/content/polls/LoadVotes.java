package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gilles on 10/10/2015.
 */
public class LoadVotes extends SQLRequest {

    private Poll poll;

    public LoadVotes(int pollIndex) {
        super(QUERY_REQUEST);
        this.poll = Poll.getPoll(pollIndex);
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        if (!sql.isConnected()) {
            return;
        }

        ResultSet rs = null;
        String query = "SELECT * FROM `pollvotes` WHERE `poll` = " + poll.getIndex();
        try {
            rs = sql.query(query);
            while (rs.next()) {
                if (rs.getBoolean("answer")) {
                    poll.addYesVote(rs.getString("playerName"));
                    continue;
                }
                poll.addNoVote(rs.getString("playerName"));
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
        System.out.println("Votes loaded for poll " + poll.getQuestion());
    }
}
