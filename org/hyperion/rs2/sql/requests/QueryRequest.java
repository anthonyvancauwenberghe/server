package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;

import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */
public class QueryRequest extends SQLRequest {

	/**
	 * The query to be processed.
	 */
	private String query;

	/**
	 * Constructs a new QueryRequest with the specified query.
	 *
	 * @param query
	 */
	public QueryRequest(String query) {
		super(SQLRequest.QUERY_REQUEST);
		this.query = query;
	}

	/**
	 * Processes the query.
	 */
	@Override
	public void process(SQLConnection sql) throws SQLException {
		sql.query(query);
	}

	@Override
	public String toString() {
		return query;
	}
}
