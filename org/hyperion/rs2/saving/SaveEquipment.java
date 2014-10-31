package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;

public class SaveEquipment extends SaveContainer {

	public SaveEquipment(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Item[] getContainer(Player player) {
		return player.getEquipment().toArray();
	}

	@Override
	public void loadItem(Player player, Item item) {
		int slot = Equipment.getType(item).getSlot();
		player.getEquipment().set(slot, item);
	}

}
