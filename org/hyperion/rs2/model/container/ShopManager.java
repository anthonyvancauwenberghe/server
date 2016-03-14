package org.hyperion.rs2.model.container;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.SpiritShields;
import org.hyperion.rs2.model.content.misc2.MysteryBox;
import org.hyperion.rs2.model.shops.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Shopping utility class.
 *
 * @author Arsen Maxyutov.
 */
public class ShopManager {

	/**
	 * Donator shop id.
	 */
	public static final int DONATOR_SHOP_1_ID = 63;

	/**
	 * The shop size.
	 */
	public static final int SIZE = 40;

	/**
	 * The player inventory interface.
	 */
	public static final int PLAYER_INVENTORY_INTERFACE = 3823;

	/**
	 * The shop inventory interface.
	 */
	public static final int SHOP_INVENTORY_INTERFACE = 3900;

	/**
	 * Opens the shop for the specified player.
	 *
	 * @param player The player to open the shop for.
	 */
	public static void open(Player player, int id) {
		if(player.getSkills().getLevel(3) <= 1) {
			player.sendMessage("You cannot open shops when you have low health.");
			return;
		}
		//player.getLogging().log("Opened shop " + id);
		player.getExtraData().put("geshop", 0);
		player.getActionSender().sendInterfaceInventory(3824, 3822);
		player.getActionSender().sendUpdateItems(3823,
				player.getInventory().toArray());
		Shop shop = Shop.forId(id);
		player.getActionSender().sendUpdateItems(3900,
				shop.getContainer().toArray());
        if(id == 78) {
			player.getActionSender().sendString(3901, "Emblem Points: @gre@"+player.getBountyHunter().getEmblemPoints());
        } else if(id == 63 || id == 64) {
			player.getActionSender().sendString(3901, "Donator points: @gre@" + player.getPoints().getDonatorPoints());
		} else if(id == 75) {
			player.getActionSender().sendString(3901, "Voting points: @gre@" + player.getPoints().getVotingPoints());
		} else if(id == 71) {
			player.getActionSender().sendString(3901, "ArteroPK points: @gre@" + player.getPoints().getPkPoints());
		} else if(id == 76) {
			player.getActionSender().sendString(3901, "Honor points: @gre@" + player.getPoints().getHonorPoints());
		} else if(id == 81) {
			player.getActionSender().sendString(3901, "Dungeoneering tokens: @gre@" + player.getDungeoneering().getTokens());
		} else {
			player.getActionSender().sendString(3901, shop.getName());
		}
		player.setShopId(id);
	}

	/**
	 * Sells the item.
	 *
	 * @param player
	 * @param itemId
	 * @param slot
	 * @param amount
	 */
	public static void sellItem(Player player, int itemId, int slot, int amount) {
		if(player.getShopId() < 0 || itemId == Shop.COINS_ID)
			return;
		if(! ItemsTradeable.isTradeable2(itemId, player.getGameMode())) {
			player.getActionSender().sendMessage(
					"You cannot sell this item in any shop.");
			return;
		}
		int has_amount = player.getInventory().getCount(itemId);
		if(has_amount == 0)
			return;
		if(amount > has_amount)
			amount = has_amount;
		Shop shop = Shop.forId(player.getShopId());
		if(shop.isGeneral() || shop.isStatic(itemId) || player.isServerOwner() && DonatorShop.isVeblenGood(itemId))
			shop.sellToShop(player, new Item(itemId, amount));
		else
			player.getActionSender().sendMessage("You can't sell this item to this shop.");
	}

	/**
	 * Values an item for selling.
	 *
	 * @param player
	 * @param itemId
	 */
	public static void valueSellItem(Player player, int itemId) {
		if(player.getShopId() < 0 || itemId == Shop.COINS_ID)
			return;
		Item item = player.getInventory().getById(itemId);
		if(item == null)
			return;
		Shop shop = Shop.forId(player.getShopId());
		if(shop.isGeneral() || shop.isStatic(itemId))
			shop.valueSellItem(player, new Item(itemId));
		else
			player.getActionSender().sendMessage("You can't sell this item to this shop.");
	}

	/**
	 * Values an item for buying.
	 *
	 * @param player
	 * @param itemId
	 */
	public static void valueBuyItem(Player player, int itemId) {
		if(player.getShopId() < 0 || itemId == 995)
			return;
		Shop shop = Shop.forId(player.getShopId());
		Item item = shop.getContainer().getById(itemId);
		if(item == null)
			return;
		shop.valueBuyItem(player, new Item(itemId));
	}

	/**
	 * Buys an item.)
	 *
	 * @param player
	 * @param itemId
	 * @param slot
	 * @param amount
	 */
	public static void buyItem(Player player, int itemId, int slot, int amount) {
		if(player.getShopId() <= - 1)
			return;
		if(amount > player.getInventory().freeSlots() && ! ItemDefinition.forId(itemId).isStackable())
			amount = player.getInventory().freeSlots();
		Shop shop = Shop.forId(player.getShopId());
		Item shopItem = shop.getContainer().getById(itemId);
		if(shopItem == null)
			return;
		if(amount > shopItem.getCount()) {
			amount = shopItem.getCount();
		}
		shop.buyFromShop(player, new Item(itemId, amount));
	}

	/**
	 * @param shopId
	 * @param itemId
	 * @return
	 */
	public static int getPoints(int shopId, int itemId) {
		switch(shopId) {
			case 75:
			case 76:
				switch(itemId) {
					case 17237:
					case 17017:
					case 16755:
					case 18747:
					case 16865:
						return 20;
					case 16931:
					case 17171:
						return 10;
					case 19780:
						return 50;
					case 15220: // Imbued rings
					case 15020:
					case 15019:
					case 15018:
					case 19747:
					case 13101:
						return 20;
					case 15600:
					case 15606:
					case 15612:
					case 15618:
					case 15602:
					case 15608:
					case 15614:
					case 15620:
					case 15604:
					case 15610:
					case 15616:
					case 15622:
					case 15021:
					case 15022:
					case 15023:
					case 15024:
					case 15025:
					case 15026:
					case 15027:
					case 15028:
					case 15029:
					case 15030:
					case 15031:
					case 15032:
					case 15033:
					case 15034:
					case 15035:
					case 15036:
					case 15037:
					case 15038:
					case 15039:
					case 15040:
					case 15041:
					case 15042:
					case 15043:

					case 15044:
						return 5;
					case 14876:
					case 2890:
						return 3;
				}
				break;
			case 71:
				if(itemId >= 8845 && itemId <= 8850) {
					return (itemId - 8844) * 100;
				}
				switch(itemId) {
					case 15272:
						return 1;
					case 6570:
					case 8842:
						return 500;
					case 8839:
					case 8840:
					case 10547:
					case 10548:
					case 10549:
					case 10550:
						return 750;
					case 11663:
					case 11664:
					case 11665:
					case 10551:
						return 1000;
					case 18333:
					case 18335:
						return 1500;
					case 15243:
						return 1;
					case 13902:
					case 13899:
						return 5000;
					case 13887:
					case 13893:
					case 13884:
					case 13890:
					case 13896:
						return 2500;
				}
			case 62:// shop id 62
				switch(itemId) {
					case 4151:
						return 5000;
					case 15441:
					case 15442:
					case 15443:
					case 15444:
						return 8000;
					case 15600:
					case 15606:
					case 15612:
					case 15618:
						return 5000;
					case 15602:
					case 15608:
					case 15614:
					case 15620:
						return 3000;
					case 15604:
					case 15610:
					case 15616:
					case 15622:
						return 4000;

					case 15021:
					case 15022:
					case 15023:
					case 15024:
					case 15025:
					case 15026:
					case 15027:
					case 15028:
					case 15029:
					case 15030:
					case 15031:
					case 15032:
					case 15033:
					case 15034:
					case 15035:
					case 15036:
					case 15037:
					case 15038:
					case 15039:
					case 15040:
					case 15041:
					case 15042:
					case 15043:
					case 15044:
						return 2500;
				}
				break;
			case 63:
			case 64:
			case 65:
				switch(itemId) {
					case 16909:
						return 4000;
					case 19713:
					case 19714:
					case 19715:
					case 19716:
					case 19717:
					case 19718:
					case 19719:
					case 19720:
					case 19721:
						return 1999;
					case 16711:
					case 17259:
					case 16689:
					case 17361:
					case 16359:
					case 16955:
						return 1499;
					case 14484:
						return 1199;
					case 18351:
					case 18349:
					case 18353:
					case 18355:
					case 18359:
					case 18357:
						return 899;
					case 11794:
						return 999;
					case 15486:
					case 1038:
					case 1040:
					case 1042:
					case 1044:
					case 1046:
					case 1048:

						return 899;

					case 15042:
						return 1199;
					case SpiritShields.DIVINE_SPIRIT_SHIELD_ID:
						return 599;
					case 15060:
					case 13738:
					case 13742:
					case 13744:
						return 199;
					case 13352:
					case 13353:
					case 13354:
					case 13355:
					case 13356:
					case 11694:
					case 15241:
					case 19143:
					case 19146:
					case 19149:
					case 1050:
					case 1053:
					case 1055:
					case 1057:
					case 1037:
					case 10887:
					case 3140:
					case 15006:
					case 15020:
						return 399;
					case 1419:
					case 10696:
					case 11698:
					case 11700:
					case 11696:
					case 19613:
					case 19615:
					case 19617:
					case 18333:
					case 18335:
					case 13736:
						return 299;
					case 11718:
					case 11720:
					case 11722:
					case 11724:
					case 11726:
					case 19459:
					case 19461:
					case 19463:
					case 19465:
						return 199;
					case 11730:
					case 15061:
					case 15062:
					case 15063:
					case 15064:
					case 15065:
					case 15066:
					case 15067:
					case 15068:
						return 150;
					case 10330:
					case 10332:
					case 10334:
					case 10336:
					case 10338:
					case 10340:
					case 10342:
					case 10344:
					case 10346:
					case 10348:
					case 10350:
					case 10352:
					case 13734:
						return 200;
					case 6889:
					case 6914:
					case 10547:
					case 10548:
					case 10549:
					case 10550:
					case 1149:
					case 4087:
					case 4585:
					case MysteryBox.ID:
						return 100;
					case 11728:
					case 15441:
					case 15442:
					case 15443:
					case 15444:
						return 99;
					case 4710:
					case 4718:
					case 4726:
					case 4734:
					case 4747:
					case 4755:
					case 4708:
					case 4712:
					case 4714:
					case 4716:
					case 4720:
					case 4722:
					case 4724:
					case 4728:
					case 4730:
					case 4732:
					case 4736:
					case 4738:
					case 4745:
					case 4749:
					case 4751:
					case 4753:
					case 4757:
					case 4759:
						return 79;
					case 8839:
					case 8840:
						return 75;
					case 6916:
					case 6918:
					case 6920:
					case 6922:
					case 6924:
					case 10551:
						return 50;
					case 7462:
						return 40;
					case 6570:
						return 35;
					case 11663:
					case 11664:
					case 11665:
					case 8842:
						return 30;
					case 7806:
					case 7807:
					case 8849:
					case 8850:
						return 24;
					case 7808:
					case 10499:
					case 8848:
						return 20;
					case 4508:
					case 7809:
						return 18;
					case 8847:
					case 6585:
						return 15;
					case 4509:
						return 14;
					case 4510:
					case 8846:
						return 10;
					case 4511:
					case 4566:
						return 8;
					case 4512:
						return 6;
					case 2439:
					case 8845:
						return 5;
					case 2431:
					case 2430:
					case 15332:
					case 15015:
						return 1;
					case 13883:
					case 13879:
						return 1;
					case 19152:
					case 19157:
					case 19162:
						return 1;

					default:
						return 50000;

				}
		}
		return 50000;// incase something bugs, we dont make it super cheap
	}

	private static final int[] valuableShopsIds = {62, 63, 64, 65, 71};

	public static boolean isValuable(int id) {
		if(id == 15272)
			return false;
		for(int shopid : valuableShopsIds) {
			if(getPoints(shopid, id) != 50000
					|| getPoints(shopid, id + 1) != 50000
					|| getPoints(shopid, id - 1) != 50000)
				return true;
		}
		return false;
	}


	public static int getSpecialPrice(int itemid) {
		switch(itemid) {
			case 2577:
			case 2581:
				return 4500;
			case 15126:
				return 8000;
			case 11235:
			case 15701:
			case 15702:
			case 15703:
			case 15704:
				return 3000;
		}
		return 50000; // incase bugs
	}

	/**
	 * A List of Shops.
	 */

	private static Shop[] shops = new Shop[100];

	public static Shop forId(int id) {
		return shops[id];
	}

	/**
	 * Finds all shops whose names contain the specified key.
	 *
	 * @return
	 */
	public static List<Shop> forName(String key) {
		key = key.toLowerCase();
		List<Shop> list = new LinkedList<Shop>();
		for(Shop shop : shops) {
			if(shop == null)
				continue;
			if(shop.getName().toLowerCase().contains(key)) {
				list.add(shop);
			}
		}
		return list;
	}

	private static void process() {
		for(Shop shop : shops) {
			if(shop == null)
				continue;
			shop.process();
		}
	}

	/**
	 * Reloads shops.
	 *
	 * @throws IOException
	 */
	public static void reloadShops() throws IOException {
		shops = new Shop[100];
		loadShops("./data/newshops.cfg");
	}

	/**
	 * Loads all shops.
	 *
	 * @param name
	 * @throws IOException
	 */
	public static void loadShops(String name) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(name));
		String line;
		int counter = 0;
		while((line = in.readLine()) != null) {
			try {
				String[] parts = line.split(",");
				int shopId = Integer.parseInt(parts[0]);
				String shopName = parts[1];
				String type = parts[2];
				Shop shop = null;
				Container shopContainer = new Container(Container.Type.ALWAYS_STACK, SIZE);

				if(type.contains("donator")) {
					shop = new DonatorShop(shopId, shopName, shopContainer);
				} else if(type.contains("vote")) {
					shop = new VoteShop(shopId, shopName, shopContainer);
				} else if(type.contains("pkshop")) {
					shop = new PkShop(shopId, shopName, shopContainer);
				} else if(type.contains("rlhonorshop")) {
					shop = new RecklessHonorShop(shopId, shopName, shopContainer);
				} else if(type.contains("devhonorshop")) {
					shop = new DeviousHonorShop(shopId, shopName, shopContainer);
				} else if(type.contains("general")) {
					shop = new CurrencyShop(shopId, shopName, shopContainer, Shop.COINS_ID, true);
				} else if(type.contains("specialist")) {
					shop = new CurrencyShop(shopId, shopName, shopContainer, Shop.COINS_ID, false);
				} else if(type.contains("tzhaar")) {
					shop = new CurrencyShop(shopId, shopName, shopContainer, 6529, false);
				} else if(type.contains("slayer")) {
                    shop = new SlayerShop(shopId, shopName
                    , shopContainer);
                } else if(type.contains("emblem")) {
                    shop = new EmblemShop(shopId, shopName
                            , shopContainer);
                } else if(type.contains("pvm")) {
                    shop  = new PvMStore(shopId, shopName, shopContainer);
                } else if(type.contains("legendary")) {
                    shop  = new LegendaryStore(shopId, shopName, shopContainer);
                } else if (type.contains("dungeon")) {
                    shop = new DungeoneeringStore(shopId, shopName, shopContainer);
                }
				for(int i = 3; i < parts.length; i++) {
					String part = parts[i];
					part = part.trim();
					if(part.length() == 0)
						break;
					String[] sub = part.split("-");
					int id = Integer.parseInt(sub[0]);
					int amount = Integer.parseInt(sub[1]);
					Item item = new Item(id, amount);
					//System.out.println(item);
					shopContainer.add(item);
					shop.addStaticItem(item);
				}
				shops[shopId] = shop;
				counter++;
			} catch(Exception e) {
				System.out.println("Shop error, Line: " + line);
				e.printStackTrace();
				break;
			}
		}
		in.close();
		System.out.println("Loaded " + counter + " shops.");
		World.submit(new Task(10000,"shopmanager") {
			@Override
			public void execute() {
				process();
			}
		});
	}

	static {
		try {
			loadShops("./data/newshops.cfg");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
