package org.hyperion.rs2.model.content.misc2;

import com.google.common.collect.Maps;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CombineItems implements ContentTemplate {

	private static final int[][] combineItems = {
			{6585, 19333, 19335}, //fury (or)
			{11335, 19346, 19336}, //Dragon full (or)
			{4087, 19348, 19338}, //D legs (or)
			{4585, 19348, 19339}, //d skirt (or)
			{6617, 19350, 19337}, // Dragon plate (or)
			{1187, 19352, 19340}, //d sq (or)
			{11335, 19354, 19341}, //Dragon full (sp)
			{4087, 19356, 19343}, //d legs (sp)
			{4585, 19356, 19344}, //d skirt (sp)
			{6617, 19358, 19342}, //Dragon plate (sp)
			{1187, 19360, 19345}, //d sq (sp)
	};

	@Override
	public boolean clickObject(Player player, final int type, final int id, final int slot, final int itemId2, final int itemSlot2) {
		if(type == 13) {
			int index = -1;
			for(int array = 0; array < combineItems.length; array++) {
				if((id == combineItems[array][0] && itemId2 == combineItems[array][1]) || (id == combineItems[array][1] && itemId2 == combineItems[array][0])) {
					index = array;
					break;
				}
			}
			if(index != - 1) {
                if (!player.getInventory().contains(id))
	                return false;
				final Item primary = Item.create(id);
				final Item secondary = Item.create(itemId2);
				final Item product = Item.create(combineItems[index][2]);
				player.getInventory().remove(primary);
				player.getInventory().remove(secondary);
				player.getInventory().add(product);
				return true;
			}
			return false;
		}
		if(type == 22) {
			if(ContentEntity.freeSlots(player) < 2) {
				ContentEntity.sendMessage(player, "You need at least 2 free Inventory spaces for this.");
                return false;
			}
			for (int[] array : combineItems) {
				if (id == array[2]) {
					final Item product = Item.create(id);
					final Item primary = Item.create(array[0]);
					final Item secondary = Item.create(array[1]);
					if (player.getInventory().hasItem(product)) {
						player.getInventory().remove(product);
						player.getInventory().add(primary);
						player.getInventory().add(secondary);
					}
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	@Override
	public int[] getValues(int type) {
		if(type == 13) {
			int[] array = new int[combineItems.length * 2];
			int index = 0;
			for(int i = 0; i < combineItems.length; i++) {
				array[index++] = combineItems[i][0];
				array[index++] = combineItems[i][1];
			}
			return array;
		}
		if(type == 22) {
			int[] array = new int[combineItems.length];
			int index = 0;
			for(int i = 0; i < combineItems.length; i++) {
				array[index++] = combineItems[i][2];
			}
			return array;
		}
		return null;
	}

}
