package org.hyperion.rs2.model.sets;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;

public class SetUtility {
    /**
     * Use as
     * <code>
     * getInstantSet(player, HELM, AMULET, ARROWS, CAPE, BODY, LEGS, SHIELD, WEAPON, BOOTS, RING, GLOVES)
     * </code>
     */
    public static final void getInstantSet(final Player p, final Item... items) {
        for(final Item item : items){
            p.getEquipment().set(Equipment.getType(item).getSlot(), item);
        }
        /*cS(p, items[0], );
        cS(p, items[1], Equipment.SLOT_AMULET);
		cS(p, items[2], Equipment.SLOT_ARROWS);
		cS(p, items[3], Equipment.SLOT_CAPE);
		cS(p, items[4], Equipment.SLOT_CHEST);
		cS(p, items[5], Equipment.SLOT_BOTTOMS);
		cS(p, items[6], Equipment.SLOT_SHIELD);
		cS(p, items[7], Equipment.SLOT_WEAPON);
		cS(p, items[8], Equipment.SLOT_BOOTS);
		cS(p, items[9], Equipment.SLOT_RING);
		cS(p, items[10], Equipment.SLOT_GLOVES);*/

    }

    @SuppressWarnings("unused")
    @Deprecated
    private static final void cS(final Player p, final Item item, final int slot) {
        p.getEquipment().set(slot, item);
        try{
            Thread.sleep(10);
        }catch(final Exception e){

        }
    }

    public static final void addSetOfItems(final Player player, final Item... items) {
        for(final Item i : items){
            player.getInventory().add(i);
            try{
                Thread.sleep(10);
            }catch(final Exception e){
                System.err.println("Test");
            }
        }
        player.getInventory().add(new Item(391, player.getInventory().freeSlots()));
    }
}
