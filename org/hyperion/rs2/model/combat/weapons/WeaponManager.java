package org.hyperion.rs2.model.combat.weapons;

import java.util.HashMap;
import java.util.Map;

public class WeaponManager {

    private static final WeaponManager manager = new WeaponManager();
    private final Map<Integer, Weapon> weapons = new HashMap<Integer, Weapon>();

    private WeaponManager() {

    }

    public static WeaponManager getManager() {
        return manager;
    }

    public Weapon get(final int id) {
        return weapons.get(id);
    }

    public void put(final int id, final Weapon weapon) {
        weapons.put(id, weapon);
    }

    public int size() {
        return weapons.size();
    }
}
