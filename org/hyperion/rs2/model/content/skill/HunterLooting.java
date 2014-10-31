package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;

/**
 * @author Vegas/Linus/Flux/Jolt/KFC/Tinderbox/Jack Daniels <- Same Person
 */

public class HunterLooting implements ContentTemplate {

	private static final int[][] LowLoots = { // Laagste imps
			{995, 500000}, {1079, 1}, {1093, 1}, {1113, 1}, {1275, 1},
			{4131, 1}, {2491, 1}, {2497, 1}, {2503, 1}, {1333, 1},
			{1319, 1}, {450, 50},};

	private static final int[][] MediumLoots = { // Medium Imps
			{995, 200000}, {9185, 1}, {560, 1000}, {565, 1000}, {561, 1000},
			{811, 50}, {1079, 1}, {1093, 1}, {1113, 1}, {1275, 1},
			{4131, 1}, {4089, 1}, {4091, 1}, {4095, 1}, {4093, 1},
			{4097, 1},};

	private static final int[][] HighLoots = { // Magpie, Pirate , Ninja enzo
			{4151, 1}, {4153, 1}, {995, 500000}, {6524, 1}, {6329, 3},
			{1231, 1}, {892, 100}, {1079, 1}, {1093, 1}, {3385, 1},
			{3387, 1}, {3389, 1}, {3391, 1}, {868, 50}, {4225, 1},};
	private static final int[][] DragonLoots = { // Dragon Imps
			{537, 15}, {535, 50}, {4087, 1}, {4585, 1}, {9244, 50},
			{9144, 100}, {11212, 10}, {1713, 5}, {11732, 1},
			{9245, 10}, {995, 2500000}, {3140, 1}, {10564, 1},
			{4214, 1},};
	private static final int[][] KinglyLoots = { // Kingly Imps
			{15509, 1}, {15503, 1}, {15505, 1}, {15507, 1}, {15511, 1},
			{7158, 1}, {2364, 100}, {995, 10000000}, {3140, 1},
			{9245, 50}, {6738, 2}, {1215, 1}, {11235, 1},
			{892, 500},

	};

	public static void giveLoot(Player p, int itemId) {
		int id = 0;
		int amount = 0;
		int index = 0;
		switch(itemId) {
			case 11238:
			case 11240:
			case 11242:
				index = Misc.random(LowLoots.length - 1);
				id = LowLoots[index][0];
				amount = LowLoots[index][1];
				break;
			case 11244:
			case 11246:
			case 11248:
			case 11250:
				index = Misc.random(MediumLoots.length - 1);
				id = MediumLoots[index][0];
				amount = MediumLoots[index][1];
				break;
			case 11252:
			case 13337:
			case 11254:
				index = Misc.random(HighLoots.length - 1);
				id = HighLoots[index][0];
				amount = HighLoots[index][1];
				break;
			case 11256:
				index = Misc.random(DragonLoots.length - 1);
				id = DragonLoots[index][0];
				amount = DragonLoots[index][1];
				break;
			case 15517:
				index = Misc.random(KinglyLoots.length - 1);
				id = KinglyLoots[index][0];
				amount = KinglyLoots[index][1];
				break;
			default:
				return;
		}
		if(ContentEntity.deleteItem(p, itemId)) {
			ContentEntity.addItem(p, id, amount);
		}
	}

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c,
	                           int d) {
		if(type == 1) {
			giveLoot(player, a);
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	@Override
	public int[] getValues(int type) {
		if(type == 1 || type == 15) {
			int[] jars = {11238, 11240, 11242, 11244, 11246, 11248, 11250,
					11252, 11254, 11256, 15517, 13337};
			return jars;
		}
		return null;
	}

}
