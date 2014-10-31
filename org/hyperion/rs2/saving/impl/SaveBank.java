package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveContainer;

public class SaveBank extends SaveContainer {

	/**
	 * Constructs a new SaveBank.
	 *
	 * @param name
	 */
	public SaveBank(String name) {
		super(name);
	}

	@Override
	public Item[] getContainer(Player player) {
		return player.getBank().toArray();
	}

	@Override
	public void loadItem(Player player, Item item) {
		player.getBank().add(item);
	}

}
