package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

/**
 * Created by User on 24.10.2015.
 */
public class GrandExchangeRequest extends SQLRequest {

    private final Player player;

    private final int id;

    public GrandExchangeRequest(final Player player, final int id) {
        super(SQLRequest.LOW_PRIORITY_REQUEST);
        this.player = player;
        this.id = id;
    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        player.getGrandExchangeTracker().handleInterfaceInteraction(id);
    }

}
