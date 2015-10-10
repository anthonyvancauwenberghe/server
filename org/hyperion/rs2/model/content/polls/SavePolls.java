package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

/**
 * Created by Gilles on 10/10/2015.
 */
public class SavePolls extends SQLRequest {

    public SavePolls() {
        super(QUERY_REQUEST);
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        if (!sql.isConnected()) {
            return;
        }
        try {
            for (int i = 0; i < Poll.getPolls().size(); i++) {
                Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[i]);
                if (poll == null)
                    continue;

                String query = String.format(
                        "INSERT INTO `server`.`polls` (`index`, `question`, `explanation`, `canChange`, `active`) VALUES (%d, '%s', '%s', '%d', '1')" +
                                " ON DUPLICATE KEY UPDATE `question` = '%s', `explanation` = '%s', `canChange` = '%d'",
                        poll.getIndex(), poll.getQuestion(), poll.getDescription(), poll.canChange() ? 1 : 0,
                        poll.getQuestion(), poll.getDescription(), poll.canChange() ? 1 : 0);
                sql.query(query);

                for (String vote : poll.getYesVotes()) {
                    query = String.format(
                            "INSERT INTO `server`.`pollvotes` (`playerName`, `poll`, `answer`) VALUES ('%s', '%d', '1')" +
                                    "ON DUPLICATE KEY UPDATE answer = '1'",
                            vote, poll.getIndex()
                    );
                    sql.query(query);
                }
                for (String vote : poll.getNoVotes()) {
                    query = String.format(
                            "INSERT INTO `server`.`pollvotes` (`playerName`, `poll`, `answer`) VALUES ('%s', '%d', '0')" +
                                    "ON DUPLICATE KEY UPDATE answer = '0'",
                            vote, poll.getIndex()
                    );
                    sql.query(query);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Poll data successfully saved.");
    }
}
