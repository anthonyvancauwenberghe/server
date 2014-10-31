package org.hyperion.rs2.sql;

import java.sql.DriverManager;

public abstract class SQLiteConnection extends SQLConnection {

	private String fileName;

	public SQLiteConnection(String name, String fileName, int create_connection_timer,
	                        int max_cycle_sleep, int min_cycle_sleep) {
		super(name, create_connection_timer, max_cycle_sleep, min_cycle_sleep);
		this.fileName = fileName;
	}

	public abstract boolean startupQueries();

	@Override
	public boolean init() {
		createConnection();
		startupQueries();
		start();
		return true;
	}

	@Override
	protected boolean createConnection() {
		if(System.currentTimeMillis() - lastConnectionCreated < create_connection_timer)
			return false;
		try {
			lastConnectionCreated = System.currentTimeMillis();
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
