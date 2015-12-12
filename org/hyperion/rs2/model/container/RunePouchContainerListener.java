package org.hyperion.rs2.model.container;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.misc2.RunePouch;

/**
 * Created by User on 3/22/2015.
 */
public class RunePouchContainerListener implements ContainerListener {

    private final Player player;

    public RunePouchContainerListener(final Player player) {
        this.player = player;
    }

    @Override
    public void itemChanged(final Container container, final int slot) {
        player.getActionSender().sendUpdateItem(RunePouch.RUNE_INTERFACE, slot, container.get(slot));
        player.getActionSender().sendUpdateItems(RunePouch.INVENTORY_INTERFACE, player.getInventory().toArray());
    }

    @Override
    public void itemsChanged(final Container container, final int[] slots) {

    }

    @Override
    public void itemsChanged(final Container container) {
        player.getActionSender().sendUpdateItems(RunePouch.RUNE_INTERFACE, container.toArray());
        player.getActionSender().sendUpdateItems(RunePouch.INVENTORY_INTERFACE, player.getInventory().toArray());
    }

}
