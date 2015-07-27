package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class PlayerKillCondition extends DelegatedCondition{

    private static final Predicate<AchievementContext> FILTER = ctx ->
            ctx.getCondition() instanceof PlayerKillCondition;

    public PlayerKillCondition(final int kills){
        super(Player::getKillCount, kills);
    }

    public String toString(){
        return String.format("Kill %,d players!", getMax(null));
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
