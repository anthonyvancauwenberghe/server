package org.hyperion.rs2.sqlv2.db;

import java.util.Properties;

public class DbConfig {

    public static boolean consoleDebug = true;
    public static boolean playerDebug = true;

    private final String url;
    private final String user;
    private final String pass;
    private final boolean enabled;

    public DbConfig(final String url, final String user, final String pass, final boolean enabled) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.enabled = enabled;
    }

    public String url() {
        return url;
    }

    public String user() {
        return user;
    }

    public String pass() {
        return pass;
    }

    public boolean enabled() {
        return enabled;
    }

    public static DbConfig parse(final Properties props, final String prefix) {
        return new DbConfig(props.getProperty(prefix + "-url"), props.getProperty(prefix + "-user"), props.getProperty(prefix + "-pass"), Boolean.parseBoolean(props.getProperty(prefix + "-enabled")));
    }
}
