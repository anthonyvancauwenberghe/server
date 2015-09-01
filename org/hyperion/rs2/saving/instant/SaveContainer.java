package org.hyperion.rs2.saving.instant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.SpiritShields;
import org.hyperion.rs2.model.container.Container;

public abstract class SaveContainer extends SaveObject {

	public static final int[][] PRICES = {
			{13899, 5000},
			{15486, 50},
			{6570, 500},
			{13896, 2500},
			{13902, 5000},
			{15243, 1},
			{13890, 2500},
			{13893, 2500},
			{11663, 1000},
			{11664, 1000},
			{11665, 1000},
			{19111, 5000},
			{8847, 300},
			{8846, 200},
			{8845, 100},
			{8842, 500},
			{15272, 1},
			{8840, 750},
			{8839, 750},
			{18333, 1500},
			{13884, 2500},
			{10547, 750},
			{13887, 2500},
			{10551, 1000},
			{10550, 750},
			{10549, 750},
			{10548, 750},
			{8850, 600},
			{8848, 400},
			{8849, 500},
			{16293,20000},
			{16403,250000},
			{15773,75000},
			{17039,75000},
			{17143,75000},
			{16837,50000},
			{16667,20000},
			{18347,200000},
			{18361,20000}
	};

	public int getPkValue(Item item) {
		for(int[] data: PRICES) {
			int id = data[0];
			int price = data[1];
			if(item.getId() == id) {
				return price * item.getCount();
			}
		}
		return 0;
	}

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
		Item[] items = getContainer(player).toArray();
		for(int i = 0; i < items.length; i++) {
			Item item = items[i];
			if(item != null) {
				writer.write(item.getId() + " " + item.getCount() + " " + i);
				writer.newLine();
			}
		}
		return true;
	}

    @Override
    public void load(Player player, String values, BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()).length() > 0) {
			
			String[] parts = line.split(" ");
			int id = Integer.parseInt(parts[0]);
			int amount = DEFAULT_ITEM_AMOUNT;
			int slot = -1;
			if (parts.length > 1) {
				amount = Integer.parseInt(parts[1]);
			}
			if(parts.length > 2) {
				slot = Integer.parseInt(parts[2]);
			}
			boolean duped = false;
			if (id >= 1038 && id <= 1058)
				duped = true;
			switch (id) {
				case 14484:
				case 19713:
				case 19714:
				case 19715:
				case 16909:
				case SpiritShields.DIVINE_SPIRIT_SHIELD_ID:
					duped = true;
			}
			if (duped && amount > 20)
				amount = 20;
			Item nextItem = new Item(id, amount);
			
			
			loadItem(player, nextItem, slot);
		}
		getContainer(player).updatePreviousItems();
	}



	public static Item lineToItem(String line) {
		String[] parts = line.split(" ");
		int id = Integer.parseInt(parts[0]);
		int amount = DEFAULT_ITEM_AMOUNT;
		int slot = -1;
		if (parts.length > 1) {
			amount = Integer.parseInt(parts[1]);
		}
		if(parts.length > 2) {
			slot = Integer.parseInt(parts[2]);
		}
		boolean duped = false;
		if (id >= 1038 && id <= 1058)
			duped = true;
		switch (id) {
			case 14484:
			case 19713:
			case 19714:
			case 19715:
			case 16909:
			case SpiritShields.DIVINE_SPIRIT_SHIELD_ID:
				duped = true;
		}
		if (duped && amount > 20)
			amount = 20;
		return new Item(id, amount);
	}

	public abstract Container getContainer(Player player);

	public abstract void loadItem(Player player, Item item, int slot);
}
