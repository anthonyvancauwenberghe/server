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
                    BankItem newBankItem = new BankItem(((BankItem)get(i)).getTabIndex(), getItems()[i].getId(), getItems()[i].getCount() + bankItem.getCount());
                    set(i, newBankItem);
                    return true;
                }
            }
            return insert(bankItem, -1);
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

    public boolean insert(final BankItem bankItem, int slot) {
        if(slot == -1 && size() == Bank.SIZE)
            return false;
        if(slot == -1)
            slot = player.getBankField().getOffset(bankItem.getTabIndex()) + player.getBankField().getTabAmounts()[bankItem.getTabIndex()];
        final Item[] old = items.clone();
        for(int i = 0 ; i < old.length; i++ ){
            if ( i < slot)
                set(i, old[i]);
            else if(i == slot)
                set(i, null);
            else
                set(i, old[i - 1]);

        }

        if (slot == -1 || slot >= Bank.SIZE) {
            return false;
        } else {
            set(slot, bankItem);
            return true;
        }
    }

    @Override
    public int remove(int preferredSlot, Item item) {
        int remove = super.remove(preferredSlot, item);
        if(remove != 0)
            shift();
        return remove;
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
