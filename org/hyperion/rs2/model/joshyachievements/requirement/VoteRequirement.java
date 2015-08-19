package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class VoteRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER =
            ctx -> ctx.getRequirement() instanceof VoteRequirement;

    public VoteRequirement(final int amount){
        super(amount);
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
