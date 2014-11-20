package org.hyperion.rs2.model.combat.specialareas;

import org.hyperion.rs2.model.combat.specialareas.impl.PurePk;

import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/20/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpecialAreaHolder {
    private static final int PURE_PK = 0;
    private static final SpecialArea[] areas = {new PurePk()};
    public static Optional<SpecialArea> get(final String key) {
        if(key.equalsIgnoreCase("PUREPK"))
            return Optional.of(areas[PURE_PK]);
        return Optional.empty();
    }

    public static SpecialArea[] getAll() {
        return areas;
    }

}
