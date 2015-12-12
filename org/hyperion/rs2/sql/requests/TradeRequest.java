package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.util.AccountValue;

import java.sql.SQLException;
import java.util.LinkedList;

public class TradeRequest extends SQLRequest {

    private static int tradeId = -1;
    private final LinkedList<String> itemQueries = new LinkedList<String>();
    private String tradeQuery;

    public TradeRequest(final String name1, final String name2, final Container container1, final Container container2) {
        super(SQLRequest.TRADE_REQUEST);
        tradeId++;
        final StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO trades (name1, name2, id, gave, received) VALUES (");
        sb.append("'").append(name1).append("',");
        sb.append("'").append(name2).append("',");
        sb.append(tradeId).append(",");
        sb.append(AccountValue.getContainerValue(container1)).append(",");
        sb.append(AccountValue.getContainerValue(container2)).append(",");

    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        /*sql.offer(tradeQuery);
        for(String query: itemQueries) {
			sql.offer(query);
		}*/
    }

}
