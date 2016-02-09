package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveContainer;

public class SaveInventory extends SaveContainer {

	public SaveInventory(String name) {
		super(name);
	}

	@Override
	public Item[] getContainer(Player player) {
		return player.getInventory().toArray();
	}

	@Override
	public void loadItem(Player player, Item item) {
		player.getInventory().add(item);
	}

}
