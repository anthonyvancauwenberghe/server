package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class SkillXpRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int skill;

        private Filter(final int skill){
            this.skill = skill;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof SkillXpRequirement
                    && ctx.<SkillXpRequirement>getRequirement().getSkill() == skill;
        }
    }

    private final int skill;

    public SkillXpRequirement(final int skill, final int xp){
        super(xp);
        this.skill = skill;
    }

    public int getSkill(){
        return skill;
    }

    public String toString(){
        return String.format("SkillXpRequirement(skill=%d,value=%,d)", skill, get());
    }

    public static Predicate<AchievementContext> filter(final int skill){
        return new Filter(skill);
    }
}
