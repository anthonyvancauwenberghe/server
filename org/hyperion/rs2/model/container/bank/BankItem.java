package org.hyperion.rs2.model.container.bank;

import org.hyperion.rs2.model.Item;

public class BankItem extends Item {

    private int tabIndex;

    public BankItem(final int tabSlot, final int itemId, final int amount) {
        super(itemId, amount);
        setTabSlot(tabSlot);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public BankItem copy() {
        return new BankItem(tabIndex, getId(), getCount());
    }

    public void setID(final int itemID) {
        setId(itemID);
    }

    public void setTabSlot(final int tabSlot) {
        this.tabIndex = tabSlot;
    }

    public String toString() {
        return "BankItem[" + getId() + ", " + getCount() + ", " + tabIndex + "]";
    }

}