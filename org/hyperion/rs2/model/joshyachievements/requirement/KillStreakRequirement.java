package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class KillStreakRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER = ctx ->
            ctx.getRequirement() instanceof KillStreakRequirement;

    public KillStreakRequirement(final int killstreak){
        super(killstreak);
    }

    public String toString(final Player player){
        return String.format("Get a %,d killstreak!", apply(player));
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
