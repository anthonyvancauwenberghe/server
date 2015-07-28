package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class SkillRequirement implements Requirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int skill;

        private Filter(final int skill){
            this.skill = skill;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof SkillRequirement
                    && ctx.<SkillRequirement>getRequirement().getSkill() == skill;
        }
    }

    private final int skill;
    private final int xp;

    public SkillRequirement(final int skill, final int xp){
        this.skill = skill;
        this.xp = xp;
    }

    public int getSkill(){
        return skill;
    }

    public int getXp(){
        return xp;
    }

    public Integer apply(final Player player){
        return xp;
    }

    public String toString(final Player player){
        final String name = Skills.SKILL_NAME[skill];
        return String.format("Get %,d %s XP!", xp, name);
    }

    public static Predicate<AchievementContext> filter(final int skill){
        return new Filter(skill);
    }
}
