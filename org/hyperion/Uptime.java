package org.hyperion;

public class Uptime {

    public static final long SERVER_STARTUP = System.currentTimeMillis();

    public long millisUptime() {
        return System.currentTimeMillis() - SERVER_STARTUP;
    }

    public int minutesUptime() {
        final long ms = System.currentTimeMillis() - SERVER_STARTUP;
        final long seconds = ms / 1000;
        return (int) (seconds / 60);
    }

    /**
     * @return The Uptime as a String.
     */
    public String toString() {
        int minutes = minutesUptime();
        final int hours = minutes / 60;
        if(hours <= 0){
            return (minutes + " Mins");
        }
        minutes = minutes % 60;
        return (hours + " Hours, " + minutes + " Mins");
    }
}
