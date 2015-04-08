package org.hyperion.rs2.model.combat;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.util.Misc;

/**
 * @author SaosinHax
 */
public class SpiritShields {

	public static final int DIVINE_SPIRIT_SHIELD_ID = 13740;
	public static final int ELYSIAN_SPIRIT_SHIELD_ID = 13742;

	/**
	 * Doesn't seem to work on players? I'll check into that - lower prayer for divine?
	 */
	public static int applyEffects(CombatEntity defender, int damg) {
        if(defender == null) {
            return 0;
        }
		if(! (defender.getEntity() instanceof Player))
			return damg;
		int shieldId = CombatAssistant.getShieldId(defender.getPlayer().getEquipment());
		switch(shieldId) {
			case DIVINE_SPIRIT_SHIELD_ID:
				if(defender.getPlayer().getSkills().getLevel(Skills.PRAYER) > 0) {
					defender.getPlayer().getSkills().detractLevel(Skills.PRAYER, (int) (damg * 0.25));
					return (int) (damg * 0.75);
				}
			case ELYSIAN_SPIRIT_SHIELD_ID:
				if(Misc.random(9) <= 6)
					return (int) (damg * 0.75);
		}

		return damg;
	}
}
