package org.hyperion.rs2.sqlv2.db;

import org.hyperion.rs2.sqlv2.impl.achievement.Achievement;
import org.hyperion.rs2.sqlv2.impl.grandexchange.GrandExchange;

/**
 * Created by Gilles on 3/02/2016.
 */
public class PlayerDb extends Db {

    private Achievement achievements;
    private GrandExchange grandExchange;

    public PlayerDb(DbConfig config) {
        super(config);
    }

    public Achievement getAchievements() {
        return achievements;
    }

    public GrandExchange getGrandExchange() {
        return grandExchange;
    }

    @Override
    protected void postInit() {
        achievements = new Achievement(this);
        grandExchange = new GrandExchange(this);
    }
}
