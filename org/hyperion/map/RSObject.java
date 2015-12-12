package org.hyperion.map;

public class RSObject {
    private final int id;
    private final int face;
    private final int type;

    public RSObject(final int objectId, final int x, final int y, final int height, final int type, final int direction) {
        id = objectId;
        face = direction;
        this.type = type;
    }

    public int id() {
        return id;
    }

    public int direction() {
        return face;
    }

    public int type() {
        return type;
    }
}
