package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Room {

    private final List<NPC> npcs = new ArrayList<>();

    private Room child;

    public final Dungeon dungeon;

    public final RoomDefinition definition;

    public boolean boss;

    public Room(final Dungeon dungeon, final RoomDefinition def) {
        this.dungeon = dungeon;
        this.definition = def;
    }

    public boolean cleared() {
        for(final NPC npc : npcs) {
            if(!npc.isDead())
                return false;
        }
        return true;
    }

    public Room getChild(){
        return child;
    }

    public void setChild(final Room child) {
        this.child = child;
    }

    public Room addNPC(final NPC npc) {
        npcs.add(npc);
        return this;
    }

}
