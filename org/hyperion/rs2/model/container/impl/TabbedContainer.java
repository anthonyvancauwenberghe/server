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
            item = item.toBankItem(0);
        bankItem = (BankItem) item;
        int here = bankItem.getTabIndex();
        if(bankItem.getId() < 0)
            return false;
        if(bankItem.getDefinition().isStackable() || getType().equals(Type.ALWAYS_STACK)) {
            for(int i = 0; i < getItems().length; i++) {
                if(getItems()[i] != null && getItems()[i].getId() == bankItem.getId()) {
                    int totalCount = bankItem.getCount() + getItems()[i].getCount();
                    long fuck_all_count = BigInteger.valueOf(bankItem.getCount()).add(BigInteger.valueOf(getItems()[i].getCount())).longValueExact();
                    if(fuck_all_count >= Constants.MAX_ITEMS || totalCount < 1) {
                        return false;
                    }
                    BankItem newBankItem = new BankItem(here, getItems()[i].getId(), getItems()[i].getCount() + bankItem.getCount());
                    set(i, newBankItem);
                    return true;
                }
            }
            if(size() == Bank.SIZE)
                return false;
            int slot = player.getBankField().getOffset(bankItem.getTabIndex()) + player.getBankField().getTabAmounts()[bankItem.getTabIndex()];
            final Item[] old = items.clone();
            for(int i = 0 ; i < old.length; i++ ){
                if ( i < slot)
                    items[i] = old[i];
                else if(i == slot)
                    items[i] = null;
                else
                    items[i] = old[i - 1];

            }

            if (slot == -1 || slot >= Bank.SIZE) {
                return false;
            } else {
                set(slot, bankItem);
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
     * Sets an item.
     *
     * @param index The position in the container.
     * @param item  The item.
     */
    @Override
    public void set(int index, Item item) {
        final Item old = get(index);
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

    @Override
    public Item get(int slot) {
        Item item = super.get(slot);
        if(item != null && !(item instanceof BankItem)) {
            return item.toBankItem(player.getBankField().getTabForSlot(slot));
        }
        return item;
    }

}
