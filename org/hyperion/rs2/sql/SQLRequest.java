package org.hyperion.rs2.sql;

import org.hyperion.rs2.model.Player;

import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SQLRequest implements Comparable<SQLRequest> {

	/**
	 * The type of request, depending on this type the request is handled
	 * in a different way.
	 */
	public static final int
			DONATION_REQUEST = 0,
			VOTE_REQUEST = 1,
			MAIL_REQUEST = 2,
			QUERY_REQUEST = 3,
			TRADE_REQUEST = 4,
			LOW_PRIORITY_REQUEST = 10;

	/**
	 * The type of request.
	 */
	private int type;

	/**
	 * The player for which the request must be processed.
	 */
	protected Player player = null;

	/**
	 * Creates a new request with the specified type.
	 *
	 * @param type the type of request.
	 */
	public SQLRequest(int type) {
		this.type = type;
	}

	/**
	 * Processes the SQLRequest.
	 *
	 * @param sql
	 * @throws SQLException
	 */
	public abstract void process(SQLConnection sql) throws SQLException;

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Compares requests in order to give more important requests
	 * a higher priority.
	 */
	@Override
	public int compareTo(SQLRequest request) {
		if(type < request.getType())
			return - 1;
		if(type > request.getType())
			return 1;
		return 0;
	}

}
