package org.hyperion.rs2.model.content.specialareas.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.specialareas.KBDZoneArea;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 5/1/15
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HybridZone extends KBDZoneArea {

    public HybridZone() {
        super(4);
    }

    public String canEnter(Player player) {
        final Item shield = player.getEquipment().get(Equipment.SLOT_SHIELD);
        if(shield != null && (shield.getId() == 13740 || shield.getId() == 13744))
            return "You cannot bring divines to this area";
        if(!player.getSpellBook().isAncient())
            return "You must be on ancients to be here";
        return "";
    }
}
