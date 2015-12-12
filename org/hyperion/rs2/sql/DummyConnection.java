package org.hyperion.rs2.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DummyConnection extends MySQLConnection {

    public DummyConnection() {
        super("DummyConnection", null, null, null, 0, 0, 0);
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean createConnection() {
        return true;
    }

    @Override
    public ResultSet query(final String s) throws SQLException {
        System.out.println("Direct query: " + s);
        return null;
    }

    @Override
    public void offer(final String query) {
        System.out.println("Query:" + query);
    }

    /**
     * Offers the SQLRequest to the SQL thread.
     *
     * @param request
     */
    public void offer(final SQLRequest request) {
        System.out.println("Query:" + request.toString());
    }
}
