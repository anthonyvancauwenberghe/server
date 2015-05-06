package org.hyperion.rs2.model.content.specialareas.impl;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;
import org.hyperion.rs2.pf.Point;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 5/1/15
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class HybridZone extends NIGGERUZ {

    private final Point cornerSW = new Point(2970, 3605),
    cornerNE = new Point(2983, 3616);


    public HybridZone() {
        super(4);
    }

    @Override
    public int getPkLevel() {
        return -1;
    }

    public String canEnter(Player player) {
        final Item shield = player.getEquipment().get(Equipment.SLOT_SHIELD);
        if(shield != null && (shield.getId() == 13740 || shield.getId() == 13744))
            return "You cannot bring divines to this area";
        if(!player.getSpellBook().isAncient())
            return "You must be on ancients to be here";
        return "";
    }

    @Override
    public void initObjects(final List<GameObject> list) {

        for(int x = cornerSW.getX() ;x <= cornerNE.getX(); x++) {
            if(x != 2976 && x != 2977 )
                list.add(new GameObject(DEFINITION, Location.create(x, cornerSW.getY(), height), 10, 2, false));
            list.add(new GameObject(DEFINITION, Location.create(x, cornerNE.getY(), height), 10, 0, false));
        }

        for(int y = cornerSW.getY(); y < cornerNE.getY(); y++) {
            list.add(new GameObject(DEFINITION, Location.create(cornerNE.getX(), y, height), 10, 1, false));
            list.add(new GameObject(DEFINITION, Location.create(cornerSW.getX(), y, height), 10, 3, false));

        }



    }

    @Override
    public Location getDefaultLocation() {
        return Location.create(2975, 3610, height);
    }

    @Override
    public boolean inArea(int x, int y, int z) {
        return z == height &&
                (x > cornerSW.getX() && y >= cornerSW.getY() && x <= cornerNE.getX() && y <= cornerNE.getY()) ;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
