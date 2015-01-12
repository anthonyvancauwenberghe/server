package org.hyperion.rs2.model.content.specialareas;

import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.content.specialareas.impl.NewGamePK;
import org.hyperion.rs2.model.content.specialareas.impl.PurePk;

import java.util.*;

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
        map.put("newgamepk", new NewGamePK());

        for(final Map.Entry<String, SpecialArea> area : map.entrySet()) {
            CommandHandler.submit(area.getValue().command(area.getKey()));
        }
    }

    public static Optional<SpecialArea> get(final String key) {
        return Optional.of(map.get(key));
    }

    public static Set<Map.Entry<String, SpecialArea>> getAll() {
        return map.entrySet();
    }

    public static Collection<SpecialArea> getAreas() {
        return map.values();
    }

}