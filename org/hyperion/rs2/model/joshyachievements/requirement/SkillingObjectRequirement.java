package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class SkillingObjectRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int skill;
        private final int itemId;

        private Filter(final int skill, final int itemId){
            this.skill = skill;
            this.itemId = itemId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof SkillingObjectRequirement
                    && ctx.<SkillingObjectRequirement>getRequirement().getSkill() == skill
                    && ctx.<SkillingObjectRequirement>getRequirement().getItemId() == itemId;
        }
    }

    private final int skill;
    private final int itemId;

    public SkillingObjectRequirement(final int skill, final int itemId, final int value){
        super(value);
        this.skill = skill;
        this.itemId = itemId;
    }

    public int getSkill(){
        return skill;
    }

    public int getItemId(){
        return itemId;
    }

    public String toString(){
        return String.format("SkillingObjectRequirement(skill=%d,itemId=%d,value=%,d)", skill, itemId, get());
    }

    public static Predicate<AchievementContext> filter(final int skill, final int itemId){
        return new Filter(skill, itemId);
    }
}
