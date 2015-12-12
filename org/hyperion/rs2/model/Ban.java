package org.hyperion.rs2.model;

import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.text.DecimalFormat;

public class Ban {

    public static final DecimalFormat HOURS = new DecimalFormat("##0.##");
    private String name;
    private String ip;
    private boolean byIp;
    private long expiration_time;
    private String reason = null;
    private int type;

    public Ban(final String line) {
        final String[] parts = line.split(",");
        setName(parts[0]);
        setIp(parts[1]);
        final boolean byIp = Boolean.parseBoolean(parts[2]);
        setByIp(byIp);
        final long expiration_time = Long.parseLong(parts[3]);
        setExpirationTime(expiration_time);
        final int type = Integer.parseInt(parts[4]);
        setType(type);
    }

    /**
     * @param name
     * @param ip
     * @param byIp
     * @param expiration_time The time in milliseconds after which the ban is no longer applied.
     * @param type
     */
    public Ban(final String name, final String ip, final boolean byIp, final long expiration_time, final int type) {
        this(name, ip, byIp, expiration_time, type, null);
    }

    public Ban(final String name, final String ip, final boolean byIp, final long expiration_time, final int type, final String reason) {
        this.setName(name);
        this.setIp(ip);
        this.setByIp(byIp);
        this.setExpirationTime(expiration_time);
        this.setType(type);
        this.reason = reason;
    }

    /**
     * @return The expiration time in milliseconds.
     */
    public long getExpirationTime() {
        return expiration_time;
    }

    /**
     * @param duration the duration to set
     */
    public void setExpirationTime(final long duration) {
        this.expiration_time = duration;
    }

    /**
     * @return the byIp
     */
    public boolean isByIp() {
        return byIp;
    }

    /**
     * @param byIp the byIp to set
     */
    public void setByIp(final boolean byIp) {
        this.byIp = byIp;
    }

    public String getReason() {
        if(reason == null)
            return "Undefined";
        return reason;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final int type) {
        //System.out.println("Setting ban type: " + type);
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(final String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "[@blu@" + TextUtils.titleCase(name) + "@bla@] | [IP:" + (ip == null ? "null" : ip.substring(0, ip.lastIndexOf('.'))) + "] | [By Ip?:" + byIp + "] | [Type:" + type + "] | [Left Time:" + HOURS.format(getHours()) + "Hours]";
    }

    public long getHours() {
        final long time = expiration_time - System.currentTimeMillis();
        return time / Time.ONE_HOUR;
    }

    @Override
    public boolean equals(final Object other) {
        if(!(other instanceof Ban))
            return false;
        final Ban otherban = (Ban) other;
        if(!name.equals(otherban.getName()))
            return false;
        if(!ip.equals(otherban.getIp()))
            return false;
        if(type != otherban.getType())
            return false;
        if(byIp != otherban.isByIp())
            return false;
        if(expiration_time != otherban.getExpirationTime())
            return false;
        return true;
    }

}
