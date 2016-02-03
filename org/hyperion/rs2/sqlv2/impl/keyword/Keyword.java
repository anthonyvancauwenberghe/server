package org.hyperion.rs2.sqlv2.impl.keyword;

public class Keyword {

    private final String name;
    private final int id;

    public Keyword(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public String name() {
        return name;
    }

    public int id() {
        return id;
    }
}
