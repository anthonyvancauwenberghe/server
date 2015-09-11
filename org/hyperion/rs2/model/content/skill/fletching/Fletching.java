package org.hyperion.rs2.model.content.skill.fletching;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.crafting.Crafting;
import org.hyperion.rs2.model.content.skill.crafting.LeatherCrafting;

import java.io.FileNotFoundException;

/**
 * Fletching skill handler
 *
 * @author Glis
 */
public class Fletching implements ContentTemplate {

	public static int EXPMULTIPLIER = Constants.XPRATE * 10;

	public Fletching() {
	}

	public static final int[] ARROWS = {882, 884, 886, 888, 890, 892};
	public static final int[] ARROW_HEADS = {39, 40, 41, 42, 43, 44};
	public static final int[] ARROW_LEVELS = {1, 15, 30, 45, 60, 75};
	public static final int[] ARROW_EXPERIENCE = {40, 57, 95, 133, 168, 207};

	public static final int[] DARTS = {806, 807, 808, 809, 810, 811};
	public static final int[] DART_TIPS = {819, 820, 821, 822, 823, 824};
	public static final int[] DART_LEVELS = 	{1, 22, 37, 52, 67, 81};
	public static final int[] DART_EXPERIENCE = {2, 4, 	8, 	12, 15, 19, 25};

	public static int getIntArray(int[] array1, int[] array2, int bow) {
		int a = 0;
		for(int object : array1) {
			if(object == bow) {
				return array2[a];
			}
			a++;
		}
		return - 1;
	}

	public String getStringArray(int[] array1, String[] array2, int bow) {
		int a = 0;
		for(int object : array1) {
			if(object == bow) {
				return array2[a];
			}
			a++;
		}
		return "";
	}

	public int contains(int[] array, int value) {
		for(int i : array) {
			if(i == value)
				return value;
		}
		return 0;
	}

	public boolean isFletchable(Player client, int slot1, int slot2, int useItem, int usedItem) {
		client.getExtraData().put("fletching", true);
		//LogCutting
		if(useItem == 946 && LogCutting.getLog(usedItem) != null) {
			return LogCutting.chooseItem(client, usedItem);
		}
		if(usedItem == 946 && LogCutting.getLog(useItem) != null) {
			return LogCutting.chooseItem(client, useItem);
		}
		//BowStringing
		else if(BowStringing.getString(usedItem) != null) {
			BowStringing.StrungItems items[] = BowStringing.getString(usedItem).getItems();
			for(int i = 0; i < items.length; i++) {
				if(items[i].getItemId() == useItem) {
					return BowStringing.stringBow(client, usedItem, i);
				}
			}
		}
		else if(BowStringing.getString(useItem) != null) {
			BowStringing.StrungItems items[] = BowStringing.getString(useItem).getItems();
			for(int i = 0; i < items.length; i++) {
				if(items[i].getItemId() == usedItem) {
					return BowStringing.stringBow(client, useItem, i);
				}
			}
		}
		//Headless arrows
		 else if(useItem == 52 && usedItem == 314 || useItem == 314 && usedItem == 52) {
			return HeadlessArrows.createHeadlessArrows(client, 52);
		}
		//ArrowMaking
		else if(useItem == 53) {
			ArrowMaking.Arrow item = ArrowMaking.getArrow(usedItem);
			if(item != null)
				ArrowMaking.createArrows(client, usedItem);
		}
		else if(usedItem == 53) {
			ArrowMaking.Arrow item = ArrowMaking.getArrow(useItem);
			if(item != null)
				ArrowMaking.createArrows(client, useItem);
		}
		//DartMaking
		else if(useItem == 314) {
			DartMaking.Dart item = DartMaking.getDart(usedItem);
			if(item != null)
				DartMaking.createDarts(client, usedItem);
		}
		else if(usedItem == 314) {
			DartMaking.Dart item = DartMaking.getDart(useItem);
			if(item != null)
				DartMaking.createDarts(client, useItem);
		}
		return false;
	}

	@Override
	public int[] getValues(int type) {
		if (type == 13) {
			int ai[] = {
					946, 1777, 314, 50, 48, 54, 56, 58, 60, 62,
					64, 66, 68, 70, 72, 39, 40, 41, 42, 43,
					44, 806, 807, 808, 809, 810, 811, 53
			};
			return ai;
		}
		return null;
	}

	public static boolean clickInterface(final Player client, final int id) {
		switch(id) {
			case 8909:
			case 8889:
			case 8949:
			case 8874:
				return LogCutting.startFletching(client, 1, 0);
			case 8913:
			case 8893:
			case 8953:
			case 8878:
				return LogCutting.startFletching(client, 1, 1);
			case 8917:
			case 8897:
			case 8957:
				return LogCutting.startFletching(client, 1, 2);
			case 8921:
			case 8961:
				return LogCutting.startFletching(client, 1, 3);
			case 8965:
				return LogCutting.startFletching(client, 1, 4);

			case 8908:
			case 8888:
			case 8948:
			case 8873:
				return LogCutting.startFletching(client, 5, 0);
			case 8912:
			case 8892:
			case 8952:
			case 8877:
				return LogCutting.startFletching(client, 5, 1);
			case 8916:
			case 8896:
			case 8956:
				return LogCutting.startFletching(client, 5, 2);
			case 8920:
			case 8960:
				return LogCutting.startFletching(client, 5, 3);
			case 8964:
				return LogCutting.startFletching(client, 5, 4);

			case 8907:
			case 8887:
			case 8947:
			case 8872:
				return LogCutting.startFletching(client, 10, 0);
			case 8911:
			case 8891:
			case 8951:
			case 8876:
				return LogCutting.startFletching(client, 10, 1);
			case 8915:
			case 8895:
			case 8955:
				return LogCutting.startFletching(client, 10, 2);
			case 8919:
			case 8959:
				return LogCutting.startFletching(client, 10, 3);
			case 8963:
				return LogCutting.startFletching(client, 10, 4);

			case 8906:
			case 8946:
			case 8886:
			case 8871:
				return LogCutting.startFletching(client, 28, 0);
			case 8910:
			case 8950:
			case 8890:
			case 8875:
				return LogCutting.startFletching(client, 28, 1);
			case 8914:
			case 8954:
			case 8894:
				return LogCutting.startFletching(client, 28, 2);
			case 8918:
			case 8958:
				return LogCutting.startFletching(client, 28, 3);
			case 8962:
				return LogCutting.startFletching(client, 28, 4);
		}
		return false;
	}

	@Override
	public boolean clickObject(final Player player, final int type, final int id, final int slot, final int itemId2, final int itemSlot2) {
		if(type == 13) {
			return isFletchable(player, slot, itemSlot2, id, itemId2);
		}
		if(type == 0) {
			if(player.getExtraData().getBoolean("crafting")) {
				return Crafting.clickInterface(player, id);
			}
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	
	
	/*
	 *work in progress
	 */

	public enum Enchanted_Bolts {

		Bronze_Arrow(new int[]{39, 53}, 882, 1, 40),
		Iron_Arrow(new int[]{40, 53}, 884, 15, 57),
		Steel_Arrow(new int[]{41, 53}, 886, 30, 95),
		Mith_Arrow(new int[]{42, 53}, 888, 45, 132),
		Addy_Arrow(new int[]{43, 53}, 890, 60, 165),
		Rune_Arrow(new int[]{44, 53}, 892, 75, 208),
		Dragon_Arrow(new int[]{11237, 53}, 11212, 75, 255),

		Iron_Bolt(new int[]{9377, 314}, 9140, 39, 2),
		Steel_Bolt(new int[]{9378, 314}, 9141, 46, 3),
		Mith_Bolt(new int[]{9379, 314}, 9142, 53, 5),
		Addy_Bolt(new int[]{9380, 314}, 9143, 61, 7),
		Rune_Bolt(new int[]{9381, 314}, 9144, 69, 10),

		Sapphire_Bolt(new int[]{9142, 9189}, 9240, 56, 5),
		Emerald_Bolt(new int[]{9142, 9190}, 9241, 58, 6),
		Ruby_Bolt(new int[]{9143, 9191}, 9242, 63, 7),
		Diamond_Bolt(new int[]{9143, 9192}, 9243, 65, 8),
		Dragon_Bolt(new int[]{9144, 9193}, 9244, 71, 9),
		Onyx_Bolt(new int[]{9144, 9194}, 9245, 73, 10);

		Enchanted_Bolts(int[] items, int finishId, int level, int xp) {
			this.items = items;
			this.finishId = finishId;
			this.level = level;
			this.xp = xp;
		}

		int[] items;
		int finishId;
		int level;
		int xp;
	}

}
