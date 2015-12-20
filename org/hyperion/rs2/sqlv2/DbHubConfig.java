package org.hyperion.rs2.sqlv2;

import org.hyperion.rs2.sqlv2.db.DbConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbHubConfig {

    private final DbConfig donationsConfig;

    public DbHubConfig(final DbConfig donationsConfig) {
        this.donationsConfig = donationsConfig;
    }

    public DbConfig donationsConfig() {
        return donationsConfig;
    }

    public static DbHubConfig parse(final File file) throws IOException {
        try(final InputStream in = new FileInputStream(file)){
            final Properties props = new Properties();
            props.load(in);
            return new DbHubConfig(DbConfig.parse(props, "donations"));
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static DbHubConfig parseDefault() throws IOException {
        return parse(new File("./data/db-hub.properties"));
    }
}
