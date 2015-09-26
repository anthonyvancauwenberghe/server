package org.hyperion.rs2.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hyperion.rs2.sql.requests.QueryRequest;

public class SQLAccessor {

    private MySQLConnection sql;

    public SQLAccessor(MySQLConnection sql) {
        this.sql = sql;
    }

    public void offer(String query) {
        offer(new QueryRequest(query));
    }

    public void offer(String query, Object...arguments) {
        String str = String.format(query, arguments);
        offer(str);
    }

    public void offer(SQLRequest request) {
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

    public ResultSet select(String query) throws SQLException {
        if(query.toLowerCase().startsWith("select")) {
            return sql.query(query);
        }
        return null;
    }

    public ResultSet delete(String query) throws SQLException {
        if(query.toLowerCase().startsWith("delete")) {
            return sql.query(query);
        }
        return null;
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return sql.getConnection().prepareStatement(query);
    }
}
