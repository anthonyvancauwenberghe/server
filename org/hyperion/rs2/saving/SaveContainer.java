package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.SpiritShields;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class SaveContainer extends SaveObject {

	/**
	 * The default item amount.
	 */
	public static final int DEFAULT_ITEM_AMOUNT = 1;

	/**
	 * The default item id.
	 */
	public static final int DEFAULT_ITEM_ID = 1;

	/**
	 * Constructs a new SaveContainer.
	 *
	 * @param name
	 */
	public SaveContainer(String name) {
		super(name);
	}

	@Override
	public boolean save(Player player, BufferedWriter writer) throws IOException {
		writer.write(getName());
		writer.newLine();
		for(Item item : getContainer(player)) {
			if(item == null)
				continue;
			writer.write(item.getId() + " " + item.getCount());
			writer.newLine();
		}
		return true;
	}

	public void load(Player player, String values, BufferedReader reader) throws IOException {
		String line;
		while((line = reader.readLine()).length() > 0) {
			Item nextItem = lineToItem(line);
			loadItem(player, nextItem);
		}
	}

	public void load(Player player, String values, BufferedReader reader, boolean debug) throws IOException {
		String line;
		while((line = reader.readLine()).length() > 0) {
			Item nextItem = lineToItem(line);
			loadItem(player, nextItem);
		}
	}

	public static Item lineToItem(String line) {
		String[] parts = line.split(" ");
		int id = Integer.parseInt(parts[0]);
		int amount = DEFAULT_ITEM_AMOUNT;
		if(parts.length > 1) {
			amount = Integer.parseInt(parts[1]);
		}
		boolean duped = false;
		if(id >= 1038 && id <= 1058)
			duped = true;
		switch(id) {
			case 14484:
			case 19713:
			case 19714:
			case 19715:
			case 16909:
			case SpiritShields.DIVINE_SPIRIT_SHIELD_ID:
				duped = true;
		}
		if(duped && amount > 20)
			amount = 20;
		return new Item(id, amount);
	}

	public abstract Item[] getContainer(Player player);

	public abstract void loadItem(Player player, Item item);
}
