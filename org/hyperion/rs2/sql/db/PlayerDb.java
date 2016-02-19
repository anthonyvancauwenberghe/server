package org.hyperion.rs2.sql.db;

import org.hyperion.Configuration;
import org.hyperion.rs2.sql.impl.achievement.Achievement;
import org.hyperion.rs2.sql.impl.grandexchange.GrandExchange;

import static org.hyperion.Configuration.ConfigurationObject.*;

/**
 * Created by Gilles on 3/02/2016.
 */
public class PlayerDb extends Db {

    private Achievement achievements;
    private GrandExchange grandExchange;

    public Achievement getAchievements() {
        return achievements;
    }

    public GrandExchange getGrandExchange() {
        return grandExchange;
    }

    @Override
    protected boolean isEnabled() {
        return Configuration.getBoolean(PLAYER_DB_ENABLED);
    }

    @Override
    protected String getUrl() {
        return Configuration.getString(PLAYER_DB_URL);
    }

    @Override
    protected String getUsername() {
        return Configuration.getString(PLAYER_DB_USER);
    }

    @Override
    protected String getPassword() {
        return Configuration.getString(PLAYER_DB_PASSWORD);
    }

    @Override
    protected void postInit() {
        achievements = new Achievement(this);
        grandExchange = new GrandExchange(this);
    }
}
