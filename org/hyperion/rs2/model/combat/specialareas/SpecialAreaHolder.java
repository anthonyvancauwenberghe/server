package org.hyperion.rs2.model.combat.specialareas;

import org.hyperion.rs2.model.combat.specialareas.impl.PurePk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/20/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpecialAreaHolder {
    private static final Map<String, SpecialArea> map;

    static {
        map = new HashMap<>();
        map.put("purepk", new PurePk());
    }

    public static Optional<SpecialArea> get(final String key) {
        return Optional.of(map.get(key));
    }

    public static Set<Map.Entry<String, SpecialArea>> getAll() {
        return map.entrySet();
    }

}
