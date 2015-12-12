package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;

public class SaveEquipment extends SaveContainer {

    public SaveEquipment(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Item[] getContainer(final Player player) {
        return player.getEquipment().toArray();
    }

    @Override
    public void loadItem(final Player player, final Item item) {
        final int slot = Equipment.getType(item).getSlot();
        player.getEquipment().set(slot, item);
    }

}
