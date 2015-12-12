package org.hyperion.rs2.model;

public class NPCDrop {
    private final int itemId; //item id
    private final int minAmount;
    private final int maxAmount;
    private final int chance; //chance out of 1000, i.e. chance = 100 = 1/10 chance of the item

    private NPCDrop(final int itemId, final int minAmount, final int maxAmount, final int chance) {
        this.itemId = itemId;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.chance = chance;
    }

    public static NPCDrop create(final int itemId, final int minAmount, final int maxAmount, final int chance) {
        return new NPCDrop(itemId, minAmount, maxAmount, chance);
    }

    public int getChance() {
        return chance;
    }

    public int getId() {
        return itemId;
    }

    public int getMin() {
        return minAmount;
    }

    public int getMax() {
        return maxAmount;
    }
}
