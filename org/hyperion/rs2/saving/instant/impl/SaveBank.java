package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.saving.instant.SaveContainer;

public class SaveBank extends SaveContainer {

    /**
     * Constructs a new SaveBank.
     *
     * @param name
     */
    public SaveBank(final String name) {
        super(name);
    }

    @Override
    public Container getContainer(final Player player) {
        return player.getBank();
    }


    @Override
    public void loadItem(final Player player, final Item item, final int slot) {
        if(this.transferedPkItem(item)){
            player.getBank().add(item.toBankItem(0));
            return;
        }
        final int pkp = this.getPkValue(item);
        final int tickets = pkp / 130;
        if(tickets > 0){
            player.getBank().add(new Item(5020, tickets));
        }
        if(ItemSpawning.canSpawn(item.getId()))
            player.getBank().add(item.toBankItem(0));
    }

}
