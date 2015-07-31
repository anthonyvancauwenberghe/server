package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class KillStreakRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER = ctx ->
            ctx.getRequirement() instanceof KillStreakRequirement;

    public KillStreakRequirement(final int killstreak){
        super(killstreak);
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
