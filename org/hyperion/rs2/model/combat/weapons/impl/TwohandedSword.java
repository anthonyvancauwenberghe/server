package org.hyperion.rs2.model.combat.weapons.impl;

import org.hyperion.Server;
import org.hyperion.rs2.model.combat.Constants;
import org.hyperion.rs2.model.combat.weapons.Weapon;
import org.hyperion.rs2.model.combat.weapons.WeaponAnimations;

public class TwohandedSword extends Weapon {

    public static final int WEAPON_SPEED = 3600;

    public static final WeaponAnimations WEAPON_ANIMS = Server.OLD_SCHOOL ? new WeaponAnimations(4300, 4306, 4305, 4307, WeaponAnimations.DEFAULT_ANIMS.getDefendAnimation()) : new WeaponAnimations(7047, 7046, 7039, 7041, WeaponAnimations.DEFAULT_ANIMS.getDefendAnimation());

    public TwohandedSword(final int id) {
        super(id, Constants.MELEE, WEAPON_SPEED, true, false, WEAPON_ANIMS);
    }
}
