package org.hyperion.rs2.sql;

import java.sql.*;

/**
 * @author Arsen Maxytuov.
 */
public class DatabaseDumper {

	//266 osbot x
	/**
	 * SQL connection login data.
	 */


	private static final String URL = "jdbc:mysql://xxx";
	private static final String USERNAME = "xxx";
	private static final String PASSWORD = "xxx";

	/**
	 * The connection.
	 */
	private Connection connection = null;

	/**
	 * The statement
	 */
	private Statement statement;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DatabaseDumper tester = new DatabaseDumper();
			tester.doStuff();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void doStuff() throws Exception {
		createConnection();
		ResultSet rs = query("SELECT * FROM smf_log_karma WHERE 1");
		if(rs == null)
			return;
		if(rs.next()) {
			System.out.println(rs.getInt("id_target"));
		}
		destroyConnection();
	}


	/**
	 * Creates a new SQL connection.
	 *
	 * @return
	 */
	private boolean createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			statement = connection.createStatement();
			statement.setQueryTimeout(1); // 1 second timeout
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Processes the query string.
	 *
	 * @param s
	 * @return
	 * @throws SQLException
	 */
	public ResultSet query(String s) throws SQLException {
		try {
			statement = connection.createStatement();
			if(s.toLowerCase().startsWith("select")) {
				ResultSet rs = statement.executeQuery(s);
				return rs;
			} else {
				statement.executeUpdate(s);
			}
			return null;
		} catch(Exception e) {
			e.printStackTrace();
			destroyConnection();
		}
		return null;
	}

	/**
	 * Destroys the SQL connection.
	 */
	public void destroyConnection() {
		try {
			statement.close();
			connection.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
