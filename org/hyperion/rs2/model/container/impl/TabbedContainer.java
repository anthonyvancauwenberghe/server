package org.hyperion.rs2.model.container.impl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankItem;

import java.math.BigInteger;
import java.util.Arrays;

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
                        //player.getBankField().getTabAmounts()[toTab]++;
                    }
                    return true;
                }
            }
            int slot = player.getBankField().getOffset(bankItem.getTabIndex()) + player.getBankField().getTabAmounts()[bankItem.getTabIndex()];
            if (slot == -1 || slot >= Bank.SIZE) {
                return false;
            } else {
                set(slot, bankItem);
                if (!contains) {
                    //player.getBankField().getTabAmounts()[toTab]++;
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
                        //player.getBankField().getTabAmounts()[toTab]++;
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

    /**
     * Removes an item.
     *
     * @param preferredSlot The preferred slot.
     * @param item          The item to remove.
     * @return The number of items removed.
     */
    /*
    @Override
    public int remove(int preferredSlot, Item item) {
        int removed = 0;
        if(item == null || item.getDefinition() == null) {
            //System.out.println("Container null , PLEASE FIX MARTIN!");
            return removed;
        }
        //if(item.getCount() == 0)
        //	return 0;
        if(item.getDefinition().isStackable() || getType().equals(Type.ALWAYS_STACK)) {
            int slot = getSlotById(item.getId());
            if(slot == - 1)
                return removed;
            Item stack = get(slot);
            if(stack.getCount() > item.getCount()) {
                removed = item.getCount();
                set(slot, new Item(stack.getId(), stack.getCount() - item.getCount()));
            } else {
                removed = stack.getCount();
                set(slot, null);
            }
        } else {
            for(int i = 0; i < item.getCount(); i++) {
                int slot = getSlotById(item.getId());
                if(slot == - 1)
                    continue;
                if(i == 0 && preferredSlot != - 1) {
                    Item inSlot = get(preferredSlot);
                    if(inSlot.getId() == item.getId()) {
                        slot = preferredSlot;
                    }
                }
                if(slot != - 1) {
                    removed++;
                    set(slot, null);
                } else {
                    break;
                }
            }
        }
        return removed;
    }
    */

    /**
     * Sets an item.
     *
     * @param index The position in the container.
     * @param item  The item.
     */
    @Override
    public void set(int index, Item item) {
        final Item old = items[index];
        if(item == null && old != null) {
            player.getBankField().getTabAmounts()[((BankItem)old).getTabIndex()]--;
        }
        items[index] = item;
        if(old == null && item != null) {
            player.getBankField().getTabAmounts()[((BankItem)item).getTabIndex()]++;
        }
        if(isFiringEvents()) {
            fireItemChanged(index);
        }
    }

    @Override
    public void shift() {
        Item[] old = items;
        items = new Item[capacity];
        int newIndex = 0;
        Arrays.fill(player.getBankField().getTabAmounts(), 0);
        for(int i = 0; i < items.length; i++) {
            if(old[i] != null) {
                items[newIndex] = old[i];
                player.getBankField().getTabAmounts()[((BankItem)items[newIndex]).getTabIndex()]++;
                newIndex++;
            }
        }
        if(isFiringEvents()) {
            fireItemsChanged();
        }
    }

}
