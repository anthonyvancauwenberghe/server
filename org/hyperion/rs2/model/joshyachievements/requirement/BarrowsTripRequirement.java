package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class BarrowsTripRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER =
            ctx -> ctx.getRequirement() instanceof BarrowsTripRequirement;

    public BarrowsTripRequirement(final int trips){
        super(trips);
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
