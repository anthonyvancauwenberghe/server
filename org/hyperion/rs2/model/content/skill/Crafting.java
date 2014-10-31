package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;


/**
 * Handles crafting
 *
 * @author Jonas(++)
 * @author madturnip - martin
 */

public class Crafting implements ContentTemplate {

	public Crafting() {
	}

	private final static int GEM_ANIM = 2269,
			CRAFT_ANIM = 2269;

	private final static int EXPMULTIPLIER = 3 * Constants.XPRATE;

	public final static int[] uncuts = {1623, 1621, 1619, 1617, 1631, 1625, 1627, 1629,/*bolts*/1607, 1605, 1603, 1601, 1615, 1609, 1611, 1613,};
	public final static int[] cut = {1607, 1605, 1603, 1601, 1615, 1609, 1611, 1613,/*bolts*/9189, 9190, 9191, 9192, 9193, 9187, 45, 9188,};
	public final static int[] cutLevel = {20, 27, 34, 43, 55, 23, 25, 30,/*bolts*/1, 20, 30, 40, 60, 20, 25, 30,};
	public final static int[] cutXp = {40, 65, 80, 85, 100, 70, 75, 80,/*bolts*/40, 50, 60, 90, 120, 90, 70, 80};

	public boolean attemptCut(Player c, int useItem, int slot1, int onItem, int slot2) {
		if(onItem == 1755) {
			onItem = useItem;
			slot2 = slot1;
			useItem = 1755;
		}
		if(onItem == 1733) {
			onItem = useItem;
			useItem = 1733;
		}
		if(useItem == 1755)
			return cutGem(c, onItem, slot2);
		else if(useItem == 1733)
			return craftLeather(c, onItem);
		return false;
	}

	public int getGem(int i) {
		for(int k = 0; k < uncuts.length; k++) {
			if(uncuts[k] == i)
				return k;
		}
		return - 1;
	}

	public boolean cutGem(Player c, int gem, int slot) {
		int g = getGem(gem);
		if(g == - 1) {
			ContentEntity.sendMessage(c, "You cannot cut this item.");
			return true;
		}
		String name = ContentEntity.getItemName(gem);

		// Check if the player can cut the gem.
		if(ContentEntity.returnSkillLevel(c, 12) < cutLevel[g]) {
			ContentEntity.sendMessage(c, "Your Crafting level is not high enough to craft this.");
			return true;
		}

		ContentEntity.startAnimation(c, GEM_ANIM);

		ContentEntity.sendMessage(c, "You cut the " + name + ".");
		ContentEntity.deleteItem(c, uncuts[g], slot);
		if(g >= 8)
			ContentEntity.addItem(c, cut[g], 15);
		else
			ContentEntity.addItem(c, cut[g], 1);


		ContentEntity.addSkillXP(c, cutXp[g] * EXPMULTIPLIER, 12);//look this one has params(c,stuff)

		return true;
	}

	/**
	 * Crafting any kind of leather with a needle.
	 *
	 * @param c    The {@link Player}.
	 * @param item The leather.
	 */

	public boolean craftLeather(final Player c, final int item) {
		try {
			int l = getLeather(item);
			if(l == - 1) {
				ContentEntity.sendMessage(c, "You cannot craft this item");
				return true;
			}
			if(ContentEntity.returnSkillLevel(c, 12) < leatherLevel[l][0]) {
				ContentEntity.sendMessage(c, "Your Crafting level is not high enough to craft this.");
				return true;
			}
			c.getExtraData().put("crafting", 1);
			c.getExtraData().put("craftingItem", l);
			c.setBusy(true);
			int[] items = finishItem[l];
			int index = 0;
			for(int k : frameId[items.length]) {
				if(index == 0) {
					ContentEntity.sendString(c, "What do you want to make?", k);
				} else if(index >= (items.length * 2 + 1)) {
					c.getActionSender().sendPacket164(k);
				} else if(index >= (items.length + 1)) {
					ContentEntity.sendInterfaceModel(c, k, 250, items[(index - items.length - 1)]);
				} else {
					ContentEntity.sendString(c, ContentEntity.getItemName(items[index - 1]), k);
				}
				index++;
			}
			return true;
	    /*
        sendFrame126("What do you want to make?", 8879);
		sendFrame246(8870, 250, longbows[id]); // right picture
		sendFrame246(8869, 250, shortbows[id]); // left picture
			sendFrame126(getItemName(shortbows[id]), 8871);
			sendFrame126(getItemName(shortbows[id]), 8874);
			sendFrame126(getItemName(longbows[id]), 8878);
			sendFrame126(getItemName(longbows[id]), 8875);
		sendFrame164(8866);*/
		
		/*
		sendString("What would you like to make?", 8898);
		sendString("Vambraces", 8889);
		sendString("Chaps", 8893);
		sendString("Body", 8897);
		sendFrame246(8883, 250, gloves[i]);
		sendFrame246(8884, 250, legs[i]);
		sendFrame246(8885, 250, chests[i]);
		sendFrame164(8880);
		*/
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static final int[][] unenchanted = {
			{1637, 1687,},//sats
			{1639, 1688,},//emralds
			{1641, 1689,},//rubys
			{1643, 1690,},//diamonds
			{1645, 1691,},//dragonstone
			{6575, 6581,},//onyx
	};

	private static final int[][] enchanted = {
			{2550, 1727,},//sats
			{2552, 1729,},//emralds
			{2568, 1725,},//rubys
			{2570, 1731,},//diamonds
			{2572, 1704,},//dragonstone
			{6583, 6585,},//onyx
	};

	private static final int[][] gfxEnchant = {
			{238, 114,},//sats
			{238, 115,},//emralds
			{238, 116,},//rubys
			{238, 153,},//diamonds
			{238, 154,},//dragonstone
			{238, 452,},//onyx
	};

	private static final int[] levelEnchant = {7, 27, 49, 57, 68, 87,};

	public boolean enchant(final Player player, int spell, int item, int slot) {
		int i = 0;
		if(unenchanted[spell][0] != item && unenchanted[spell][1] != item) {
			player.getActionSender().sendMessage("You cannot enchant this item.");
			return false;
		}
		if(levelEnchant[spell] > player.getSkills().getLevel(6)) {
			player.getActionSender().sendMessage("You need a magic level of " + levelEnchant[spell] + " to enchant this item.");
			return false;
		}
		if(unenchanted[spell][0] != item)
			i = 1;
		ContentEntity.playerGfx(player, gfxEnchant[spell][i]);//722
		if(i == 1)
			ContentEntity.startAnimation(player, 719);
		else
			ContentEntity.startAnimation(player, 727);

		ContentEntity.deleteItem(player, item, slot);
		ContentEntity.addItem(player, enchanted[spell][i], 1);
		return true;
	}
	
	
	/*
	 * rings
	 * Tab.anIntArray240[0] = 4233;
		Tab.anIntArray240[1] = 4246;
		Tab.anIntArray240[2] = 4247;
		Tab.anIntArray240[3] = 4248;
		Tab.anIntArray240[4] = 4249;
		Tab.anIntArray240[5] = 4250;
		Tab.anIntArray240[6] = 6021;
	 * 
	 * necklases
	 * Tab.anIntArray240[0] = 4239;
		Tab.anIntArray240[1] = 4251;
		Tab.anIntArray240[2] = 4252;
		Tab.anIntArray240[3] = 4253;
		Tab.anIntArray240[4] = 4254;
		Tab.anIntArray240[5] = 4255;
		Tab.anIntArray240[6] = 6022;
	 * 
	 * 
	 * amulets
	 * Tab.anIntArray240[0] = 4245;
		Tab.anIntArray240[1] = 4256;
		Tab.anIntArray240[2] = 4257;
		Tab.anIntArray240[3] = 4258;
		Tab.anIntArray240[4] = 4259;
		Tab.anIntArray240[5] = 4260;
		Tab.anIntArray240[6] = 6023;
	 */


	public static boolean startAgain(final Player c, final int amm, final int slot) {
		if(((Integer) c.getExtraData().get("craftingItem")) == - 1) {
			System.out.println("problem");
			return false;
		}
		c.getActionSender().removeAllInterfaces();
		if(ContentEntity.returnSkillLevel(c, 12) < leatherLevel[((Integer) c.getExtraData().get("craftingItem"))][slot]) {
			ContentEntity.sendMessage(c, "Your Crafting level is not high enough to craft this.");
			return true;
		}
		c.getExtraData().put("craftingMade", finishItem[((Integer) c.getExtraData().get("craftingItem"))][slot]);
		c.getExtraData().put("craftingAmm", amountHide[((Integer) c.getExtraData().get("craftingItem"))][slot]);
		finishCraft(c, amm);
		return true;
	}

	public static void finishCraft(final Player c, final int amm) {
		if(c == null)
			return;
		if(((Integer) c.getExtraData().get("craftingItem")) == - 1 || ((Integer) c.getExtraData().get("craftingMade")) == - 1 || amm <= 0)
			return;
		c.setBusy(true);
		ContentEntity.startAnimation(c, CRAFT_ANIM);
		World.getWorld().submit(new Event(2000) {
			int craftAm = amm;

			@Override
			public void execute() {
				if(craftAm <= 0 || ! c.isBusy()) {
					stop2();
					return;
				} else if(ContentEntity.getItemAmount(c, (leatherItem[((Integer) c.getExtraData().get("craftingItem"))])) >= ((Integer) c.getExtraData().get("craftingAmm")) && ContentEntity.getItemAmount(c, 1734) > 0) {
					if(craftAm > 1)
						ContentEntity.startAnimation(c, CRAFT_ANIM);
					ContentEntity.sendMessage(c, "You craft the leather.");
					ContentEntity.deleteItemA(c, leatherItem[((Integer) c.getExtraData().get("craftingItem"))], ((Integer) c.getExtraData().get("craftingAmm")));
					ContentEntity.deleteItemA(c, 1734, 1);
					ContentEntity.addItem(c, ((Integer) c.getExtraData().get("craftingMade")), 1);
					ContentEntity.addSkillXP(c, leatherXp[((Integer) c.getExtraData().get("craftingItem"))] * EXPMULTIPLIER, 12);
					craftAm--;
				} else {
					stop2();
					return;
				}
			}


			public void stop2() {
				//c.getExtraData().put("craftingItem",-1);
				//c.getExtraData().put("craftingMade",-1);
				c.setBusy(false);
				this.stop();
			}

		});
	}

	public int getLeather(int i) {
		for(int k = 0; k < leatherItem.length; k++) {
			if(leatherItem[k] == i)
				return k;
		}
		return - 1;
	}

	public final static int[][] frameId = {
			{},//0 items we dont use this
			{},//1 items
			{},//2 items
			{8898, 8889, 8893, 8897, 8883, 8884, 8885, 8880},//3 items
			{},//4 Item
	};
	public final static int[] leatherItem = {1741, 1743, 1745, 2505, 2507, 2509};
	public final static int[][] finishItem = {
			{1061, 1063, 1095,/*1129*/},
			{1131, 1131, 1131},
			{1065, 1099, 1135,},
			{2487, 2493, 2499},
			{2489, 2495, 2501},
			{2491, 2497, 2503},
	};
	public final static int[][] amountHide = {
			{1, 1, 1,},
			{1, 1, 1,},
			{1, 2, 3,},
			{1, 2, 3,},
			{1, 2, 3,},
			{1, 2, 3,},
	};
	/*
	 * Gloves	1	13.8
	Boots	7	16.3
	Cowl	9	18.5
	Vambraces	11	22
	Leather body	14	25
	Chaps	18	27
	Coif	38	37
	Hard leather body	28	35
	Studded leather body	41	40+25
	Studded leather chaps	44	42+27
	 * */

	public final static int[] leatherXp = {28, 50, 186, 210, 234, 258};
	public final static int[][] leatherLevel = {
			{1, 9, 11},
			{28, 41, 44},
			{57, 60, 63,},
			{66, 68, 71},
			{73, 75, 77,},
			{79, 82, 84},
	};

	@Override
	public int[] getValues(int type) {
		if(type == 13) {
			int[] j = {1755, 1733,/*uncuts*/1623, 1621, 1619, 1617, 1631, 1625, 1627, 1629,/*bolts*/1607, 1605, 1603, 1601, 1615, 1609, 1611, 1613,/*hides*/1741, 1743, 1745, 2505, 2507, 2509,};
			return j;
		}
		if(type == 18) {
			int[] j = {1155, 1165, 1176, 1180, 1187, 6003,};
			return j;
		}
		if(type == 7) {
			int[] j = {2646, 2644,};
			return j;
		}
		if(type == 6) {
			int[] j = {1747/*ladder*/,};
			return j;
		}
		if(type == 14) {
			int[] j = {1779,};
			return j;
		}
		return null;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int id, final int slot, final int itemId2, final int itemSlot2) {
		if(type == 13) {
			return attemptCut(client, id, slot, itemId2, itemSlot2);
		}
		if(type == 6) {
			//System.out.println("ladder");
			client.setTeleportTarget(Location.create(client.getLocation().getX(), client.getLocation().getY(), client.getLocation().getZ() + 1));
			return true;
		}
		if(type == 7) {
			if(id == 2644)
				return spinFlax(client, id);
			return pickFlax(client, id);
		}
		if(type == 14) {
			return spinFlax(client, id);
		}
		if(type == 18)
			if(id == 1155)
				return enchant(client, 0, slot, itemId2);
			else if(id == 1165)
				return enchant(client, 1, slot, itemId2);
			else if(id == 1176)
				return enchant(client, 2, slot, itemId2);
			else if(id == 1180)
				return enchant(client, 3, slot, itemId2);
			else if(id == 1187)
				return enchant(client, 4, slot, itemId2);
			else if(id == 6003)
				return enchant(client, 5, slot, itemId2);
		return false;
	}

	private boolean pickFlax(final Player client, int id) {
		if(client.isBusy())
			return true;
		client.setBusy(true);
		ContentEntity.startAnimation(client, 2286);
		World.getWorld().submit(new Event(2000) {
			@Override
			public void execute() {
				if(! client.isBusy()) {
					this.stop();
					return;
				}
				ContentEntity.addItem(client, 1779, 1);
				client.setBusy(false);
				this.stop();
			}
		});
		return true;
	}

	private boolean spinFlax(final Player client, int id) {
		ContentEntity.startAnimation(client, 894);
		client.setBusy(true);
		World.getWorld().submit(new Event(2000) {
			int amount = ContentEntity.getItemAmount(client, 1779);

			@Override
			public void execute() {
				if(ContentEntity.isItemInBag(client, 1779) && amount > 0 && client.isBusy()) {
					ContentEntity.startAnimation(client, 894);
					ContentEntity.deleteItemA(client, 1779, 1);
					ContentEntity.addItem(client, 1777, 1);
					ContentEntity.sendMessage(client, "You spin the flax into a bow String.");
					ContentEntity.addSkillXP(client, 15 * EXPMULTIPLIER, Skills.CRAFTING);
					amount--;
				} else
					this.stop();
			}

		});
		ContentEntity.addItem(client, 1779, 1);

		return true;
	}


	@Override
	public void init() throws FileNotFoundException {
	}

	// needle: 1733
	// chisel: 1755
	/* 1059 Leather gloves 
 1061 Leather boots 
 1063 Leather vambraces 
 1095 Leather chaps 
 1129 Leather body 
 1131 Hardleather body 
 1167 Leather cowl 
 1741 Leather 
 1743 Hard leather 
 1745 Green d-leather 
 2505 Blue d-leather 
 2507 Red dragon leather 
 2509 Black d-leather 
*/

}