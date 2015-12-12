package org.hyperion.rs2.model;

import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;

import java.util.Arrays;
import java.util.LinkedList;

public class OSPK extends NIGGERUZ {

    private static final LinkedList<Integer> exceptions = new LinkedList<Integer>(Arrays.asList(13351));


    public OSPK() {
        super(600);
    }

    public static GameObject loadObjects() {
        return new GameObject(GameObjectDefinition.forId(2470), Location.create(3085, 3515, 0), 10, 1);
    }

    private static boolean valid(final int id) {
        return id < 12000 || exceptions.contains(id);
    }

    public String canEnter(final Player player) {
        final String base = "You cannot have: ";
        final StringBuilder builder = new StringBuilder().append(base);
        for(final Item i : player.getEquipment().toArray()){
            if(i != null)
                if(!valid(i.getId()))
                    builder.append(i.getDefinition().getName()).append(", ");
        }
        for(final Item i : player.getInventory().toArray()){
            if(i != null)
                if(!valid(i.getId()))
                    builder.append(i.getDefinition().getName()).append(", ");
        }
        if(!builder.toString().equalsIgnoreCase(base))
            return builder.toString();
        if(!player.getPrayers().isDefaultPrayerbook())
            return "You must be on the normal prayer book!";
        return "";
    }


}
