package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.saving.instant.SaveContainer;

public class SaveInventory extends SaveContainer {

    public SaveInventory(final String name) {
        super(name);
    }

    @Override
    public Container getContainer(final Player player) {
        return player.getInventory();
    }

    @Override
    public void loadItem(final Player player, final Item item, final int slot) {
        if(this.transferedPkItem(item)){
            if(slot >= 0){
                player.getInventory().set(slot, item);
            }else{
                player.getInventory().add(item);
            }
            return;
        }
        final int pkp = this.getPkValue(item);
        final int tickets = pkp / 130;
        if(tickets > 0){
            player.getBank().add(new Item(5020, tickets));
        }
        if(ItemSpawning.canSpawn(item.getId())){
            if(slot >= 0){
                player.getInventory().set(slot, item);
            }else{
                player.getInventory().add(item);
            }
        }
    }

}
