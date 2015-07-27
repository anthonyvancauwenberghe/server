package org.hyperion.rs2.model.joshyachievements.condition;

import java.util.function.Predicate;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class SkillCondition implements Condition{

    private static class Filter implements Predicate<AchievementContext>{

        private final int skill;

        private Filter(final int skill){
            this.skill = skill;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getCondition() instanceof SkillCondition
                    && ctx.<SkillCondition>getCondition().getSkill() == skill;
        }
    }

    private final int skill;
    private final int xp;

    public SkillCondition(final int skill, final int xp){
        this.skill = skill;
        this.xp = xp;
    }

    public int getSkill(){
        return skill;
    }

    public int getXp(){
        return xp;
    }

    public int getMax(final Player player){
        return xp;
    }

    public int getCurrent(final Player player){
        return player.getSkills().getExperience(skill);
    }

    public String toString(){
        final String name = Skills.SKILL_NAME[skill];
        return String.format("Get %,d %s XP!", xp, skill);
    }

    public static Predicate<AchievementContext> filter(final int skill){
        return new Filter(skill);
    }
}
