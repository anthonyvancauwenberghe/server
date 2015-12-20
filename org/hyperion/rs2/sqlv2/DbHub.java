package org.hyperion.rs2.sqlv2;

import org.hyperion.rs2.sqlv2.db.DonationsDb;

import java.io.IOException;

public class DbHub {

    private static DbHubConfig config;

    private static DonationsDb donationsDb;

    public static DbHubConfig config() {
        return config;
    }

    public static DonationsDb donationsDb() {
        return donationsDb;
    }

    public static void init(final DbHubConfig config) {
        DbHub.config = config;

        donationsDb = new DonationsDb(config.donationsConfig());
        donationsDb.init();
    }

    public static void initDefault() throws IOException {
        init(DbHubConfig.parseDefault());
    }

    public static boolean initialized() {
        return config != null;
    }
}
