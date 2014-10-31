package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

public class GlitchRequest extends SQLRequest {

	private Player player;

	private String message;

	public GlitchRequest(String Player, String message) {
		super(SQLRequest.LOW_PRIORITY_REQUEST);
	}

	@Override
	public void process(SQLConnection sql) throws SQLException {
		String query = "INSERT INTO glitchers(name,message) VALUES ('" + player.getName().toLowerCase() + "','" + message + "')";
		sql.query(query);
	}

}
