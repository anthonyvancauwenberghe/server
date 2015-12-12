package org.hyperion.rs2.model.content.ge;

import org.hyperion.rs2.model.Item;

/**
 * Created by Allen Kinzalow on 4/24/2015.
 */
public class OfferCollection {

    private Item firstSlot;
    private Item secondSlot;

    public OfferCollection() {
    }

    public OfferCollection(final Item firstSlot, final Item secondSlot) {
        this.firstSlot = firstSlot;
        this.secondSlot = secondSlot;
    }

    public Item getFirstSlot() {
        return firstSlot;
    }

    public void setFirstSlot(final Item item) {
        this.firstSlot = item;
    }

    public Item getSecondSlot() {
        return secondSlot;
    }

    public void setSecondSlot(final Item item) {
        this.secondSlot = item;
    }

    public boolean isEmpty() {
        return firstSlot == null && secondSlot == null;
    }

}
