package org.hyperion.rs2.sqlv2.db;

import org.hyperion.Configuration;
import org.hyperion.rs2.sqlv2.impl.punishments.Punishments;

import static org.hyperion.Configuration.ConfigurationObject.*;

/**
 * Created by Gilles on 3/02/2016.
 */
public class GameDb extends Db {

    private Punishments punishment;

    public Punishments getPunishment() {
        return punishment;
    }

    @Override
    public boolean isEnabled() {
        return Configuration.getBoolean(GAME_DB_ENABLED);
    }

    @Override
    public String getUrl() {
        return Configuration.getString(GAME_DB_URL);
    }

    @Override
    public String getUsername() {
        return Configuration.getString(GAME_DB_USER);
    }

    @Override
    public String getPassword() {
        return Configuration.getString(GAME_DB_PASSWORD);
    }

    @Override
    protected void postInit() {
        punishment = new Punishments(this);
    }
}
