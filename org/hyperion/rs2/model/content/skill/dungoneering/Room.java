package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.Damage;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.World;

import java.awt.*;
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
    private boolean initialized;
    public boolean boss;

    public final Dungeon dungeon;
    public final RoomDefinition definition;

    public Room(final Dungeon dungeon, final RoomDefinition def) {
        this.dungeon = dungeon;
        this.definition = def;
    }

    public boolean cleared() {
        for(final NPC npc : npcs) {
            if(!npc.isDead())
                return false;
        }
        return initialized;
    }

    public Room getChild(){
        return child;
    }

    public void setChild(final Room child) {
        this.child = child;
    }

    public void initialize() {
        if(initialized)
            return;
        initialized = true;
        int npcCount = boss ? 1 : dungeon.difficulty.spawns;
        for(int i = 0; i < npcCount; i++) {
            final Point loc = definition.randomLoc();
            final NPC npc = World.getWorld().getNPCManager().addNPC(Location.create(loc.x, loc.y, dungeon.heightLevel), boss ? dungeon.difficulty.getBoss() : dungeon.difficulty.getRandomMonster(), -1);
            npc.agreesiveDis = 10;
            npcs.add(npc);
        }
    }

    public Location getSpawnLocation() {
        return Location.create(definition.x, definition.y, dungeon.heightLevel);
    }

    public void destroy() {
        initialized = false;
        boss = false;
        for(NPC npc : npcs) {
            if(!npc.isDead()) {
                npc.serverKilled = true;
                npc.inflictDamage(new Damage.Hit(npc.health, Damage.HitType.NORMAL_DAMAGE, 0), null);
            }
        }
        npcs.clear();
    }

}
