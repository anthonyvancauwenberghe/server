package org.hyperion.rs2.sqlv2;

import org.hyperion.rs2.sqlv2.db.DonationsDb;
import org.hyperion.rs2.sqlv2.db.GameDb;
import org.hyperion.rs2.sqlv2.db.PlayerDb;

import java.io.IOException;

public class DbHub {

    private static DbHubConfig config;
    private static DonationsDb donationsDb;
    private static PlayerDb playerDb;
    private static GameDb gameDb;

    public static DbHubConfig getConfig() {
        return config;
    }

    public static DonationsDb getDonationsDb() {
        return donationsDb;
    }

    public static PlayerDb getPlayerDb() {
        return playerDb;
    }

    public static GameDb getGameDb() {
        return gameDb;
    }

    public static void init(final DbHubConfig config) {
        DbHub.config = config;

        donationsDb = new DonationsDb(config.getDonationsConfig());
        donationsDb.init();

        playerDb = new PlayerDb(config.getPlayerConfig());
        playerDb.init();

        gameDb = new GameDb(config.getGameConfig());
        gameDb.init();
    }

    public static void initDefault() throws IOException {
        init(DbHubConfig.parseDefault());
    }

    public static boolean initialized() {
        return config != null;
    }
}
