package org.hyperion.rs2.sql;

import org.hyperion.Configuration;
import org.hyperion.Server;

public class CharactersSQLConnection extends MySQLConnection{

    public CharactersSQLConnection(Configuration config) {
        super("charactersSQL", config.getString("charsurl"), config.getString("charsuser"), config.getString("charspass"), 30000,
                10000, 100);
    }

    public boolean init(){
        if (!Server.getConfig().getBoolean("sql"))
            return false;
        establishConnection();
        start();
        return true;
    }
}
