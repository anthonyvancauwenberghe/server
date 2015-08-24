package org.hyperion.rs2.model;

import java.util.Arrays;
import java.util.LinkedList;

import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;

public class OSPK extends NIGGERUZ {

    public OSPK() {
        super(600);
    }


	public static GameObject loadObjects() {
		return new GameObject(GameObjectDefinition.forId(2470),Location.create(3085, 3515, 0), 10, 1);
	}
	
	public String canEnter(Player player) {
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

	
}
