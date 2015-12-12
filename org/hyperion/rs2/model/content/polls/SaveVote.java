package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

/**
 * Created by Gilles on 10/10/2015.
 */
public class SaveVote extends SQLRequest {

    private final String player;
    private final int pollIndex;
    private final boolean vote;

    public SaveVote(final String player, final int pollIndex, final boolean vote) {
        super(QUERY_REQUEST);
        this.player = player;
        this.pollIndex = pollIndex;
        this.vote = vote;
    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        if(!sql.isConnected()){
            return;
        }
        final String query = String.format("INSERT INTO `server`.`pollvotes` (`playerName`, `poll`, `answer`) VALUES ('%s', '%d', '%d')" + "ON DUPLICATE KEY UPDATE `answer` = '%d'", player, pollIndex, vote ? 1 : 0, vote ? 1 : 0);
        try{
            sql.offer(query);
        }catch(final Exception e){
            e.printStackTrace();
        }
    }
}
