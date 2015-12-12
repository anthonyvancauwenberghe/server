package org.hyperion.rs2.sql;

public class StrLongObject {

    private String str;

    private long longValue;

    public StrLongObject(final String str, final long integer) {
        this.setStr(str);
        this.setLongValue(integer);
    }

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(final long longValue) {
        this.longValue = longValue;
    }

}
