package org.hyperion.rs2.sql;

import java.sql.PreparedStatement;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.rs2.util.RestarterThread;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SQLConnection extends Thread {

	/**
	 * Queue used to transfer requests from game threads to SQL thread.
	 */
	private Queue<SQLRequest> queue = new LinkedList<SQLRequest>();

	/**
	 * List used to hold the events in it.
	 */
	private List<SQLEvent> events = new ArrayList<SQLEvent>();

	/**
	 * The connection.
	 */
	protected Connection connection = null;

	/**
	 * The debugging flag, true if debugging mode is enabled, otherwise false.
	 */
	private boolean debug = false;

	/**
	 * The running flag.
	 */
	private boolean running = true;

	/**
	 * The timer stopping the thread from continuously attempting to make new connections.
	 */
	protected int create_connection_timer;

	/**
	 * Holds the time when the last attempt to create a new connection happened.
	 */
	protected long lastConnectionCreated = 0;

	/**
	 * The maximum cycle sleep time.
	 */
	private int max_cycle_sleep;

	/**
	 * The minimum cycle sleep time.
	 */
	private int min_cycle_sleep;

	/**
	 * The current cycle sleep time.
	 */
	private int sleep_time;


	/**
	 * @param name
	 * @param create_connection_timer
	 * @param max_cycle_sleep
	 * @param min_cycle_sleep
	 */
	public SQLConnection(String name, int create_connection_timer, int max_cycle_sleep, int min_cycle_sleep) {
		this.setName(name);
		this.create_connection_timer = create_connection_timer;
		this.max_cycle_sleep = max_cycle_sleep;
		this.min_cycle_sleep = min_cycle_sleep;
		this.sleep_time = max_cycle_sleep;
	}

	/**
	 * The run method of the thread which
	 * processes all queries and events.
	 */
	@Override
	public void run() {
		createConnection();
		while(running) {
			try {
				while(connection == null || connection.isClosed()) {
					sleep(create_connection_timer);
					createConnection();
				}
				SQLRequest request = queue.poll();
				if(request != null) {
					try {
						request.process(this);
					} catch(Exception e) {
						handleException(e, request.toString());
					}
				}
				processEvents();
				if(queue.size() > 0) {
					sleep_time = Math.max(min_cycle_sleep, sleep_time / 2);
				} else {
					sleep_time = Math.min(max_cycle_sleep, sleep_time * 2);
				}
				if(sleep_time > 0)
					sleep(sleep_time);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initiates the SQL thread, returns true if succesful, false if not.
	 *
	 * @return
	 */
	public abstract boolean init();

	/**
	 * Processes all of the SQLEvents.
	 *
	 * @throws SQLException
	 */
	private void processEvents() throws SQLException {
		for(int i = 0; i < events.size(); i++) {
			SQLEvent event = events.get(i);
			if(! event.isRunning()) {
				events.remove(i);
				i--;
			} else if(event.shouldExecute()) {
				event.execute(this);
			}
		}
	}

	/**
	 * Submits SQLEvents to the events list.
	 *
	 * @param events
	 */
	public void submit(SQLEvent... events) {
		for(SQLEvent event : events)
			this.events.add(event);
	}

	/**
	 * Offers a new query request.
	 *
	 * @param query
	 */
	public void offer(String query) {
		queue.offer(new QueryRequest(query));
	}

	/**
	 * Offers the SQLRequest to the SQL thread.
	 *
	 * @param request
	 */
	public void offer(SQLRequest request) {
		if(running)
			queue.offer(request);
		else {
			System.out.println("Processing: " + request.toString());
			try {
				request.process(this);
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the specified Exception.
	 *
	 * @param e
	 */
	private void handleException(Exception e, String query) {
		System.out.println("Error in sql: " + e.getMessage());
		String logMessage = "Epic Error: \\n  ---" + query + "---";
		for(StackTraceElement st : e.getStackTrace()) {
			logMessage += st.toString() + "\\n";
		}
		writeLog(logMessage);
	}

	/**
	 * Writes the specified line in the log file of the SQL thread.
	 */
	public void writeLog(String message) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"./data/sqllog.log", true));
			bw.newLine();
			bw.write(message);
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Changes the debugging mode.
	 */
	public void changeDebug() {
		debug = ! debug;
	}

	/**
	 * Stops the SQL thread.
	 */
	public void stopRunning() {
		System.out.println("Stopped running SQL!");
		running = false;
		interrupt();
	}

	/**
	 * Checks whether SQL is running.
	 *
	 * @return true if running, otherwise false
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Checks if the SQL is connected.
	 *
	 * @return true is connected, otherwise false
	 */
	public boolean isConnected() {
		try {
			return connection != null && ! connection.isClosed();
		} catch(SQLException e) {
			return false;
		}
	}

	/**
	 * Creates a new SQL connection.
	 *
	 * @return
	 */
	protected abstract boolean createConnection();

	/**
	 * A blocking method which keeps trying to make a connection until one is made.
	 */
	public void establishConnection() {
		createConnection();
		while(! isConnected()) {
			try {
				sleep(create_connection_timer);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			createConnection();
			System.out.println("Was not able to connect to " + this.getName());
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
		//System.out.println(this.getName() + ":" + s);
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = null;
			if(s.toLowerCase().startsWith("select")) {
				rs = statement.executeQuery(s);
			} else {
				statement.executeUpdate(s);

			}
			RestarterThread.getRestarter().updateSQLTimer();
			return rs;
		} catch(Exception e) {
			if(e instanceof SocketTimeoutException) {
				System.out.println("Can't process query: " + s);
				//e.printStackTrace();
			} else {
				System.out.println("Error:" + s);
				e.printStackTrace();

				handleException(e, s);
			}
			return null;
		}
	}

	/**
	 * Destroys the SQL connection.
	 */
	public void close() {
		if(connection != null) {
			try {
				connection.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

    public PreparedStatement prepare(final String sql){
        try{
            return connection.prepareStatement(sql);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
