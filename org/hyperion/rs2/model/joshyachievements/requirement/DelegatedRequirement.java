package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Function;
import org.hyperion.rs2.model.Player;

public class DelegatedRequirement implements Requirement{

    private static class Constant implements Function<Player, Integer>{

        private final int value;

        private Constant(final int value){
            this.value = value;
        }

        public Integer apply(final Player player){
            return value;
        }
    }

    private final Function<Player, Integer> max;

    public DelegatedRequirement(final Function<Player, Integer> max){
        this.max = max;
    }

    public DelegatedRequirement(final int max){
        this(new Constant(max));
    }

    public Integer apply(final Player player){
        return max.apply(player);
    }
}
