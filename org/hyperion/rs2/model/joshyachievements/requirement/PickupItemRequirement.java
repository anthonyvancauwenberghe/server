package org.hyperion.rs2.model.joshyachievements.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;

public class PickupItemRequirement extends DelegatedRequirement{

    private static class Filter implements Predicate<AchievementContext>{

        private final From from;
        private final int itemId;

        private Filter(final From from, final int itemId){
            this.from = from;
            this.itemId = itemId;
        }

        public boolean test(final AchievementContext ctx){
            return ctx.getRequirement() instanceof PickupItemRequirement
                    && ctx.<PickupItemRequirement>getRequirement().getFrom() == from
                    && ctx.<PickupItemRequirement>getRequirement().getItemIds().contains(itemId);
        }
    }

    public enum From{
        PLAYER, NPC
    }

    private final From from;
    private final List<Integer> itemIds;

    public PickupItemRequirement(final From from, final int[] itemIds, final int quantity){
        super(quantity);
        this.from = from;
        this.itemIds = Arrays.stream(itemIds).boxed().collect(Collectors.toList());
    }

    public From getFrom(){
        return from;
    }

    public List<Integer> getItemIds(){
        return itemIds;
    }

    public static Predicate<AchievementContext> filter(final From from, final int itemId){
        return new Filter(from, itemId);
    }
}
