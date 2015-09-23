package org.hyperion.rs2.sql.requests;

import org.hyperion.Server;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.sql.event.impl.BetaServerEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gilles on 9/22/2015.
 */
public class BetaRequest extends SQLRequest {

    public BetaRequest() {
        super(SQLRequest.QUERY_REQUEST);
    }

    @Override
    public void process(SQLConnection sql) throws SQLException {
        if(Server.NAME.equalsIgnoreCase("ArteroBeta")) {
            //First query will get the whitelist
            ResultSet rs = sql.query("SELECT * FROM whitelist");
            if (rs != null) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    if(!name.isEmpty() && !BetaServerEvent.whitelist.contains(name))
                        BetaServerEvent.whitelist.add(name);
                }
                rs.close();
            }

            //Second query gets the active changelog
            rs = sql.query("SELECT * FROM updates WHERE active = 1");
            if (rs != null) {
                while (rs.next()) {
                    //Adds the changelog entries
                    String changelogEntries[] = rs.getString("changelog").split("#");
                    for(String entry : changelogEntries)
                        if(!BetaServerEvent.changes.contains(entry) && !entry.equalsIgnoreCase(""))
                            BetaServerEvent.changes.add(entry);
                    //Adds the toTest entries
                    String testEntries[] = rs.getString("toTest").split("#");
                    for(String test : testEntries)
                        if(!BetaServerEvent.toTest.contains(test) && !test.equalsIgnoreCase(""))
                            BetaServerEvent.toTest.add(test);
                    //Adds the test command entries
                    String testCommandEntries[] = rs.getString("testCommands").split("#");
                    for(String command : testCommandEntries)
                        if(!BetaServerEvent.testCommands.contains(command) && !command.equalsIgnoreCase("") )
                            BetaServerEvent.testCommands.add(command);
                }
                rs.close();
            }
        }
    }
}
