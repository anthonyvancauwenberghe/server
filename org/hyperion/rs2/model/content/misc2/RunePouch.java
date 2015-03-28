package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.RunePouchContainerListener;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentTemplate;

/**
 * Created by Scott Perretta on 3/22/2015.
 */
public class RunePouch implements ContentTemplate {

    public static final int SIZE = 3;
    public static final int POUCH = 17999;
    public static final int INTERFACE = 28990;
    public static final int RUNE_INTERFACE = 28992;
    public static final int INVENTORY_INTERFACE = 28995;

    public static void open(Player player) {
        if(player.getBankField().isBanking()) {
            player.sendMessage("You cannot store runes and bank at the same time.");
            return;
        }
        player.getActionSender().showInterface(INTERFACE);
        player.getInterfaceState().addListener(player.getRunePouch(), new RunePouchContainerListener(player));
        player.getRunePouch().shift();
        player.getRunePouch().setFiringEvents(true);
    }

    private static boolean isRune(Item item) {
        if(item != null && ((item.getId() >= 554 && item.getId() <= 565) || item.getId() == 9075)) {
                return true;
        }
        return false;
    }

    public static void withdraw(Player player, int id, int amount) {
        if (player.getBankField().isBanking()) {
            return;
        }
        final int slot = player.getRunePouch().getSlotById(id);
        if(slot < 0) {
            return;
        }
        Item item = player.getRunePouch().get(slot);
        if ((item == null) || (item.getId() != id)) {
            return;
        }
        int transferAmount = player.getRunePouch().getCount(item.getId());
        if (transferAmount >= amount) {
            transferAmount = amount;
        } else if (transferAmount == 0) {
            return;
        }
        if ((player.getInventory().freeSlots() <= 0)
                && (player.getInventory().getById(id) == null)) {
            player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many.");
            return;
        }
        if (player.getInventory().add(new Item(id, transferAmount))) {
            int newAmount = item.getCount() - transferAmount;
            if (newAmount <= 0) {
                player.getRunePouch().set(slot, null);
                player.getRunePouch().shift();
                player.getRunePouch().fireItemsChanged();
            } else {
                item.setCount(newAmount);
                player.getRunePouch().set(slot, item);
                player.getRunePouch().fireItemChanged(slot);
            }
        } else {
            player.getActionSender().sendMessage("You don't have enough inventory space to withdraw that many.");
        }
    }

    public static final void deposit(Player player, int slot, int id, int amount) {
        if(player.getBankField().isBanking() || player.openedBoB) {
            return;
        }
        final boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
        final boolean runePouchFiringEvents = player.getRunePouch().isFiringEvents();
        player.getInventory().setFiringEvents(false);
        player.getRunePouch().setFiringEvents(true);
        try {
            Item item = player.getInventory().getById(id);
            if (item == null || item.getId() != id) {
                return;
            }
            if(!isRune(item)) {
                player.sendMessage("You can only store runes in this pouch.");
                return;
            }
            int transferAmount = player.getInventory().getCount(id);
            if(transferAmount >= amount) {
                transferAmount = amount;
            } else if(transferAmount == 0) {
                return;
            }
            if ((player.getRunePouch().freeSlots() < 1) && (player.getRunePouch().getById(id) == null)) {
                player.getActionSender().sendMessage("You don't have enough space in your rune pouch.");
                return;
            }
            int newInventoryAmount = item.getCount() - transferAmount;
            Item newItem = (newInventoryAmount <= 0) ? null : new Item(item.getId(), newInventoryAmount);
            if (!player.getRunePouch().add(new Item(item.getId(), transferAmount))) {
                player.getActionSender().sendMessage("You don't have enough space in your rune pouch.");
            } else {
                player.getInventory().set(slot, newItem);
                player.getInventory().fireItemsChanged();
                player.getRunePouch().fireItemChanged(player.getRunePouch().getSlotById(id));
            }
        } finally {
            player.getInventory().setFiringEvents(inventoryFiringEvents);
            player.getRunePouch().setFiringEvents(runePouchFiringEvents);
        }
    }

    public static void empty(Player player) {
        if(player == null || player.getRunePouch() == null)
            return;
        for(Item rune : player.getRunePouch().toArray()) {
            if (rune != null) {
                player.getRunePouch().remove(rune);
                player.getInventory().add(rune);
            }
        }
    }

    @Override
    public boolean clickObject2(Player player, int type, int a, int b, int c, int d) {
        if(type == ClickType.ITEM_OPTOION6) {
            if(player.getRunePouch().size() > 0) {
                RunePouch.empty(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getValues(int type) {
        if(type == ClickType.EAT || type == ClickType.ITEM_OPTOION6)
            return new int[]{POUCH};
        return new int[0];
    }

    @Override
    public boolean itemOptionOne(Player player, int id, int slot, int interfaceId) {
        player.openedBoB = false;
        player.getBankField().setBanking(false);
        RunePouch.open(player);
        return false;
    }
}