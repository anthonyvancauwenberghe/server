package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Function;
import org.hyperion.rs2.model.Player;

public class DelegatedCondition implements Condition{

    private static class Constant implements Function<Player, Integer>{

        private final int value;

        private Constant(final int value){
            this.value = value;
        }

        public Integer apply(final Player player){
            return value;
        }
    }

    private final Function<Player, Integer> current;
    private final Function<Player, Integer> max;

    public DelegatedCondition(final Function<Player, Integer> current, final Function<Player, Integer> max){
        this.current = current;
        this.max = max;
    }

    public DelegatedCondition(final Function<Player, Integer> current, final int max){
        this(current, new Constant(max));
    }

    public Function<Player, Integer> getCurrentFunction(){
        return current;
    }

    public Function<Player, Integer> getMaxFunction(){
        return max;
    }

    public int getCurrent(final Player player){
        return current.apply(player);
    }

    public int getMax(final Player player){
        return max.apply(player);
    }
}
