package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.util.AccountValue;

import java.sql.SQLException;
import java.util.LinkedList;

public class TradeRequest extends SQLRequest {

	private String tradeQuery;

	private LinkedList<String> itemQueries = new LinkedList<String>();

	private static int tradeId = - 1;

	public TradeRequest(String name1, String name2, Container container1, Container container2) {
		super(SQLRequest.TRADE_REQUEST);
		tradeId++;
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO trades (name1, name2, id, gave, received) VALUES (");
		sb.append("'" + name1 + "',");
		sb.append("'" + name2 + "',");
		sb.append(tradeId + ",");
		sb.append(AccountValue.getContainerValue(container1) + ",");
		sb.append(AccountValue.getContainerValue(container2) + ",");

	}

	@Override
	public void process(SQLConnection sql) throws SQLException {
	    /*sql.offer(tradeQuery);
		for(String query: itemQueries) {
			sql.offer(query);
		}*/
	}

}
