package org.hyperion.rs2.model.content.skill;
//Shard Revolutions Generic MMORPG Server
//Copyright (C) 2008  Graham Edgecombe

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

/**
 * Fletching skill handler
 *
 * @author Jonas
 */
public class Fletching implements ContentTemplate {

	private static int EXPMULTIPLIER = Constants.XPRATE * 10;

	public Fletching() {
	}
	//TODO: recode whole shit
	//TODO: add make x

	public final int[] LOGS = {1511, 1521, 1519, 1517, 1515, 1513};
	public final int[] UNSTRUNG_BOWS = {50, 48, 54, 56, 60, 58, 64, 62, 68, 66, 72, 70};
	public final int[] STRUNG_BOWS = {841, 839, 843, 845, 849, 847, 853, 851, 857, 855, 861, 859,};
	public final int[] FLETCHING_LEVELS = {5, 10, 20, 25, 35, 40, 50,
			55, 65, 70, 80, 85};
	public final int[] EXPERIENCE = {10, 20, 33, 50, 66, 83, 100,
			117, 133, 150, 167, 183};

	public final int[] ARROWS = {882, 884, 886, 888, 890, 892};
	public final int[] ARROW_HEADS = {39, 40, 41, 42, 43, 44};
	public final int[] ARROW_LEVELS = {1, 15, 30, 45, 60, 75};
	public final int[] ARROW_EXPERIENCE = {40, 57, 95, 133, 168, 207};

	public final int[] DARTS = {806, 807, 808, 809, 810, 811};
	public final int[] DART_TIPS = {819, 820, 821, 822, 823, 824};
	public final int[] DART_LEVELS = {1, 22, 37, 52, 67, 81};
	public final int[] DART_EXPERIENCE = {2, 4, 8, 12, 15, 19, 25};

	public final int[] LEFT_ITEM = {50, 54, 60, 64, 68, 72};
	public final int[] RIGHT_ITEM = {48, 56, 58, 62, 66, 70};
	public String[] LEFT_ITEM_NAME = {"Longbow(u)", "Oak Longbow(u)",
			"Willow Longbow(u)", "Maple Longbow(u)", "Yew Longbow(u)",
			"Magic Longbow(u)"};
	public String[] RIGHT_ITEM_NAME = {"Shortbow(u)",
			"Oak Shortbow(u)", "Willow Shortbow(u)", "Maple Shortbow(u)",
			"Yew Shortbow(u)", "Magic Shortbow(u)"};

	public final int FLETCHING_DELAY = 2500;

	// private int logId = 0;

	public int getLogId(Player client) {
		if(client.getExtraData().get("logId") == null) {
			return - 1;
		}
		int log = (Integer) client.getExtraData().get("logId");
		return log;
	}

	public void setLogId(Player client, int id) {
		client.getExtraData().put("logId", (Integer) id);
	}

	public int getIntArray(int[] array1, int[] array2, int bow) {
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
		client.getExtraData().put("crafting", 2);
		if(useItem == 946 && usedItem == contains(LOGS, usedItem)
				|| useItem == contains(LOGS, useItem) && usedItem == 946) {
			return chooseItem(client,
					(useItem == contains(LOGS, useItem) ? useItem : usedItem));
		} else if(useItem == 1777
				&& usedItem == contains(UNSTRUNG_BOWS, usedItem)
				|| useItem == contains(UNSTRUNG_BOWS, useItem)
				&& usedItem == 1777) {
			return stringBow(client, (useItem == contains(UNSTRUNG_BOWS,
					useItem) ? useItem : usedItem));
		} else if(useItem == 52 && usedItem == 314 || useItem == 314
				&& usedItem == 52) {
			return createHeadlessArrows(client, 52);
		} else if(useItem == 53 && usedItem == contains(ARROW_HEADS, usedItem)
				|| useItem == contains(ARROW_HEADS, useItem) && usedItem == 53) {
			return createArrows(client, (useItem == contains(ARROW_HEADS,
					useItem) ? useItem : usedItem));
		} else if(useItem == 314 && usedItem == contains(DART_TIPS, usedItem)
				|| useItem == contains(DART_TIPS, useItem) && usedItem == 314) {
			return createDarts(client,
					(useItem == contains(DART_TIPS, useItem) ? useItem
							: usedItem));
		} else
			return false;
	}

	public boolean createHeadlessArrows(Player client, int item) {

		if(client.isBusy()) {
			return true;
		}

		int amount = ContentEntity.getItemAmount(client, item);

		if(ContentEntity.freeSlots(client) >= 1) {
			int am2 = ContentEntity.getItemAmount(client, 314);
			if(am2 < amount)
				amount = am2;
			ContentEntity.deleteItemA(client, 314,
					amount > 15 ? 15 : amount);
			ContentEntity.deleteItemA(client, item,
					amount > 15 ? 15 : amount);
			ContentEntity.addItem(client, 53, amount > 15 ? 15 : amount);
			ContentEntity.addSkillXP(client, 15 * EXPMULTIPLIER,
					9);
			ContentEntity.sendMessage(client,
					"You make some headless arrows.");
		} else {
			ContentEntity.sendMessage(client,
					"You have no space in your inventory");
		}
		return true;
	}

	public boolean createDarts(Player client, int item) {

		if(client.isBusy()) {
			return true;
		}

		int amount = ContentEntity.getItemAmount(client, item);

		if(ContentEntity.freeSlots(client) >= 1) {
			if(ContentEntity.returnSkillLevel(client, 9) >= getIntArray(
					DART_TIPS, DART_LEVELS, item)) {
				int am2 = ContentEntity.getItemAmount(client, 314);
				if(am2 < amount)
					amount = am2;
				ContentEntity.deleteItemA(client, 314,
						amount > 15 ? 15 : amount);
				ContentEntity.deleteItemA(client, item,
						amount > 15 ? 15 : amount);
				ContentEntity.addItem(client,
						getIntArray(DART_TIPS, DARTS, item),
						amount > 15 ? 15 : amount);
				ContentEntity.addSkillXP(client,
						getIntArray(DART_TIPS, DART_EXPERIENCE, item) * EXPMULTIPLIER,
						9);
				ContentEntity.sendMessage(client, "You make some darts.");
			} else {
				ContentEntity.sendMessage(client,
						"You need a fletching level of" + " "
								+ getIntArray(DART_TIPS, DART_LEVELS, item)
								+ " to make these darts.");
			}
		} else {
			ContentEntity.sendMessage(client,
					"You have no space in your inventory");
		}
		return true;
	}

	public boolean createArrows(Player client, int item) {

		if(client.isBusy()) {
			return true;
		}

		int amount = ContentEntity.getItemAmount(client, item);

		if(ContentEntity.freeSlots(client) >= 1) {
			if(ContentEntity.returnSkillLevel(client, 9) >= getIntArray(
					ARROW_HEADS, ARROW_LEVELS, item)) {
				int am2 = ContentEntity.getItemAmount(client, 53);
				if(am2 < amount)
					amount = am2;
				ContentEntity.deleteItemA(client, 53,
						amount > 15 ? 15 : amount);
				ContentEntity.deleteItemA(client, item,
						amount > 15 ? 15 : amount);
				ContentEntity.addItem(client,
						getIntArray(ARROW_HEADS, ARROWS, item), amount > 15 ? 15 : amount);
				ContentEntity.addSkillXP(client,
						getIntArray(ARROW_HEADS, ARROW_EXPERIENCE, item) * EXPMULTIPLIER,
						9);
				ContentEntity
						.sendMessage(client, "You make some arrows.");
			} else {
				ContentEntity.sendMessage(client,
						"You need a fletching level of" + " "
								+ getIntArray(ARROW_HEADS, ARROW_LEVELS, item)
								+ " to make these arrows.");
			}
		} else {
			ContentEntity.sendMessage(client,
					"You have no space in your inventory");
		}
		return true;
	}

	public boolean stringBow(Player client, int bow) {

		if(client.isBusy()) {
			return true;
		}
		if(ContentEntity.returnSkillLevel(client, 9) >= getIntArray(
				UNSTRUNG_BOWS, FLETCHING_LEVELS, bow)) {
			ContentEntity.startAnimation(client, 1248);
			ContentEntity.deleteItemA(client, bow, 1);
			ContentEntity.deleteItemA(client, 1777, 1);
			ContentEntity.addItem(client,
					getIntArray(UNSTRUNG_BOWS, STRUNG_BOWS, bow), 1);
			ContentEntity.sendMessage(client,
					"You attach the bowstring to the bow.");
			ContentEntity.addSkillXP(client, (getIntArray(UNSTRUNG_BOWS, EXPERIENCE, bow) * EXPMULTIPLIER), Skills.FLETCHING);
		} else {
			ContentEntity.sendMessage(client,
					"You need a fletching level of" + " "
							+ getIntArray(UNSTRUNG_BOWS, FLETCHING_LEVELS, bow)
							+ " to string that bow.");
		}
		return true;
	}

	public boolean chooseItem(Player client, int log) {
		if(client.isBusy()) {
			return true;
		}

		ContentEntity.removeAllWindows(client);
		client.getActionSender().sendPacket164(8880);

		ContentEntity.sendInterfaceModel(client, 8883, 200,
				getIntArray(LOGS, LEFT_ITEM, log));
		ContentEntity.sendInterfaceModel(client, 8884, 200,
				(log == 1511 ? 52 : - 1));
		ContentEntity.sendInterfaceModel(client, 8885, 200,
				getIntArray(LOGS, RIGHT_ITEM, log));

		ContentEntity.sendString(client,
				getStringArray(LOGS, LEFT_ITEM_NAME, log), 8897);
		ContentEntity.sendString(client, log == 1511 ? "Arrow Shaft" : "",
				8893);
		ContentEntity.sendString(client,
				getStringArray(LOGS, RIGHT_ITEM_NAME, log), 8889);

		setLogId(client, log);
		return true;
	}

	public boolean startFletching(final Player client, final int amount,
	                              final String length) {
		if(client.isBusy()) {
			return true;
		}
		client.setBusy(true);
		client.getActionSender().removeAllInterfaces();
		ContentEntity.startAnimation(client, 1248);
		client.inAction = true;
		World.getWorld().submit(new Event(FLETCHING_DELAY) {
			int amountLeft = amount;
			int log2 = getLogId(client);
			int unstrungBow = 0;

			public void stop2() {
				ContentEntity.startAnimation(client, - 1);
				client.setBusy(false);
				client.getExtraData().remove("logId");
				this.stop();
			}

			@Override
			public void execute() {
				if(! client.inAction) {
					stop2();
					return;
				}
				if(amountLeft == 0 || log2 == - 1) {
					stop2();
					return;
				}
				ContentEntity.startAnimation(client, 1248);
				if(length.equals("shortbow")) {
					unstrungBow = getIntArray(LOGS, LEFT_ITEM, log2);
				} else if(length.equals("longbow")) {
					unstrungBow = getIntArray(LOGS, RIGHT_ITEM, log2);
				} else {
					unstrungBow = 52;
				}

				if(ContentEntity.freeSlots(client) >= 0) {
					if(ContentEntity.getItemAmount(client, log2) > 0) {
						if(ContentEntity.returnSkillLevel(client, 9) >= getIntArray(
								UNSTRUNG_BOWS, FLETCHING_LEVELS, unstrungBow)) {

							ContentEntity.deleteItemA(client, log2, 1);
							ContentEntity.addItem(client,
									unstrungBow == 52 ? 52 : unstrungBow,
									unstrungBow == 52 ? 15 : 1);
							ContentEntity.addSkillXP(client,
									unstrungBow == 52 ? 50 : getIntArray(
											UNSTRUNG_BOWS, EXPERIENCE,
											unstrungBow) * EXPMULTIPLIER,
									9);
							ContentEntity.sendMessage(client,
									"You fletch the bow.");
							amountLeft--;

						} else {
							ContentEntity.sendMessage(client,
									"You need a fletching level of"
											+ " "
											+ getIntArray(UNSTRUNG_BOWS,
											FLETCHING_LEVELS,
											unstrungBow)
											+ " to fletch that bow.");
							stop2();
						}
					} else {
						ContentEntity.sendMessage(client,
								"You don't have the item to fletch");
						stop2();
					}
				} else {
					ContentEntity.sendMessage(client,
							"You have no space in your inventory");
					stop2();
				}
			}
		});
		return true;
	}

	@Override
	public int[] getValues(int type) {
		if(type == 13) {
			int ai[] = {
					946, 1777, 314, 50, 48, 54, 56, 58, 60, 62,
					64, 66, 68, 70, 72, 39, 40, 41, 42, 43,
					44, 806, 807, 808, 809, 810, 811, 53
			};
			return ai;
		}
		if(type == 0) {
			int ai1[] = {
					8897, 8896, 8895, 8894, 8893, 8892, 8891, 8890, 8889, 8888,
					8887, 8886
			};
			return ai1;
		} else {
			return null;
		}
	}

	@Override
	public boolean clickObject(final Player player, final int type, final int id, final int slot, final int itemId2, final int itemSlot2) {
		if(type == 13) {
			return isFletchable(player, slot, itemSlot2, id, itemId2);
		}
		if(type == 0) {
			if((Integer) player.getExtraData().get("crafting") == 1) {
				switch(id) {

					case 8889:
						return Crafting.startAgain(player, 1, 0);
					case 8897:
						return Crafting.startAgain(player, 1, 2);
					case 8888:
						return Crafting.startAgain(player, 5, 0);
					case 8896:
						return Crafting.startAgain(player, 5, 2);
					case 8887:
						return Crafting.startAgain(player, 10, 0);
					case 8895:
						return Crafting.startAgain(player, 10, 2);
					case 8893:
						return Crafting.startAgain(player, 1, 1);
					case 8892:
						return Crafting.startAgain(player, 5, 1);
					case 8891:
						return Crafting.startAgain(player, 10, 1);

				}
			}
			switch(id) {
				case 8889:
					startFletching(player, 1, "shortbow");
					return true;

				case 8897:
					startFletching(player, 1, "longbow");
					return true;

				case 8888:
					startFletching(player, 5, "shortbow");
					return true;

				case 8896:
					startFletching(player, 5, "longbow");
					return true;

				case 8887:
					startFletching(player, 10, "shortbow");
					return true;

				case 8895:
					startFletching(player, 10, "longbow");
					return true;

				case 8893:
					startFletching(player, 1, "shaft");
					setLogId(player, 52);
					return true;

				case 8892:
					startFletching(player, 5, "shaft");
					setLogId(player, 52);
					return true;

				case 8891:
					startFletching(player, 10, "shaft");
					setLogId(player, 52);
					return true;
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
