package org.hyperion.rs2.model.content.specialareas;

import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Equipment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 5/1/15
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NIGGERUZ extends SpecialArea {

    public static final int OBJECT_ID = 6856;
    protected static final GameObjectDefinition DEFINITION = GameObjectDefinition.forId(OBJECT_ID);

    private final int height;

    public NIGGERUZ(int height) {

        this.height = height;

    }

    @Override
    public boolean canSpawn() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPkArea() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean inArea(int x, int y, int z) {
        return z == height &&
                x > 2271 && y >= 4680 && x <= 2287 && y <= 4711 ;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exit(Player player) {
        player.setTeleportTarget(getDefaultLocation());
    }

    public void initObjects(final List<GameObject> manager) {
        for(int i = 4680; i <= 4711; i++) {
            if(i == 4696 || i == 4697) continue;
            manager.add(new GameObject(DEFINITION, Location.create(2261, i, height), 10, 1, false));
        }

        manager.add(new GameObject(GameObjectDefinition.forId(6951), Location.create(2271, 4680, height), 4, 0, false));


    }

    @Override
    public Location getDefaultLocation() {
        return Location.create(2258, 4696, height);
    }


    @Override
    public abstract String canEnter(Player player);
}
