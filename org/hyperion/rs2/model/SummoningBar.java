package org.hyperion.rs2.model;

public class SummoningBar {
    public static final int DELAY = 1500;
    private final Player player;
    private long lastSpec;
    private int amount;

    public SummoningBar(final Player p) {
        this.player = p;
    }

    public long getLast() {
        return lastSpec;
    }

    public void setLast(final long l) {
        lastSpec = l;
    }

    public void increment(final int increase) {
        amount += increase;
        if(amount > 100)
            amount = 100;
    }

    public void decrement(final int decrease) {
        amount -= decrease;
        if(amount < 0)
            amount = 0;
    }

    public void cycle() {
        amount += 10;
        if(amount > 100)
            amount = 100;
    }

    public int getAmount() {
        return amount;
    }

}
