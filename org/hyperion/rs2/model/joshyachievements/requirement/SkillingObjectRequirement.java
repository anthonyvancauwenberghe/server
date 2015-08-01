package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
                    && ctx.<SkillingObjectRequirement>getRequirement().getItemIds().contains(itemId);
        }
    }

    private final int skill;
    private final List<Integer> itemIds;

    public SkillingObjectRequirement(final int skill, final int[] itemIds, final int value){
        super(value);
        this.skill = skill;
        this.itemIds = Arrays.stream(itemIds).boxed().collect(Collectors.toList());
    }

    public int getSkill(){
        return skill;
    }

    public List<Integer> getItemIds(){
        return itemIds;
    }

    public String toString(){
        return String.format("SkillingObjectRequirement(skill=%d,itemIds=%s,value=%,d)", skill, itemIds, get());
    }

    public static Predicate<AchievementContext> filter(final int skill, final int itemId){
        return new Filter(skill, itemId);
    }
}
