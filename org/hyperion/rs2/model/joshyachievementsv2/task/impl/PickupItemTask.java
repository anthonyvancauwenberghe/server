package org.hyperion.rs2.model.joshyachievementsv2.task.impl;

import org.hyperion.rs2.model.joshyachievementsv2.task.Task;
import org.hyperion.rs2.model.joshyachievementsv2.utils.AchievementUtils;

import java.util.List;

public class PickupItemTask extends Task {

    public final From from;
    public final List<Integer> itemIds;

    public PickupItemTask(final int id, final From from, final List<Integer> itemIds, final int quantity) {
        super(id, quantity);
        this.from = from;
        this.itemIds = itemIds;

        final String itemsJoined = AchievementUtils.joinItems(itemIds);

        desc = String.format("Pickup %,d %s from %sS", quantity, itemsJoined, from);
    }

    public static Filter<PickupItemTask> filter(final From from, final int itemId) {
        return new MyFilter(from, itemId);
    }

    public enum From {
        PLAYER, NPC
    }

    private static class MyFilter extends Filter<PickupItemTask> {

        private final From from;
        private final int itemId;

        private MyFilter(final From from, final int itemId) {
            super(PickupItemTask.class);
            this.from = from;
            this.itemId = itemId;
        }

        public boolean test(final PickupItemTask t) {
            return super.test(t) && t.from == from && t.itemIds.contains(itemId);
        }
    }
}
