package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Equipment;

public class ContentEntity {

	public static double getXp(Player player, int skill) {
		return player.getSkills().getExperience(skill);
	}


	public static int getTotalAmountOfItems(Player player) {
		int count = 0;
		for(Item i : player.getInventory().toArray()) {
			if(i == null)
				continue;
			count += i.getCount();
		}
		return count;
	}

	public static int getTotalAmountOfEquipmentItems(Player player) {
		int count = 0;
		for(Item i : player.getEquipment().toArray()) {
			if(i == null)
				continue;
			count += i.getCount();
		}
		return count;
	}


	public static int getItemAmount(Player player, int id) {
		if(player.getInventory().getSlotById(id) < 0)
			return 0;
		Item i = player.getInventory().get(player.getInventory().getSlotById(id));
		if(i == null)
			return 0;
		else if(i.getDefinition().isStackable())
			return i.getCount();
		else {
			int count = 0;
			for(Item i2 : player.getInventory().toArray()) {
				if(i2 != null) {
					if(i2.getId() == id) {
						count++;
					}
				}
			}
			return count;
		}
	}

	public static void sendInterfaceModel(Player player, int id, int zoom, int model) {
		player.getActionSender().sendInterfaceModel(id, zoom, model);
	}

	public static void sendString(Player player, String s, int id) {
		player.getActionSender().sendString(id, s);
	}

	public static void removeAllWindows(Player player) {
		player.getActionSender().removeAllInterfaces();
	}

	public static void refreshSmithingScreen(Player player, int writeFrame, int[][] SMITHING_ITEMS) {
		player.getActionSender().sendUpdateSmith(writeFrame, SMITHING_ITEMS);
	}

	public static void showInterface(Player player, int id) {
		player.getActionSender().showInterface(id);
	}

	public static int getItemSlot(Player player, int item) {
		return player.getInventory().getSlotById(item);
	}

	public static void deleteItemAll(Player player, int id, int amount) {
		for(Item i : player.getInventory().toArray()) {
			if(i == null)
				continue;
			if(i.getId() == id) {
				player.getInventory().remove(i);
			}
		}
	}

	public static void turnTo(Player p, int x, int y) {
		p.face(Location.create(x, y, 0));
	}

	public static void playerGfx(Player player, int gfx) {
		player.playGraphics(Graphic.create(gfx, 6553600));
	}

	public static void playerGfx(Player player, int gfx, int delay) {
		player.playGraphics(Graphic.create(gfx, delay));
	}

	public static int freeSlots(Player player) {
		return player.getInventory().freeSlots();
	}

	public static boolean hasRoomFor(Player player, int id, int count) {
		return player.getInventory().hasRoomFor(new Item(id, count));
	}

	public static String getItemName(int item) {
		return ItemDefinition.forId(item).getName();
	}

	public static String getObjectName(int id) {
		return "";
	}

	public static void sendMessage(Player player, String message) {
		player.getActionSender().sendMessage(message);
	}

	public static boolean isItemInBag(Player player, int itemId) {
		if(player.getInventory().getSlotById(itemId) == - 1)
			return false;
		Item i = player.getInventory().get(player.getInventory().getSlotById(itemId));
		if(i == null)
			return false;
		return true;
	}

	public static boolean addItem(Player player, int itemId) {
		return addItem(player, itemId, 1);
	}

	public static boolean addItem(Player player, int itemId, int amm) {
		return player.getInventory().add(new Item(itemId, amm));
	}

	public static boolean addItem(Player player, int itemId, int amm, int slot) {
		Item item = player.getInventory().get(slot);
		if(item != null) {
			return false;
		}
		player.getInventory().add(new Item(itemId, amm), slot);
		return true;
	}

	public static boolean deleteItem(Player player, int itemId) {
		return player.getInventory().remove(new Item(itemId, 1)) != 0;
	}

	public static boolean deleteItem(Player player, int itemId, int slot) {
		return deleteItem(player, itemId, slot, 1);
	}

	public static boolean deleteItemA(Player player, int itemId, int am) {
		return player.getInventory().remove(new Item(itemId, am)) != 0;
	}

	public static int returnSkillLevel(Player player, int skill) {
		return player.getSkills().getLevel(skill);
	}

	public static void addSkillXP(Player player, double addXp, int skill) {
        double multiplier = 1.25;
        addXp *= multiplier;
		player.getSkills().addExperience(skill, addXp);
	}

	public static boolean deleteItem(Player player, int itemId, int slot, int amount) {
		Item item = player.getInventory().get(slot);
		if(item == null) {
			return false; // invalid packet, or client out of sync
		}
		if(item.getId() != itemId) {
			return false; // invalid packet, or client out of sync
		}
		if(item.getCount() < amount) {
			return false;
		}
		return player.getInventory().remove(slot, new Item(item.getId(), amount)) > 0;
	}

	public static int getLevelForXP(Player player, int skill) {
		return player.getSkills().getLevelForExp(skill);
	}

	/*public static int getLevelForXP(Player player,double xp){
	    return getLevelForXP(player,(int) xp);
	}*/

	public static void openDialogue(Player player, String[] message) {

	}

	public static void openInterface(Player player, int id) {

	}

	public static void freezeWalking(Player player, int length) {

	}

	public static int random(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	public static void showInterfaceWalkable(Player player, int i) {
		player.getActionSender().showInterfaceWalkable(i);
	}

	public static boolean isInArea(Player player, int x1, int y1, int x2, int y2) {
		if(player.getLocation().getX() >= x1 && player.getLocation().getX() <= x2
				&& player.getLocation().getY() >= y1 && player.getLocation().getY() <= y2)
			return true;
		return false;
	}

	public static void teleport(Player player, int x, int y, int z) {
		player.setTeleportTarget(Location.create(x, y, z));
	}

	public static void startAnimation(Player player, int id) {
		player.playAnimation(Animation.create(id, 0));
	}

	public static void startAnimation(Player player, int id, int delay) {
		player.playAnimation(Animation.create(id, delay));
	}

	public static void replaceItem(Player player, int slot, int newId, int amount) {
		Item i = player.getInventory().get(slot);
		if(i != null)
			player.getInventory().remove(slot, i);
		//player.getInventory().remove(i);
		addItem(player, newId, amount, slot);
	}

	public static void heal(Player player, int heal) {
		//System.out.println("heal");
		player.heal(heal);
	}
	
	public static void setOvlSkill(Player player, int skill, int am) {
		player.getSkills().setLevel(skill, player.getSkills().getLevelForExp(skill) + am);
	}
	
	public static void increaseSkill(Player player, int skill, int am, boolean ignoreHp) {
		//note do not use for HP!!!!!!
		if((! ignoreHp && skill == 3) || skill == 5) {
			player.heal(am, skill);
			return;
		} else if(player.getSkills().getLevel(skill) > player.getSkills().getLevelForExp(skill)) {
			player.getSkills().setLevel(skill, player.getSkills().getLevelForExp(skill) + am);
		} else {
			player.getSkills().setLevel(skill, player.getSkills().getLevel(skill) + am);
		}
	}

	public static void increaseSkill(Player player, int skill, int am) {
		increaseSkill(player, skill, am, false);
	}

	public static void decreaseSkill(Player player, int skill, int am) {
		player.getSkills().setLevel(skill, player.getSkills().getLevel(skill) - am);
	}

	public static boolean isItemInBag(Player player, int item, int slot) {
		Item item2 = player.getInventory().get(slot);
		if(item2 == null) {
			return false; // invalid packet, or client out of sync
		}
		if(item2.getId() != item) {
			return false; // invalid packet, or client out of sync
		}
		return true;
	}

	public static int count99Levels(Player player) {
		int counter = 0;
		for(int i = 0; i < 21; i++) {
			if(player.getSkills().getXPForLevel(i) >= 99)
				counter++;
		}
		return counter;
	}
}