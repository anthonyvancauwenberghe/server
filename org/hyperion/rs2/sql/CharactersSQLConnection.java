package org.hyperion.rs2.sql;

import org.hyperion.Configuration;

public class CharactersSQLConnection extends MySQLConnection{

    public CharactersSQLConnection(Configuration config) {
        super("charactersSQL", config.getString("charsurl"), config.getString("charsuser"), config.getString("charspass"), 30000,
                10000, 100);
    }

    public boolean init(){
        establishConnection();
        start();
        return true;
    }
}
