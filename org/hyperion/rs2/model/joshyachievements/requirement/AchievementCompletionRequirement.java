package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class AchievementCompletionRequirement extends DelegatedRequirement{

    private static final Predicate<AchievementContext> FILTER = ctx ->
            ctx.getRequirement() instanceof AchievementCompletionRequirement;

    public AchievementCompletionRequirement(final int numberCompleted){
        super(numberCompleted);
    }

    public static Predicate<AchievementContext> filter(){
        return FILTER;
    }
}
