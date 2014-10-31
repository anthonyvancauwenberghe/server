package org.hyperion.rs2.sql.event.impl;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.util.MmorpgToplistLogger;
import org.hyperion.util.Time;

import java.sql.SQLException;

public class MmorpgLoggerEvent extends SQLEvent {

	public static final long DELAY = Time.ONE_HOUR;

	public MmorpgLoggerEvent() {
		super(DELAY);
	}

	@Override
	public void execute(SQLConnection sql) throws SQLException {
		try {
			MmorpgToplistLogger.getLogger().refresh();
		} catch(Exception e) {
			e.printStackTrace();
		}
		sql.query(MmorpgToplistLogger.getLogger().constructQuery());
		super.updateStartTime();
	}

}
