package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;

import java.sql.SQLException;

public class KeepConnectionAliveEvent extends SQLEvent {

	public static final long DELAY = 20000;

	public KeepConnectionAliveEvent() {
		super(DELAY);
	}

	@Override
	public void execute(SQLConnection sql) throws SQLException {
        System.out.println("KEEPIN CONNECTION ALIVE");
		sql.query("SELECT * FROM donator WHERE row = 1");
		super.updateStartTime();
	}


}
