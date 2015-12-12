package org.hyperion.rs2.model.combat.weapons;

/**
 * @author Arsen Maxyutov.
 */
public class WeaponAnimations {

    /**
     * The default weapon animations.
     */
    public static final WeaponAnimations DEFAULT_ANIMS = new WeaponAnimations(808, 819, 824, 12029, 1156);
    ;

    /**
     * The walk animation.
     */
    private final int walkAnimation;

    /**
     * The stand animation.
     */
    private final int standAnimation;

    /**
     * The run animation.
     */
    private final int runAnimation;

    /**
     * The attack animation.
     */
    private final int attackAnimation;

    /**
     * The defend animation.
     */
    private final int defendAnimation;

    /**
     * Constructs a new Weapon Animations from the specified ids.
     *
     * @param standId
     * @param walkId
     * @param runId
     * @param attackId
     * @param defendId
     */
    public WeaponAnimations(final int standId, final int walkId, final int runId, final int attackId, final int defendId) {
        standAnimation = (standId);
        walkAnimation = (walkId);
        runAnimation = (runId);
        attackAnimation = (attackId);
        defendAnimation = (defendId);
    }

    public static WeaponAnimations create(final int standId, final int walkId, final int runId, final int attackId, final int defendId) {
        return new WeaponAnimations(standId, walkId, runId, attackId, defendId);
    }

    public int getWalkAnimation() {
        return walkAnimation;
    }


    public int getStandAnimation() {
        return standAnimation;
    }


    public int getRunAnimation() {
        return runAnimation;
    }


    public int getAttackAnimation() {
        return attackAnimation;
    }

    public int getDefendAnimation() {
        return defendAnimation;
    }

}
