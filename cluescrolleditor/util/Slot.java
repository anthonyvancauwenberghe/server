package cluescrolleditor.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private static final Map<Integer, Slot> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(s -> s.id, Function.<Slot>identity()));

    public final int id;

    private Slot(final int id){
        this.id = id;
    }

    public static Slot getSlot(final int id){
        return MAP.get(id);
    }
}
