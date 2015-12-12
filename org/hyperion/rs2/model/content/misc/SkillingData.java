package org.hyperion.rs2.model.content.misc;

public class SkillingData {
    private int amount = -1;
    private int firstId = -1;
    private int secondId = -1;
    private int thirdId = -1;
    private int firstSlot = -1;
    private int secondSlot = -1;

    public SkillingData() {

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(final int firstId) {
        this.firstId = firstId;
    }

    public int getSecondId() {
        return secondId;
    }

    public void setSecondId(final int secondId) {
        this.secondId = secondId;
    }

    public int getThirdId() {
        return thirdId;
    }

    public void setThirdId(final int thirdId) {
        this.thirdId = thirdId;
    }

    public int getFirstSlot() {
        return firstSlot;
    }

    public void setFirstSlot(final int firstSlot) {
        this.firstSlot = firstSlot;
    }

    public int getSecondSlot() {
        return secondSlot;
    }

    public void setSecondSlot(final int secondSlot) {
        this.secondSlot = secondSlot;
    }
}
