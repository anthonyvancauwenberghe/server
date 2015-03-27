package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveContainer;

public class SaveRunePouch extends SaveContainer {

    public SaveRunePouch(String name) {
            super(name);
        }

    @Override
    public Item[] getContainer(Player player) {
            return player.getRunePouch().toArray();
        }

    @Override
    public void loadItem(Player player, Item item) {
            player.getRunePouch().add(item);
        }

}
