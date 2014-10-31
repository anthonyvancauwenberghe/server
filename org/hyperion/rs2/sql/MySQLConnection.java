package org.hyperion.rs2.sql;

import java.sql.DriverManager;

public abstract class MySQLConnection extends SQLConnection {

	/**
	 * The database url.
	 */
	private String url;

	/**
	 * The database username.
	 */
	private String username;

	/**
	 * The database password.
	 */
	private String password;


	public MySQLConnection(String name, String url, String username,
	                       String password, int create_connection_timer,
	                       int max_cycle_sleep, int min_cycle_sleep) {
		super(name, create_connection_timer, max_cycle_sleep, min_cycle_sleep);
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public abstract boolean init();

	public boolean createConnection() {
		if(System.currentTimeMillis() - lastConnectionCreated < create_connection_timer)
			return false;
		try {
			lastConnectionCreated = System.currentTimeMillis();
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connected! " + this.getName());
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
