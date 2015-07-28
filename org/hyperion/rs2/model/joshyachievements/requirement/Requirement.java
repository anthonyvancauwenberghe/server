package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Function;
import org.hyperion.rs2.model.Player;

public interface Requirement extends Function<Player, Integer>{

    default String toString(final Player player){
        return null;
    }

}
