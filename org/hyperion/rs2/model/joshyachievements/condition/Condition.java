package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;

public interface Condition extends Predicate<Player>{

    int getMax(final Player player);

    int getCurrent(final Player player);

    default int getRemaining(final Player player){
        return getMax(player) - getCurrent(player);
    }

    default boolean test(final Player player){
        return getRemaining(player) <= 0;
    }
}
