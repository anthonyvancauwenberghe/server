package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class ItemOpenRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final int itemId;

        private Filter(final int itemId){
            this.itemId = itemId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof ItemOpenRequirement
                    && ctx.<ItemOpenRequirement>getRequirement().getItemIds().contains(itemId);
        }
    }

    private final List<Integer> itemIds;

    public ItemOpenRequirement(final int[] itemIds, final int amount){
        super(amount);
        this.itemIds = Arrays.stream(itemIds).boxed().collect(Collectors.toList());
    }

    public List<Integer> getItemIds(){
        return itemIds;
    }

    public static Predicate<AchievementContext> filter(final int itemId){
        return new Filter(itemId);
    }
}
