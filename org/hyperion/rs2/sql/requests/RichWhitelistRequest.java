package org.hyperion.rs2.sql.requests;

import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.sql.event.impl.RichWhitelistEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gilles on 25/10/2015.
 */
public class RichWhitelistRequest extends SQLRequest {

    public RichWhitelistRequest() {
        super(SQLRequest.QUERY_REQUEST);
    }

    @Override
    public void process(final SQLConnection sql) throws SQLException {
        if(!RichWhitelistEvent.enabled)
            return;
        final ResultSet rs = sql.query("SELECT name FROM accountvalues where value > 50000 or pkvalue > 500000");
        if(rs != null){
            while(rs.next()){
                final String name = rs.getString("name");
                if(!name.isEmpty() && !RichWhitelistEvent.whitelist.contains(name))
                    RichWhitelistEvent.whitelist.add(name);
            }
            rs.close();
        }
    }
}
