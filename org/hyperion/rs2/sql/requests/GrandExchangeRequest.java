package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

import static org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface.*;

/**
 * Created by User on 24.10.2015.
 */
public class GrandExchangeRequest extends SQLRequest {

    private Player player;

    private int id;

    public GrandExchangeRequest(Player player, int id) {
        super(SQLRequest.LOW_PRIORITY_REQUEST);
        this.player = player;
        this.id = id;
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        player.getGrandExchangeTracker().handleInterfaceInteraction(id);
    }

}
