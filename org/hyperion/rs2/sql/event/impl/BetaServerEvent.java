package org.hyperion.rs2.sql.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.sql.event.SQLEvent;
import org.hyperion.util.Time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 20/09/2015.
 */
public class BetaServerEvent extends SQLEvent {

    public static final List<String> whitelist = new ArrayList();
    public static final List<String> changes = new ArrayList();
    public static final List<String> toTest = new ArrayList();
    public static final List<String> testCommands = new ArrayList();

    public BetaServerEvent(){
        super(Time.FIVE_MINUTES);
    }


    public void execute(SQLConnection sql) throws SQLException {
        if(Server.NAME.equalsIgnoreCase("ArteroBeta")) {
            //First query will get the whitelist
            ResultSet rs = sql.query("SELECT * FROM whitelist");
            if (rs != null) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    if(!name.isEmpty() && !whitelist.contains(name))
                        whitelist.add(name);
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
                        if(!changes.contains(entry) && !entry.equalsIgnoreCase(""))
                            changes.add(entry);
                    //Adds the toTest entries
                    String testEntries[] = rs.getString("toTest").split("#");
                    for(String test : testEntries)
                        if(!toTest.contains(test) && !test.equalsIgnoreCase(""))
                            toTest.add(test);
                    //Adds the test command entries
                    String testCommandEntries[] = rs.getString("testCommands").split("#");
                    for(String command : testCommandEntries)
                        if(!testCommands.contains(command) && !command.equalsIgnoreCase("") )
                            testCommands.add(command);
                }
                rs.close();
            }
        }
    }
}
