package org.hyperion.rs2.model.container;

import org.hyperion.rs2.model.BankPin;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

/**
 * Banking utility class.
 *
 * @author Graham Edgecombe
 */
public class Bank {

    /**
     * The bank size.
     */
    public static final int SIZE = 352;

    /**
     * The player inventory interface.
     */
    public static final int PLAYER_INVENTORY_INTERFACE = 5064;

    /**
     * The bank inventory interface.
     */
    public static final int BANK_INVENTORY_INTERFACE = 5382;

    /**
     * The Deposit Box interface.
     */
    public static final int DEPOSIT_INVENTORY_INTERFACE = 7433;//7423


    /**
     * Opens the bank for the specified player.
     *
     * @param player The player to open the bank for.
     */
    public static void open(Player player, boolean setPin) {
        if(player.getLocation().inPvPArea()) { //:(
            return;
        }
        if(!ItemSpawning.canSpawn(player)) {
            return;
        }
        if(FightPits.inPits(player))
            return;
        player.resetingPin = false;
        if((player.bankPin.length() < 4 && setPin)
                || (player.bankPin.length() >= 4 && ! player.bankPin
                .equals(player.enterPin))) {
            BankPin.loadUpPinInterface(player);
            return;
        }
        player.openedBoB = false;
        player.getBank().shift();
        player.getActionSender().sendInterfaceInventory(5292, 5063);
        player.getInterfaceState().addListener(player.getBank(),
                new InterfaceContainerListener(player, 5382));
        player.getInterfaceState().addListener(player.getInventory(),
                new InterfaceContainerListener(player, 5064));
    }

    /**
     * Opens the deposit box for the certin player
     *
     * @param player The player to have a opened deposit Box
     */
    public static void openDepositBox(Player player) {
        player.getBank().shift();
        player.getActionSender().sendInterfaceInventory(4465, 197);
        player.getInterfaceState().addListener(player.getInventory(),
                new InterfaceContainerListener(player, 7423));
    }

    /**
     * Withdraws an item.
     *
     * @param player The player.
     * @param slot   The slot in the player's inventory.
     * @param id     The item id.
     * @param amount The amount of the item to deposit.
     */
    public static void withdraw(Player player, int slot, int id, int amount) {
        if(player.getLocation().inPvPArea())
            return;
        if(!ItemSpawning.canSpawn(player))
            return;
        if(slot < 0 || slot > player.getBank().capacity() || id < 0 || id > ItemDefinition.MAX_ID)
            return;
        Item item = player.getBank().getById(id);
        if(item == null)
            return;
        if(!player.getBank().contains(id)) {
            return; // invalid packet, or client out of sync
        }
        if(item.getId() != id) {
            return; // invalid packet, or client out of sync
        }
        if(FightPits.inPits(player)) //trying to smuggle
            return;
        int transferAmount = item.getCount();
        if(transferAmount >= amount) {
            transferAmount = amount;
        } else if(transferAmount == 0) {
            return; // invalid packet, or client out of sync
        }
        int newId = item.getId(); // TODO deal with withdraw as notes!
        if(player.getSettings().isWithdrawingAsNotes()) {
            if(item.getDefinition().isNoteable()) {
                newId = item.getDefinition().getNotedId();
            }
        }
        ItemDefinition def = ItemDefinition.forId(newId);
        if(def.isStackable()) {
            if(player.getInventory().freeSlots() <= 0
                    && player.getInventory().getById(newId) == null) {
                player.getActionSender()
                        .sendMessage(
                                "You don't have enough inventory space to withdraw that many."); // this
                // is
                // the
                // real
                // message
            }
        } else {
            int free = player.getInventory().freeSlots();
            if(transferAmount > free) {
                player.getActionSender()
                        .sendMessage(
                                "You don't have enough inventory space to withdraw that many."); // this
                // is
                // the
                // real
                // message
                transferAmount = free;
            }
        }
        // now add it to inv
        if(player.getInventory().add(new Item(newId, transferAmount))) {
            // all items in the bank are stacked, makes it very easy!
            int newAmount = item.getCount() - transferAmount;
            if(newAmount <= 0) {
                player.getBank().remove(Item.create(id, transferAmount));
            } else {
                player.getBank().remove(Item.create(id, transferAmount));
            }
        } else {
            player.getActionSender()
                    .sendMessage(
                            "You don't have enough inventory space to withdraw that many."); // this
            // is
            // the
            // real
            // message
        }
    }

    /**
     * Deposits an item.
     *
     * @param player The player.
     * @param slot   The slot in the player's inventory.
     * @param id     The item id.
     * @param amount The amount of the item to deposit.
     */
    public static void deposit(Player player, int slot, int id, int amount) {
        deposit(player, slot, id, amount, player.getInventory());
    }

    public static void deposit(Player player, int slot, int id, int amount,
                               Container container) {
        if(player.getLocation().inPvPArea())
            return;
        if(slot < 0 || slot > container.capacity() || id < 0 || id > ItemDefinition.MAX_ID)
            return;
        if(Location.inAttackableArea(player))
            return;
        if(FightPits.inPits(player))
            return;
        boolean inventoryFiringEvents = container.isFiringEvents();
        container.setFiringEvents(false);
        try {
            Item item = container.get(slot);
            if(item == null) {
                return; // invalid packet, or client out of sync
            }
            if(item.getId() != id) {
                return; // invalid packet, or client out of sync
            }
            int transferAmount = container.getCount(id);
            if(transferAmount >= amount) {
                transferAmount = amount;
            } else if(transferAmount == 0) {
                return; // invalid packet, or client out of sync
            }
            boolean noted = item.getDefinition().isNoted();
            if(item.getDefinition().isStackable() || noted) {
                int bankedId = noted ? item.getDefinition().getNormalId()
                        : item.getId();
                if(player.getBank().freeSlots() < 1
                        && player.getBank().getById(bankedId) == null) {
                    player.getActionSender()
                            .sendMessage(
                                    "You don't have enough space in your bank account."); // this
                    // is
                    // the
                    // real
                    // message
                }
                // we only need to remove from one stack
                int newInventoryAmount = item.getCount() - transferAmount;
                Item newItem;
                if(newInventoryAmount <= 0) {
                    newItem = null;
                } else {
                    newItem = new Item(item.getId(), newInventoryAmount);
                }
                if(! player.getBank().add(new Item(bankedId, transferAmount))) {
                    player.getActionSender()
                            .sendMessage(
                                    "You don't have enough space in your bank account."); // this
                    // is
                    // the
                    // real
                    // message
                } else {
                    container.set(slot, newItem);
                    container.fireItemsChanged();
                    player.getBank().fireItemsChanged();
                }
            } else {
                if(player.getBank().freeSlots() < transferAmount) {
                    player.getActionSender()
                            .sendMessage(
                                    "You don't have enough space in your bank account."); // this
                    // is
                    // the
                    // real
                    // message
                }
                if(! player.getBank().add(
                        new Item(item.getId(), transferAmount))) {
                    player.getActionSender()
                            .sendMessage(
                                    "You don't have enough space in your bank account."); // this
                    // is
                    // the
                    // real
                    // message
                } else {
                    // we need to remove multiple items
                    for(int i = 0; i < transferAmount; i++) {
                        if(i == 0) {
                            container.set(slot, null);
                        } else {
                            container.set(container.getSlotById(item.getId()),
                                    null);
                        }
                    }
                    container.fireItemsChanged();
                }
            }
        } finally {
            container.setFiringEvents(inventoryFiringEvents);
        }
    }

}
