package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.EnumSet;


/**
 * Handles crafting
 *
 * @author Jonas(++)
 * @author madturnip - martin
 */

public class Crafting implements ContentTemplate {

	public Crafting() {
	}

	private final static int EXPMULTIPLIER = 3 * Constants.XPRATE;

	public enum Gems {
		SAPPHIRE_GEM(1623, 1607, 40, 20, 888),
		EMERALD_GEM(1621, 1605, 65, 27, 889),
		RUBY_GEM(1619, 1603, 80, 34, 887),
		DIAMOND_GEM(1617, 1601, 85, 43, 886),
		DRAGONSTONE_GEM(1631, 1615, 100, 55, 885),
		OPAL_GEM(1625, 1609, 70, 23, 890),
		JADE_GEM(1627, 1611, 75, 25, 891),
		RED_TOPAZ_GEM(1629, 1613, 80, 30, 892),

		SAPPHIRE_BOLT_TIPS(1607, 9189, 40, 1, 888),
		EMERALD_BOLT_TIPS(1605, 9190, 50, 20, 889),
		RUBY_BOLT_TIPS(1603, 9191, 60, 30, 887),
		DIAMOND_BOLT_TIPS(1601, 9192, 90, 40, 886),
		DRAGONSTONE_BOLT_TIPS(1615, 9193, 120, 60, 885),
		OPAL_BOLT_TIPS(1609, 9187, 90, 20, 890),
		JADE_BOLT_TIPS(1611, 45, 70, 25, 891),
		RED_TOPAZ_BOLT_TIPS(1613, 9188, 80, 30, 892);

		private int gemId;
		private int resultId;
		private int exp;
		private int levelReq;
		private int emote;

		public int getResultId() {
			return resultId;
		}

		public int getGemId() {
			return gemId;
		}

		public int getExp() {
			return exp;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public String getName() {
			return Misc.ucFirst(this.toString().replaceAll("_", " ").replaceAll(" GEM", "").toLowerCase());
		}

		public int getEmote() {
			return emote;
		}

		Gems(int gemId, int resultId, int exp, int levelReq, int emote) {
			this.gemId = gemId;
			this.resultId = resultId;
			this.exp = exp;
			this.levelReq = levelReq;
			this.emote = emote;
		}

	}

	private enum Leather {
		LEATHER(1741, Leather_Item.LEATHER_BOOTS, Leather_Item.LEATHER_VAMBS, Leather_Item.LEATHER_CHAPS, Leather_Item.LEATHER_BODY),
		HARD_LEATHER(1743, Leather_Item.HARDLEATHER_BODY),
		GREEN_DRAGON_LEATHER(1745, Leather_Item.GREEN_DHIDE_VAMBS, Leather_Item.GREEN_DHIDE_CHAPS, Leather_Item.GREEN_DHIDE_BODY),
		BLUE_DRAGON_LEATHER(2505, Leather_Item.BLUE_DHIDE_VAMBS, Leather_Item.BLUE_DHIDE_CHAPS, Leather_Item.BLUE_DHIDE_BODY),
		RED_DRAGON_LEATHER(2507, Leather_Item.RED_DHIDE_VAMBS, Leather_Item.RED_DHIDE_CHAPS, Leather_Item.RED_DHIDE_BODY),
		BLACK_DRAGON_LEATHER(2509, Leather_Item.BLACK_DHIDE_VAMBS, Leather_Item.BLACK_DHIDE_CHAPS, Leather_Item.BLACK_DHIDE_BODY);

		private int itemId;
		private Leather_Item[] items;

		public Leather_Item[] getItems() {
			return items;
		}

		public int getItemId() {
			return itemId;
		}

		public String getName() {
			return Misc.ucFirst(this.toString().replaceAll("_", " ").toLowerCase());
		}

		Leather(int itemId, Leather_Item... items) {
			this.itemId = itemId;
			this.items = items;
		}


	}

	private enum Leather_Item {
		LEATHER_BOOTS(1061, 1, 1, 16),
		LEATHER_VAMBS(1063, 1, 9, 22),
		LEATHER_CHAPS(1095, 1, 11, 27),
		LEATHER_BODY(1129, 1, 14, 25),
		HARDLEATHER_BODY(1131, 1, 28, 35),
		GREEN_DHIDE_VAMBS(1065, 1, 57, 62),
		GREEN_DHIDE_CHAPS(1099, 2, 60, 124),
		GREEN_DHIDE_BODY(1135, 3, 63, 186),
		BLUE_DHIDE_VAMBS(2487, 1, 66, 70),
		BLUE_DHIDE_CHAPS(2493, 2, 68, 140),
		BLUE_DHIDE_BODY(2499, 3, 71, 210),
		RED_DHIDE_VAMBS(2489, 1, 73, 78),
		RED_DHIDE_CHAPS(2495, 2, 75, 156),
		RED_DHIDE_BODY(2501, 3, 77, 234),
		BLACK_DHIDE_VAMBS(2491, 1, 79, 86),
		BLACK_DHIDE_CHAPS(2497, 2, 82, 172),
		BLACK_DHIDE_BODY(2503, 3, 84, 258);

		private int
				itemId,
				levelReq,
				amountReq,
				exp;

		public int getItemId() {
			return itemId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getAmountReq() {
			return amountReq;
		}

		public int getExp() {
			return exp;
		}

		public String getName() {
			return Misc.ucFirst(this.toString().replaceAll("_", " ").replaceAll("DHIDE", "D'HIDE").toLowerCase());
		}

		Leather_Item(int itemId, int amountReq, int levelReq, int exp) {
			this.itemId = itemId;
			this.amountReq = amountReq;
			this.levelReq = levelReq;
			this.exp = exp;
		}
	}

	public boolean attemptCraft(Player c, int useItem, int slot1, int onItem, int slot2) {
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

	private Gems getGem(int i) {
		for(Gems gem : Gems.values()) {
			if(gem.getGemId() == i)
				return gem;
		}
		return null;
	}

	private static Leather_Item getLeatherItem(int i) {
		for(Leather_Item item : Leather_Item.values()) {
			if(item.getItemId() == i)
				return item;
		}
		return null;
	}

	private static Leather getLeather(int i) {
		for(Leather l : Leather.values()) {
			if(l.getItemId() == i)
				return l;
		}
		return null;
	}

	public boolean cutGem(Player c, int gem, int slot) {
		Gems g = getGem(gem);
		if(g == null) {
			return false;
		}

		if(ContentEntity.returnSkillLevel(c, 12) < g.getLevelReq()) {
			ContentEntity.sendMessage(c, "You need a crafting level of " + g.getLevelReq() + " to cut this gem.");
			return false;
		}

		if(c.isBusy())
			return false;

		c.setBusy(true);
		ContentEntity.startAnimation(c, g.getEmote());

		ContentEntity.sendMessage(c, "You start cutting the gem...");

		World.getWorld().submit(new Event(2200) {
			@Override
			public void execute() {
				ContentEntity.deleteItem(c, g.getGemId(), slot);
				boolean isBolt = g.getName().contains("tip");
				if (isBolt) {
					ContentEntity.sendMessage(c, "You cut the gem into " + g.getName().toLowerCase() + ".");
					ContentEntity.addItem(c, g.getResultId(), 15);
				} else {
					ContentEntity.sendMessage(c, "You cut the gem into " + Misc.aOrAn(g.getName()) + " " + g.getName().toLowerCase() + ".");
					ContentEntity.addItem(c, g.getResultId(), 1);
				}
				ContentEntity.addSkillXP(c, g.getExp(), Skills.CRAFTING);
				c.setBusy(false);
				this.stop();
			}
		});
		return true;
	}

	public final static int[][] frameId = {
			{}, //0 items
			{}, //1 items
			{8866, 8874, 8878, 8869, 8670},//2 items
			{8880, 8889, 8893, 8897, 8883, 8884, 8885},//3 items
			{8899, 8909, 8913, 8917, 8921, 8902, 8903, 8904, 8905},//4 items
			{8938, 8949, 8953, 8957, 8961, 8965, 8941, 8942, 8943, 8944, 8945},//5 items
	};

	public boolean craftLeather(final Player c, final int item) {
		try {
			Leather l = getLeather(item);
			if(l == null) {
				return true;
			}
			c.getExtraData().put("crafting", true);
			c.getExtraData().put("craftingFrom", l.getItemId());
			c.setBusy(true);
			Leather_Item[] items = l.getItems();
			if(items.length > 1) {
				ContentEntity.sendString(c, "What would you like to make?", frameId[items.length][0]);
				c.getActionSender().sendPacket164(frameId[items.length][0]);
				for (int i = 0; i < items.length; i++) {
					ContentEntity.sendString(c, items[i].getName(), frameId[items.length][i + 1]);
					ContentEntity.sendInterfaceModel(c, frameId[items.length][items.length + i + 1], 250, items[i].getItemId());
				}
			} else {
				startAgain(c, 1, 0);
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean startAgain(final Player c, final int amm, final int slot) {
		Leather leather = getLeather(c.getExtraData().getInt("craftingFrom"));

		if(leather == null)
			return false;

		Leather_Item item = leather.getItems()[slot];
		if(item != null)
			c.getExtraData().put("toCraft", item.getItemId());
		c.getActionSender().removeAllInterfaces();
		if(ContentEntity.returnSkillLevel(c, 12) < item.getLevelReq()) {
			ContentEntity.sendMessage(c, "You need a crafting level of " + item.getLevelReq() + " to craft this item.");
			return false;
		}
		if (ContentEntity.getItemAmount(c, leather.getItemId()) < item.getAmountReq()) {
			c.sendMessage("You need at least " + item.getAmountReq() + " pieces of " + leather.getName().toLowerCase() + ".");
			return false;
		}
		if (ContentEntity.getItemAmount(c, 1734) <= 0) {
			c.sendMessage("You don't have any thread.");
			return false;
		}
		finishCraft(c, amm);
		return true;
	}

	public static void finishCraft(final Player c, final int amm) {
		if(c == null)
			return;

		Leather_Item item = getLeatherItem(c.getExtraData().getInt("toCraft"));
		Leather leather = getLeather(c.getExtraData().getInt("craftingFrom"));

		if(item == null || leather == null || amm <= 0) {
			return;
		}

		c.setBusy(true);
		ContentEntity.startAnimation(c, 1249);

		World.getWorld().submit(new Event(2000) {
			int craftAm = amm;

			@Override
			public void execute() {
				if (craftAm <= 0 || !c.isBusy() || ContentEntity.getItemAmount(c, leather.getItemId()) <= 0) {
					stop();
					return;
				}
				if (ContentEntity.getItemAmount(c, 1734) <= 0) {
					c.sendMessage("You don't have any thread.");
					stop();
					return;
				}
				if (ContentEntity.getItemAmount(c, leather.getItemId()) < item.getAmountReq()) {
					c.sendMessage("You need at least " + item.getAmountReq() + " pieces of " + leather.getName().toLowerCase() + ".");
					stop();
					return;
				}
				if (ContentEntity.getItemAmount(c, 1733) <= 0) {
					c.sendMessage("You need a needle for this.");
					stop();
					return;
				}
				c.sendMessage("You craft the " + leather.getName().toLowerCase() +  " into " + Misc.aOrAn(item.getName()) + ((item.getName().contains("chaps") || item.getName().contains("boots") || item.getName().contains("vambs")) ? " pair of" : "") + " " + item.getName().toLowerCase() + ".");
				ContentEntity.deleteItemA(c, leather.getItemId(), item.getAmountReq());
				if (Misc.random(2) == 1)
					ContentEntity.deleteItemA(c, 1734, 1);
				ContentEntity.addItem(c, item.getItemId(), 1);
				ContentEntity.addSkillXP(c, item.getExp() * EXPMULTIPLIER, 12);
				if (craftAm > 1)
					ContentEntity.startAnimation(c, 1249);
				craftAm--;
			}

			@Override
			public void stop() {
				c.getExtraData().put("toCraft", null);
				c.getExtraData().put("craftingFrom", null);
				c.getExtraData().put("crafting", false);
				c.setBusy(false);
				ContentEntity.startAnimation(c, -1);
				super.stop();
			}

		});
	}

	@Override
	public int[] getValues(int type) {
		if(type == 13) {
			int[] j = {1755, 1733,/*uncuts*/1623, 1621, 1619, 1617, 1631, 1625, 1627, 1629,/*cuts*/1607, 1605, 1603, 1601, 1615, 1609, 1611, 1613,/*hides*/1741, 1743, 1745, 2505, 2507, 2509,};
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
		if(type == 14) {
			int[] j = {1779};
			return j;
		}
		return null;
	}

	public static boolean clickInterface(final Player client, final int id) {
		switch(id) {
			case 8909:
			case 8889:
			case 8949:
			case 8874:
				return startAgain(client, 1, 0);
			case 8913:
			case 8893:
			case 8953:
			case 8878:
				return startAgain(client, 1, 1);
			case 8917:
			case 8897:
			case 8957:
				return startAgain(client, 1, 2);
			case 8921:
			case 8961:
				return startAgain(client, 1, 3);
			case 8965:
				return startAgain(client, 1, 4);

			case 8908:
			case 8888:
			case 8948:
			case 8873:
				return startAgain(client, 5, 0);
			case 8912:
			case 8892:
			case 8952:
			case 8877:
				return startAgain(client, 5, 1);
			case 8916:
			case 8896:
			case 8956:
				return startAgain(client, 5, 2);
			case 8920:
			case 8960:
				return startAgain(client, 5, 3);
			case 8964:
				return startAgain(client, 5, 4);

			case 8907:
			case 8887:
			case 8947:
			case 8872:
				return startAgain(client, 10, 0);
			case 8911:
			case 8891:
			case 8951:
			case 8876:
				return startAgain(client, 10, 1);
			case 8915:
			case 8895:
			case 8955:
				return startAgain(client, 10, 2);
			case 8919:
			case 8959:
				return startAgain(client, 10, 3);
			case 8963:
				return startAgain(client, 10, 4);

			case 8906:
			case 8946:
			case 8886:
			case 8871:
				return startAgain(client, 28, 0);
			case 8910:
			case 8950:
			case 8890:
			case 8875:
				return startAgain(client, 28, 1);
			case 8914:
			case 8954:
			case 8894:
				return startAgain(client, 28, 2);
			case 8918:
			case 8958:
				return startAgain(client, 28, 3);
			case 8962:
				return startAgain(client, 28, 4);
		}
		return false;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int id, final int slot, final int itemId2, final int itemSlot2) {
		if(type == 13) {
			return attemptCraft(client, id, slot, itemId2, itemSlot2);
		}
		if(type == 7) {
			if(id == 2644)
				return spinFlax(client, id);
			return pickFlax(client, id);
		}
		if(type == 14) {
			return spinFlax(client, id);
		}
		return false;
	}

	private boolean pickFlax(final Player client, int id) {
		if(client.isBusy())
			return false;
		client.setBusy(true);
		ContentEntity.startAnimation(client, 2286);
		World.getWorld().submit(new Event(2000) {
			@Override
			public void execute() {
				if(!client.isBusy()) {
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
}