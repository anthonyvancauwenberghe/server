package org.hyperion.rs2.sql;

import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.util.Time;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SQLConnection extends Thread {

	protected String lastQueryString = null;

	public String getLastQueryString() {
		return lastQueryString;
	}

	/**
	 * Queue used to transfer requests from game threads to SQL thread.
	 */
	protected ConcurrentLinkedQueue<SQLRequest> queue = new ConcurrentLinkedQueue<SQLRequest>();

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
	 * Holds the time when the last query was sent.
	 */
	protected long lastQuery = 0;

	public long getLastQuery() {
		return lastQuery;
	}

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
	protected int sleep_time;

	public Connection getConnection() {
		return connection;
	}

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

	public SQLRequest peek() {
		return queue.peek();
	}

	public int getQueueSize() {
		return queue.size();
	}

	private boolean logged = false;

	public void setLogged(boolean b) {
		logged = b;
	}


	private LinkedList<StrLongObject> lastQueries = new LinkedList<StrLongObject>();

	public void dumpLastQueries(String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for(StrLongObject lastQuery: lastQueries) {
				out.write(new Date(lastQuery.getLongValue()).toString() + "---" + lastQuery.getStr());
				out.newLine();
			}
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void dumpLastQueries() {
		dumpLastQueries("./data/dumplastqueries.log");
	}



	/**
	 * The run method of the thread which
	 * processes all queries and events.
	 */
	@Override
	public void run() {
		try {
			createConnection();
		} catch(SQLException e) {
			System.out.println("Could not create connection..." + this.getName());
		}
		while (running) {

			try {
				long very_start = System.currentTimeMillis();
				while (connection == null || connection.isClosed()) {
					sleep(create_connection_timer);
					createConnection();
				}
				int queue_size = queue.size();
				if(logged) {
					World.getWorld().getGUI().setStatus("About to check queue size");
					World.getWorld().getGUI().updateQueueSizes();
				}

				if(queue_size > 0) {
					if(logged) {
						World.getWorld().getGUI().setStatus("Finding non null E");
					}
					SQLRequest request = null;
					int attempts = 0;
					//request = queue.poll();
					while(queue.size() > 0 && request == null && attempts < 50) {
						/*request = queue.get(0);
						queue.remove(0);*/
						request = queue.poll();
						attempts++;
					}
					if(logged) {
						World.getWorld().getGUI().setStatus("About to request != null");
					}
					if (request != null) {
						try {
							if(logged) {
								World.getWorld().getGUI().setStatus("About to process request");
								World.getWorld().getGUI().setLastQuery(request.toString());
								long start = System.currentTimeMillis();
								World.getWorld().getGUI().setStart(start);
								this.lastQueryString = request.toString();
								request.process(this);


								long delta = System.currentTimeMillis() - start;
								World.getWorld().getGUI().setDelta(delta);

							} else {
								request.process(this);
							}
						} catch (Exception e) {
							if(logged) {
								World.getWorld().getGUI().setStatus("Exp caught :/");
							}
							handleException(e, request.toString());
							sleep(Time.ONE_MINUTE);
						}
					} else {
						if(logged) {
							World.getWorld().getGUI().setStatus("Request was null");
						}

					}
					sleep_time = Math.max(min_cycle_sleep, sleep_time / 2);
				} else {
					if(logged) {
						World.getWorld().getGUI().setStatus("Sleep time..");
					}

					sleep_time = Math.min(max_cycle_sleep, sleep_time * 2 + 1);
					if (sleep_time > 0)
						sleep(sleep_time);
				}

				if(logged) {
					World.getWorld().getGUI().setStatus("Wanna process Events");
				}

				processEvents();

				if(logged) {
					World.getWorld().getGUI().setStatus("Finished processing events");
				}

				if (System.currentTimeMillis() - lastQuery + sleep_time > 30000) {
					query("SELECT 1");
				}
			} catch (Exception e) {
				try {
					sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}

		}
	}
	
	/*public static void writeProcessLog(String query) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("./data/sqlprocesslog.log", true));
			bw.write(query);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/**
	 * Initiates the SQL thread, returns true if succesful, false if not.
	 *
	 * @return
	 */
	public abstract boolean init();

	/**
	 * Processes all of the SQLEvents.
	 *
	 * @throws java.sql.SQLException
	 */
	private void processEvents() throws SQLException {
		for (int i = 0; i < events.size(); i++) {
			SQLEvent event = events.get(i);
			if (!event.isRunning()) {
				events.remove(i);
				return;
			} else if (event.shouldExecute()) {
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
		for (SQLEvent event : events)
			this.events.add(event);
	}

	/**
	 * Offers a new query request.
	 *
	 * @param query
	 */
	public void offer(String query) {
		offer(new QueryRequest(query));
	}

	public void offer(String query, Object...args) {
		query = String.format(query, args);
		offer(query);
	}


	private LinkedList<String> overloadQueries = new LinkedList<String>();

	/**
	 * Offers the SQLRequest to the SQL thread.
	 *
	 * @param request
	 */
	public void offer(SQLRequest request) {
		long now = System.currentTimeMillis();
		StrLongObject o = new StrLongObject(request.toString(),now);
		lastQueries.add(o);
		if(lastQueries.size() > 100) {
			long first = lastQueries.get(0).getLongValue();
			if(now - first < Time.FIVE_SECONDS) {
				dumpLastQueries();
			}
			lastQueries.remove(0);
		}
		int queueSize = queue.size();

		if(queueSize > 1000) {
			//System.out.println("QueueSize: " + queueSize + " --- " + request);
			overloadQueries.add("QueueSize: " + queueSize + " --- " + request.toString());
			if(overloadQueries.size() > 1000) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("./data/overloadqueries.log",true));
					for(String s: overloadQueries) {
						bw.write(s);
						bw.newLine();
					}
					bw.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				overloadQueries.clear();
			}
		}
		if (running) {
			queue.offer(request);
		} else {
			System.out.println("Processing: " + request.toString());
			try {
				request.process(this);
			} catch (SQLException e) {
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
		for (StackTraceElement st : e.getStackTrace()) {
			logMessage += st.toString() + "\\n";
		}
		writeLog(logMessage);
		if(this instanceof MySQLConnection) {
			MySQLConnection mc = (MySQLConnection) this;
			mc.dumpLastQueries();
		}
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Changes the debugging mode.
	 */
	public void changeDebug() {
		debug = !debug;
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
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Creates a new SQL connection.
	 *
	 * @return
	 */
	protected abstract boolean createConnection() throws SQLException;

	/**
	 * A blocking method which keeps trying to make a connection until one is made.
	 */
	public void establishConnection() {
		try {
			createConnection();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		while (!isConnected()) {
			try {
				sleep(create_connection_timer);
				createConnection();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch(SQLException e) {
				System.out.println("Not able to connect to sql" + this.getName());
			}
			System.out.println("Was not able to connect to " + this.getName());
		}
	}

	/**
	 * Processes the formatted query string.
	 * @param query
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public ResultSet query(String query, Object...args) throws SQLException {
		query = String.format(query, args);
		return query(query);
	}
	/**
	 * Processes the query string.
	 *
	 * @param s
	 * @return
	 * @throws java.sql.SQLException
	 */
	public abstract ResultSet query(String s) throws SQLException;

	/**
	 * Destroys the SQL connection.
	 */
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
