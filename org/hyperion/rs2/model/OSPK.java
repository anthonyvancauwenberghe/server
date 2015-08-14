package org.hyperion.rs2.model;

import java.util.Arrays;
import java.util.LinkedList;

import org.hyperion.rs2.model.combat.Magic;

public class OSPK {
	
	public static boolean enter(Player player) {
		if(canEnter(player)) {
			player.setOverloaded(false);
			Magic.teleport(player, 2758, 3496, 0, false);
			return true;
		} else {
			player.sendMessage("@dre@" + canEnterMessage(player));
			player.getActionSender().removeChatboxInterface();
		}
		return false;
	}

	public static GameObject loadObjects() {
		return new GameObject(GameObjectDefinition.forId(2470),Location.create(3085, 3515, 0), 10, 1);
	}
	
	public static String canEnterMessage(Player player) {
		for(Item i : player.getEquipment().toArray()) {
			if(i != null)
				if(!valid(i.getId()))
					return "You are wearing an item with an id > 11000";
		}
		for(Item i : player.getInventory().toArray()) {
			if(i != null)
				if(!valid(i.getId()))
					return "Your inventory contains an item with an id > 11000";
		}
		if(!player.getPrayers().isDefaultPrayerbook())
			return "You are on curses!";
		return "";
	}
	
	private static LinkedList<Integer> exceptions = new LinkedList<Integer>(Arrays.asList(new Integer[]{
		11732, 20072
	}));
	
	private static boolean valid(int id) {
		return id < 11000 || exceptions.contains(id);
	}
	
	public static boolean canEnter(Player player) {
		return canEnterMessage(player).length() < 2;
	}
	
	public static boolean inArea(int x, int y) {
		return x <= 2764 && y >= 3493 && x >= 2752 && y <= 3503;
	}
	
	public static boolean inArea(Player player) {
		return inArea(player.getLocation().getX(), player.getLocation().getY());

	}
	
}
