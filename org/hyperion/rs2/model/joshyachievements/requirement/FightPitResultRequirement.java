package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class FightPitResultRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final Result result;

        private Filter(final Result result){
            this.result = result;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof FightPitResultRequirement
                    && ctx.<FightPitResultRequirement>getRequirement().getResult() == result;
        }
    }

    public enum Result{
        WIN, LOSE
    }

    private final Result result;

    public FightPitResultRequirement(final Result result, final int amount){
        super(amount);
        this.result = result;
    }

    public Result getResult(){
        return result;
    }

    public static Predicate<AchievementContext> filter(final Result result){
        return new Filter(result);
    }
}
