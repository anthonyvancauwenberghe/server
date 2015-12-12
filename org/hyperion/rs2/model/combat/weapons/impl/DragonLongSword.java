package org.hyperion.rs2.model.combat.weapons.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.SpecialBar;
import org.hyperion.rs2.model.combat.weapons.SpecialWeapon;

/**
 * @author Jack Daniels.
 */
public class DragonLongSword extends Longsword implements SpecialWeapon {

    /**
     * The weapon id.
     */
    public static final int WEAPON_ID = 1302;

    /**
     * The gfx id.
     */
    public static final int GFX_ID = 2117;

    /**
     * The animation id.
     */
    public static final int ANIM_ID = 12033;

    /**
     * The special drain.
     */
    public static final int SPECIAL_DRAIN = SpecialBar.FULL / 4;

    /**
     * Constructs a new DragonLongSword SpecialWeapon.
     *
     * @param id
     */
    public DragonLongSword(final int id) {
        super(id);
    }

    @Override
    public boolean specialAttack(final Player player) {
        //do a bunch of stuff
        return true;
    }

    @Override
    public int getSpecialDrain(final Player player) {
        return SPECIAL_DRAIN;
    }

}
