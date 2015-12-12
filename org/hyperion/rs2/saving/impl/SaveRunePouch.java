package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveContainer;

public class SaveRunePouch extends SaveContainer {

    public SaveRunePouch(final String name) {
        super(name);
    }

    @Override
    public Item[] getContainer(final Player player) {
        return player.getRunePouch().toArray();
    }

    @Override
    public void loadItem(final Player player, final Item item) {
        player.getRunePouch().add(item);
    }

}
