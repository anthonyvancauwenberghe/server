package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

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
			int index = - 1;
			for(int i = 0; i < combineItems.length; i++) {
				if((id == combineItems[i][0] && itemId2 == combineItems[i][1]) || (id == combineItems[i][1] && itemId2 == combineItems[i][0])) {
					index = i;
					break;
				}
			}
			if(index != - 1) {
                if (player.getInventory().contains(id) || player.getInventory().contains(itemId2))
	                return false;
							ContentEntity.deleteItem(player, id, slot, 1);
				ContentEntity.deleteItem(player, itemId2, itemSlot2, 1);
				ContentEntity.addItem(player, combineItems[index][2]);
				return true;
			}
			return false;

		}
		if(type == 22) {
			if(System.currentTimeMillis() - player.splitDelay < 2000) {
				player.getActionSender().sendMessage("You can't split that fast!");
			}
			if(ContentEntity.freeSlots(player) < 1) {
				ContentEntity.sendMessage(player, "You don't have enough space for this");
			}
			for(int i = 0; i < combineItems.length; i++) {
				if(id == combineItems[i][2] && player.getInventory().getCount(id) > 0) {
					player.splitDelay = System.currentTimeMillis();
					ContentEntity.deleteItem(player, id, slot, 1);
					ContentEntity.addItem(player, combineItems[i][0]);
					ContentEntity.addItem(player, combineItems[i][1]);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {

	}

	@Override
	public int[] getValues(int type) {
		if(type == 13) {
			int[] j = new int[combineItems.length * 2];
			int i2 = 0;
			for(int i = 0; i < combineItems.length; i++) {
				j[i2++] = combineItems[i][0];
				j[i2++] = combineItems[i][1];
			}
			return j;
		}

		if(type == 22) {
			int[] j = new int[combineItems.length];
			int i2 = 0;
			for(int i = 0; i < combineItems.length; i++) {
				j[i2++] = combineItems[i][2];
			}
			return j;
		}
		return null;
	}

}
