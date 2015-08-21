package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class BhTargetKillRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER =
            ctx -> ctx.getRequirement() instanceof BhTargetKillRequirement;

    public BhTargetKillRequirement(final int kills){
        super(kills);
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
