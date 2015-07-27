package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class KillStreakCondition extends DelegatedCondition{

    private static final Predicate<AchievementContext> FILTER = ctx ->
            ctx.getCondition() instanceof KillStreakCondition;

    public KillStreakCondition(final int killstreak){
        super(Player::getKillStreak, killstreak);
    }

    public String toString(){
        return String.format("Get a %,d killstreak!", getMax(null));
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
