package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatCalculation;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.container.Equipment;

public class KBD implements Attack {

	public String getName() {
        return "KBD";
	}

	public int handleAttack(NPC n, CombatEntity attack) {
		int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
		if(distance < (7 + ((n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2))) {
			if(n.cE.predictedAtk > System.currentTimeMillis()) {
				return 6;//we dont want to reset attack but just wait another 500ms or so...
			}
            n.getDefinition().getBonus()[4] = 400;
            n.getDefinition().getBonus()[3] = 400;
			int attackId = Combat.random(10);
            for(final Player p : attack.getEntity().getRegion().getPlayers()) {
	        /*
			 * 
			 * 393 = KBD = red projectile
				394 = KBD = green projectile
				395 = KBD = white projectile
				396 = KBD = blue projectile
			 */
			    if(attackId > 4 && distance <= (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2)) {
				//melee
				    n.cE.doAnim(n.getDefinition().getAtkEmote(1));
				    n.cE.predictedAtk = (System.currentTimeMillis() + 2000);
				    Combat.npcAttack(n, p.cE, CombatCalculation.getCalculatedDamage(n, p, Combat.random(47), Constants.MELEE, 47), 500, Constants.MELEE);
			    } else if(attackId <= 1) {
				    //posison
				    n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				    n.cE.predictedAtk = (System.currentTimeMillis() + 2000);
				    Combat.npcAttack(n, p.cE, CombatCalculation.getCalculatedDamage(n, p, Combat.random(57), Constants.RANGE, 57), 1000, Constants.RANGE);
				    if(n.getDefinition().getId() == 50)
					    Combat.poisonEntity(p.cE);
				    Combat.npcRangeAttack(n, p.cE, 394, 40, false);
			    } else if(attackId > 1 && attackId <= 4) {
				//ice freeze
				    n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				    n.cE.predictedAtk = (System.currentTimeMillis() + 1800);
				    Combat.npcAttack(n, p.cE, CombatCalculation.getCalculatedDamage(n, p, Combat.random(45), Constants.MAGE, 45), 1000, Constants.MAGE);
				    if(n.getDefinition().getId() == 50 && Combat.random(2) == 1) {
                        if(p.cE.canBeFrozen())
					        p.cE.setFreezeTimer(20000);
                    }
				    Combat.npcRangeAttack(n, p.cE, 396, 40, false);
			    } else {
				//firebreath
				    n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				    n.cE.predictedAtk = (System.currentTimeMillis() + 1800);

				    boolean antiFire = (System.currentTimeMillis() - p.antiFireTimer < 360000) && p.superAntiFire;
                    if (System.currentTimeMillis() - p.antiFireTimer < 360000 && p.superAntiFire) {
                            Combat.npcAttack(n, p.cE.getOpponent(), 0, 1000, 3);
                    } else if(p.getEquipment().get(Equipment.SLOT_SHIELD) != null && (p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 1540 || p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283 || p.getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284)) {
					    if(System.currentTimeMillis() - p.antiFireTimer < 360000) {
						    Combat.npcAttack(n, p.cE, 0, 1000, 3);
                        } else
						    Combat.npcAttack(n, p.cE, Combat.random(10), 1000, 3);
                    } else if(System.currentTimeMillis() - p.antiFireTimer < 360000)
					    Combat.npcAttack(n, p.cE, Combat.random(10), 1000, 3);
				    else
					    Combat.npcAttack(n, p.cE, Combat.random(60), 1000, 3);
				    Combat.npcRangeAttack(n, p.cE, 393, 40, false);
			    }
            }

			return 5;
		} else if(n.getLocation().isWithinDistance(n.cE.getOpponent().getEntity().getLocation(), 15)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int[] npcIds() {
		int[] j = {50, 5363};
		return j;
	}
}