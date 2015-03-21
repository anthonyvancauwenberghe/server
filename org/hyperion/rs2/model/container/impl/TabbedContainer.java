package org.hyperion.rs2.model.container.impl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.bank.BankItem;

import java.math.BigInteger;

/**
 * Created by User on 3/20/2015.
 */
public class TabbedContainer extends Container {

    private Player player;

    public TabbedContainer(Type type, int capacity, Player player) {
        super(type, capacity);
        this.player = player;
    }

    public boolean add(Item item) {
        if(item == null) {
            return false;        }
        BankItem bankItem;
        if(!(item instanceof BankItem))
            item = item.toBankItem(player.getBankField().getTabIndex());
        bankItem = (BankItem) item;
        boolean contains = contains(bankItem.getId());
        int here = bankItem.getTabIndex();
        int toTab = contains ? ((BankItem) getById(bankItem.getId())).getTabIndex() : here;
        BankItem toAdd = new BankItem(toTab, bankItem.getId(), bankItem.getCount());
        if(bankItem.getId() < 0)
            return false;
        if(bankItem.getDefinition().isStackable() || getType().equals(Type.ALWAYS_STACK)) {
            for(int i = 0; i < getItems().length; i++) {
                if(getItems()[i] != null && getItems()[i].getId() == toAdd.getId()) {
                    int totalCount = toAdd.getCount() + getItems()[i].getCount();
                    long fuck_all_count = BigInteger.valueOf(toAdd.getCount()).add(BigInteger.valueOf(getItems()[i].getCount())).longValueExact();
                    if(fuck_all_count >= Constants.MAX_ITEMS || totalCount < 1) {
                        return false;
                    }
                    BankItem newBankItem = new BankItem(toAdd.getTabIndex(), getItems()[i].getId(), getItems()[i].getCount() + bankItem.getCount());
                    set(i, newBankItem);
                    if (!contains) {
                        player.getBankField().getTabAmounts()[toTab]++;
                    }
                    return true;
                }
            }
            int slot = freeSlot();
            if (slot == -1) {
                return false;
            } else {
                set(slot, bankItem);
                if (!contains) {
                    player.getBankField().getTabAmounts()[toTab]++;
                }
                return true;
            }
        } else {
            int slots = freeSlots();
            if(slots >= bankItem.getCount()) {
                boolean b = isFiringEvents();
                setFiringEvents(false);
                try {
                    for (int i = 0; i < bankItem.getCount(); i++) {
                        set(freeSlot(), new BankItem(bankItem.getTabIndex(), bankItem.getId(), 1));
                    }
                    if (!contains) {
                        player.getBankField().getTabAmounts()[toTab]++;
                    }
                    return true;
                } finally {
                    setFiringEvents(b);
                }
            } else {
                return false;
            }
        }
    }

}