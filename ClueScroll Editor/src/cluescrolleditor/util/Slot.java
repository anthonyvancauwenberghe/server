package cluescrolleditor.util;

import java.util.HashMap;
import java.util.Map;

public enum Slot{
    HELMET(0),
    CAPE(1),
    AMULET(2),
    WEAPON(3),
    CHEST(4),
    SHIELD(5),
    BOTTOMS(7),
    GLOVES(9),
    BOOTS(10),
    RING(12),
    ARROWS(13),
    PLATEBODY(15),
    FULL_HELM(16),
    FULL_MASK(17);

    private static final Map<Integer, Slot> MAP = new HashMap<>();

    static{
        for(final Slot slot : values())
            MAP.put(slot.id, slot);
    }

    public final int id;

    private Slot(final int id){
        this.id = id;
    }

    public static Slot getSlot(final int id){
        return MAP.get(id);
    }
}
