package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.content.checkers.GameHandler;
import org.hyperion.rs2.net.Packet;

/**
 * Switch item packet handler.
 *
 * @author Graham Edgecombe
 */
public class SwitchItemPacketHandler implements PacketHandler {

	@Override
    public void handle(Player player, Packet packet) {
        final int interfaceId = packet.getLEShortA();
        final int fromTab = packet.getByte() + 88;
        final int fromSlot = packet.getLEShortA();
        int toSlot = packet.getLEShort();
        if(toSlot < 0) {
            toSlot += 15485;
        }
        if (!player.getBankField().isBanking()) {
            player.getInterfaceState().interfaceClosed();
        }
        if ((interfaceId >= -15448) && (interfaceId <= -15440)) {
            if (player.getBankField().isSearching()) {
                return;
            }
            if ((fromTab > 8) || (fromTab < 0)) {
                return;
            }
            int tab = (interfaceId + 15448);
            System.out.println(tab);
            int currentOffset = player.getBankField().getOffset(fromTab);
            int destinationOffset = player.getBankField().getOffset(tab);
            if (tab != fromTab) {
                if (toSlot >= player.getBankField().getTabAmounts()[tab]) {
                    Bank.moveToTab(player, fromSlot, fromTab, tab);
                    return;
                }
                if (player.getBankField().isInserting()) {
                    player.sendMessage("<col=369>Inserting function to be implemented...");
                } else {
                    Bank.swapTabs(player, fromSlot + currentOffset, toSlot + destinationOffset);
                }
                return;
            }
            if (fromSlot == toSlot) {
                return;
            }
            if ((toSlot >= player.getBankField().getTabAmounts()[tab])
                    || (fromSlot >= player.getBankField().getTabAmounts()[tab])) {
                return;
            }
            boolean bankFiringEvents = player.getBank().isFiringEvents();
            player.getBank().setFiringEvents(false);
            if (player.getBankField().isInserting()) {
                Bank.insert(player, fromSlot + currentOffset, toSlot + destinationOffset);
            } else {
                Bank.swap(player, fromSlot + currentOffset, toSlot + destinationOffset);
            }

            player.getBank().setFiringEvents(bankFiringEvents);
            player.getBank().shift();
            return;
        }
        if ((interfaceId >= (-15485)) && (interfaceId <= (-15477))) {
            if (player.getBankField().isSearching()) {
                return;
            }
            int toTab = interfaceId + (15485);
            System.out.println(toTab);
            Bank.moveToTab(player, fromSlot, fromTab, toTab);
            return;
        }
        if ((interfaceId == Inventory.INTERFACE) || (interfaceId == 5064)) {
            if ((fromSlot < 28) && (toSlot < 28)) {
                Item dest = player.getInventory().get(toSlot);
                player.getInventory()
                        .set(toSlot, player.getInventory().get(fromSlot));
                player.getInventory().set(fromSlot, dest);
            }
        }
    }

}
