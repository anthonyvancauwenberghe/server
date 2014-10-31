package org.hyperion.rs2.sql.event;

import org.hyperion.rs2.sql.SQLConnection;

import java.sql.SQLException;

public abstract class SQLEvent {

	private long delay;

	private long starttime;

	private boolean running = true;

	public SQLEvent(long delay) {
		this.delay = delay;
		updateStartTime();
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
	}

	public void updateStartTime() {
		starttime = System.currentTimeMillis() + delay;
	}

	public boolean shouldExecute() {
		return System.currentTimeMillis() > starttime;
	}


	public abstract void execute(SQLConnection sql) throws SQLException;


}
