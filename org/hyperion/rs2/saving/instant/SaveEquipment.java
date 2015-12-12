package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

public class SaveEquipment extends SaveContainer {

    public SaveEquipment(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Container getContainer(final Player player) {
        return player.getEquipment();
    }

    @Override
    public void loadItem(final Player player, final Item item, int slot) {
        if(this.transferedPkItem(item)){
            slot = Equipment.getType(item).getSlot();
            player.getEquipment().set(slot, item);
            return;
        }
        final int pkp = this.getPkValue(item);
        final int tickets = pkp / 130;
        if(tickets > 0){
            player.getBank().add(new Item(5020, tickets));
        }
        if(ItemSpawning.canSpawn(item.getId())){
            slot = Equipment.getType(item).getSlot();
            player.getEquipment().set(slot, item);
        }
    }

}
