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
        final String base = "You cannot have: ";
        final StringBuilder builder = new StringBuilder(base);
		for(Item i : player.getEquipment().toArray()) {
			if(i != null)
				if(!valid(i.getId()))
					builder.append(i.getDefinition().getName()).append(", ");
		}
		for(Item i : player.getInventory().toArray()) {
			if(i != null)
				if(!valid(i.getId()))
                    builder.append(i.getDefinition().getName()).append(", ");
		}
        if(!builder.toString().equalsIgnoreCase(base))
            return builder.toString();
		if(!player.getPrayers().isDefaultPrayerbook())
			return "You are on curses!";
		return "";
	}
	
	private static LinkedList<Integer> exceptions = new LinkedList<Integer>(Arrays.asList(new Integer[]{
		13351
	}));

	private static boolean valid(int id) {
		return id < 12000 || exceptions.contains(id);
	}

	
}
