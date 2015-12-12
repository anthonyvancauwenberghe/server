package org.hyperion.rs2.sql;

import org.hyperion.rs2.sql.requests.QueryRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAccessor {

    private final MySQLConnection sql;

    public SQLAccessor(final MySQLConnection sql) {
        this.sql = sql;
    }

    public void offer(final String query) {
        offer(new QueryRequest(query));
    }

    public void offer(final String query, final Object... arguments) {
        final String str = String.format(query, arguments);
        offer(str);
    }

    public void offer(final SQLRequest request) {
        sql.offer(request);
    }

    public int getQueueSize() {
        return sql.getQueueSize();
    }

    public long getLastQuery() {
        return sql.getLastQuery();
    }

    public String getLastQueryString() {
        return sql.getLastQueryString();
    }

    public boolean isRunning() {
        return sql.isRunning();
    }

    public ResultSet select(final String query) throws SQLException {
        if(query.toLowerCase().startsWith("select")){
            return sql.query(query);
        }
        return null;
    }

    public ResultSet delete(final String query) throws SQLException {
        if(query.toLowerCase().startsWith("delete")){
            return sql.query(query);
        }
        return null;
    }

    public PreparedStatement prepareStatement(final String query) throws SQLException {
        return sql.getConnection().prepareStatement(query);
    }
}
