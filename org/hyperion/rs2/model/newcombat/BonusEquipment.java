package org.hyperion.rs2.model.newcombat;

/**
 * @author Arsen Maxyutov.
 */
public class BonusEquipment extends Container {

    /**
     * The obsidian bonus set.
     */
    public static final int[] OBSIDIAN_SET = {11128, 6528};
    /**
     * The dharok bonus set.
     */
    public static final int[] DHAROK_SET = {4716, 4718, 4720, 4722};
    /**
     * The void melee bonus set.
     */
    public static final int[] VOID_MELEE_SET = {8839, 8840, 8842, 11665};

    public BonusEquipment(final Type type, final int capacity) {
        super(type, capacity);
        // TODO Auto-generated constructor stub
    }

    /**
     * Gets the strength bonus multiplicator which is 1 by default.
     *
     * @return
     */
    public double getBonus(final Player player, final int skill) {
        final Skills skills = player.getSkills();
        double bonus = 1.0;
        if(skill == Skills.STRENGTH){
            if(this.contains(OBSIDIAN_SET))
                bonus *= 1.20;
            if(this.contains(VOID_MELEE_SET))
                bonus *= 1.15;
            else if(this.contains(DHAROK_SET)){
                final double ratio = (double) skills.getLevel(Skills.HITPOINTS) / (double) skills.getLevelForExp(Skills.HITPOINTS);
                bonus *= 2.0 - ratio;
            }
        }else if(skill == Skills.RANGED){

        }
        return bonus;
    }

}
