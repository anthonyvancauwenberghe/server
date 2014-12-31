package org.hyperion.rs2.model;

import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.EP.EPDrops;
import org.hyperion.rs2.model.content.minigame.DangerousPK;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc2.Food;
import org.hyperion.rs2.model.content.misc2.NewGameMode;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.util.Misc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author SaosinHax
 */
public class DeathDrops {
	
	public static final List<Integer> FOOD = Arrays.asList(new Integer[] {
		391, 15272, 385	
	});
	
	private static boolean dontDrop(Player player) {
		return dontDropRank(player.getPlayerRank()) || player.getLocation().inFunPk();
	}
	private static boolean dontDropRank(long l) {
		return Rank.getPrimaryRank(l).ordinal() >= Rank.ADMINISTRATOR.ordinal();
	}
	/**
	 * Drops player's items on normal death
	 */
	public static void dropsAtDeath(Player player, Player killer) {
		if(killer == null || player == null)
			return;
		if(dontDrop(player) || dontDrop(killer))
			return;
		/**
		 * Resets death variables - which slots are being protected
		 * Sets which items are being kept, deletes them 
		 */
		player.resetDeathItemsVariables();
		List<Item> keepItems = itemsKeptOnDeath(player, false, false);
		
		//If in dangerous pk set shit to null again
		if(DangerousPK.inDangerousPK(player))
			player.resetDeathItemsVariables();
		/**
		 * List that stores items to be dropped later
		 */
		List<Item> droppingItems = new LinkedList<>();
		
		/**
		 * {@link #processContainer(Container container, List<Item> original, boolean inv, Player player)}
		 */
		droppingItems = processContainer(player.getInventory(), droppingItems, true, player);
		droppingItems = processContainer(player.getEquipment(), droppingItems, false, player);
		/**
		 * Adds EP Drop.
		 */
		Item epItems = EPDrops.getEPItem(killer.EP);
		if(epItems != null) {
			killer.removeEP();
			World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), epItems));
		}
		/**
		 * Drops the items for the killer
		 */
		for(Item item : droppingItems) {
            if(killer.getGameMode() <= player.getGameMode())
			    World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), item));
            else {
                int price = (int)(NewGameMode.getUnitPrice(item) * NewGameMode.SELL_REDUCTION);
                if(price > 1)
                    World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(),
                        Item.create(995, price)));
            }
        }

        if(killer.hardMode()) {
            World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), Item.create(995, 50_000)));
        }

        player.getLogManager().add(LogEntry.death(player, killer, droppingItems.toArray(new Item[droppingItems.size()])));;


    }
	/**
	 * Adds unspawnables from equip/inv - only takes items that are unspawnable
	 */
	public static List<Item> processContainer(Container container, List<Item> originalDrops, boolean inv, Player player) {
		for(int slot = 0; slot < container.capacity(); slot++) {
			if((inv && player.invSlot[slot])) 
				continue;
			if((!inv && player.equipSlot[slot]))
				continue;
			Item item = container.get(slot);
			if(toDrop(item, player.getGameMode())) {
                System.out.println("To drop: "+item.getId());
				if(ItemsTradeable.isTradeable(item.getId()))
					originalDrops.add(item);
				container.remove(slot, item);
			}	
		}
		return originalDrops;
	}
	
	public static boolean toDrop(Item item, final int gameMode) {
		if(item == null)
			return false;
		if(ItemsTradeable.isTradeable(item.getId())) {
			if(ItemSpawning.canSpawn(item.getId()) && Food.get(item.getId()) == null && gameMode == 0)
				return false;
			return true;
		} else {
            if(item.getId() >= 13195 && item.getId() <= 13205)
                return true;
			switch(item.getId()) {
			case 20000:
			case 19713://nex helms
			case 19716:
			case 19719:
			case 19817:
			case 19816: //Glacor boots
			case 19815:
			case 16887: // sagittarian shortbow
			case 16337: // sagittarian longbow
			case 17193: //sagittarian gear
			case 17339:
			case 17215:
			case 17317:
			case 18349: //chaotic weapons
			case 18351:
			case 18353:
			case 18355:
			case 18357:
            case 17660:
				return Misc.random(9) == 0;
			case 19780: //krazi korazi!
				return true;
				//return Misc.random(2) == 0;
			}
		}
		return false;
	}

	public static List<Item> itemsKeptOnDeath(Player player, boolean delete, boolean interfaceUse) {
		boolean[] invSlot = player.invSlot.clone();
		boolean[] equipSlot = player.equipSlot.clone();
		List<Item> keepItems = new LinkedList<Item>();
		int keeping = player.getLocation().inFunPk() ? 
			player.getInventory().size() + player.getEquipment().size() :
			((player.isSkulled() ? 0 : 3) + (player.getPrayers().isProtectingItem() ? 1 : 0));
		for(int i = 0; i < keeping; i++) {
			Item item = keepItem(player, i, delete);
			if(item != null)
				keepItems.add(item);
		}
		if(interfaceUse) {
			for(int i = 0; i < player.invSlot.length; i++)
				player.invSlot[i] = invSlot[i];
			for(int i = 0; i < player.equipSlot.length; i++)
				player.equipSlot[i] = equipSlot[i];
		}
		return keepItems;
	}
	
	@Deprecated
	public static void dropAllItems(Player player, Player killer) {
		//System.out.println("Dropping items for player:" + player.getName());
		if(killer == null || player == null)
			return;
		if(Rank.getPrimaryRank(player).ordinal() >= Rank.ADMINISTRATOR.ordinal())
			return;

		if(Rank.getPrimaryRank(killer).ordinal() >= Rank.ADMINISTRATOR.ordinal())
			return;
		if(killer.getLocation().inFunPk() || player.getLocation().inFunPk())
			return;
		player.resetDeathItemsVariables();
		List<Item> keepItems = itemsKeptOnDeath(player, true, false);
		/**
		 * Use one list to avoid repeating code.
		 */
		LinkedList<Item> droppingItems = new LinkedList<Item>();
		for(Item item : player.getEquipment().toArray()) {
			droppingItems.add(item);
		}
		for(Item item : player.getInventory().toArray()) {
			droppingItems.add(item);
		}
		/**
		 * Clear inventory and equipment.
		 */
		player.getInventory().clear();
		player.getEquipment().clear();
		/**
		 * Adds EP Drop.
		 */
		if(killer != null && ! killer.loggedOut) {
			if(Rank.hasAbility(killer, Rank.OWNER))
				System.out.println("Killer: " + killer.getName());
			Item EPItem = EPDrops.getEPItem(killer.EP);
			if(EPItem != null) {
				killer.removeEP();
				if(Rank.hasAbility(killer, Rank.OWNER))
					System.out.println("Ep item: " + ItemDefinition.forId(EPItem.getId()).getName());
				World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), EPItem));
			}
		}
		LinkedList<Item> delayedDrops = new LinkedList<Item>();
		/**
		 * Clean Equipment.
		 */
		for(Item dropItem : droppingItems) {
			if(dropItem == null)
				continue;
			if(ItemsTradeable.isTradeable(dropItem.getId())) {
				if(ItemSpawning.allowedMessage(dropItem.getId()).length() <= 1)
					delayedDrops.add(dropItem);
				else
					World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), dropItem));
			} else {
				/**
				 * Following id's should not be dropped.
				 */
				switch(dropItem.getId()) {
					case 20000:
					case 19713://nex helms
					case 19716:
					case 19719:
					case 19817:
					case 19816: //Glacor boots
					case 19815:
					case 16887: // sagittarian shortbow
					case 16337: // sagittarian longbow
					case 17193: //sagittarian gear
					case 17339:
					case 17215:
					case 17317:
					case 18349: //chaotic weapons
					case 18351:
					case 18353:
					case 18355:
					case 18357:
						if(Misc.random(4) == 0)
							continue;
						break;
					case 19780:
						continue;
				}
				keepItems.add(dropItem);
				//World.getWorld().getGlobalItemManager().newDropItem(player, new GlobalItem(player,player.getLocation(),player.getEquipment().get(i)));
			}
		}
		for(Item delayedItem : delayedDrops) {
			World.getWorld().getGlobalItemManager().newDropItem(killer, new GlobalItem(killer, player.getLocation(), delayedItem));
		}
		for(Item keepItem : keepItems) {
			if(keepItem != null)
				player.getInventory().add(keepItem);
		}
	}

	public static Item keepItem2(Player player, int keepItem, boolean deleteItem) {
		int capacity = player.getInventory().capacity() + player.getEquipment().capacity();
		int itemId = - 1, itemSlot = 0, itemCount = 1, itemContainer = 0;
		for(int i = 0; i < capacity; i++) {
			boolean slotInInv = i < player.getInventory().capacity();
			int currentSlot = slotInInv ? i : i - player.getInventory().capacity();
			Item item = (slotInInv ? player.getInventory() : player.getEquipment()).get(currentSlot);
			if(item != null && item.comparePriceWith(itemId) != itemId) {
				itemId = item.getId();
				itemContainer = slotInInv ? 0 : 1;
				itemSlot = currentSlot;
				itemCount = item.getCount();
			}
		}
		if(itemId != - 1) {
			(itemContainer == 0 ? player.invSlot : player.equipSlot)[itemSlot] = itemCount > 1;
			if(deleteItem) {
				if(itemContainer == 0)
					ContentEntity.deleteItem(player, itemId, itemSlot, 1);
				else
					player.getEquipment().set(itemSlot, null);
			}
			return new Item(player.itemKeptId[keepItem] = itemId, 1);
		}
		return null;
	}
	public static int calculateAlchValue(final Player player ,int id) {
		int dpVal = DonatorShop.getPrice(id);
		int inventoryItemValue = 0;
        if(player.hardMode())
            inventoryItemValue = NewGameMode.getUnitPrice(id);
		else if(dpVal > 100)
			inventoryItemValue = dpVal * 150000;
		else
			inventoryItemValue = ItemSpawning.canSpawn(id) ? -1 :(int) Math.floor(ItemDefinition.forId(id).getHighAlcValue());
		return inventoryItemValue;
	}
	public static Item keepItem(Player player, int keepItem, boolean deleteItem) {
		int value = 0;
		int item = - 1;
		int slotId = 0;
		boolean itemInInventory = false, itemStackZero = true;
		for(int i = 0; i < player.getInventory().capacity(); i++) {
			if(player.getInventory().get(i) != null) {
				int dpVal = DonatorShop.getPrice(player.getInventory().get(i).getId());
				int inventoryItemValue = calculateAlchValue(player ,player.getInventory().get(i).getId());
				if(inventoryItemValue > value && (! player.invSlot[i])) {
					value = inventoryItemValue;
					item = player.getInventory().get(i).getId();
					slotId = i;
					itemInInventory = true;
				}
			}
		}
		for(int i1 = 0; i1 < player.getEquipment().capacity(); i1++) {
			if(player.getEquipment().get(i1) != null) {
				int dpValue = (int)Math.floor(DonatorShop.getPrice(player.getEquipment().get(i1).getId()));
				int equipmentItemValue = calculateAlchValue(player ,player.getEquipment().get(i1).getId());

				if(equipmentItemValue > value && (! player.equipSlot[i1])) {
					value = equipmentItemValue;
					item = player.getEquipment().get(i1).getId();
					slotId = i1;
					itemInInventory = false;
				}
			}
		}
		if(itemInInventory) {
			if(itemStackZero)
				player.invSlot[slotId] = true;
			if(deleteItem) {
				//ContentEntity.deleteItem(player, player.getInventory().get(slotId).getId(), slotId);
				ContentEntity.deleteItem(player, item, slotId, 1);
			}
		} else {
			if(itemStackZero)
				player.equipSlot[slotId] = true;
			if(deleteItem) {
				player.getEquipment().set(slotId, null);
			}
		}
		player.itemKeptId[keepItem] = item;
		if(item == - 1)
			return null;
		else
			return new Item(item, 1);
	}
}
