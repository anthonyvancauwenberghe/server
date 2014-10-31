package org.hyperion.rs2.sql.event;

import org.hyperion.rs2.sql.SQLConnection;

public abstract class SQLStartupEvent {

	public abstract void run(SQLConnection sql);
}
