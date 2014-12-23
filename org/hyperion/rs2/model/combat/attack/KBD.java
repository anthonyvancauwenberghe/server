package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Combat;
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
	        /*
			 * 
			 * 393 = KBD = red projectile
				394 = KBD = green projectile
				395 = KBD = white projectile
				396 = KBD = blue projectile
			 */
			if(attackId > 6 && distance <= (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2)) {
				//melee
				n.cE.doAnim(n.getDefinition().getAtkEmote(1));
				n.cE.predictedAtk = (System.currentTimeMillis() + 1000);
				Combat.npcAttack(n, attack, Combat.random(35), 500, 0);
			} else if(attackId == 0) {
				//posison
				n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				n.cE.predictedAtk = (System.currentTimeMillis() + 1000);
				Combat.npcAttack(n, attack, 12, 1000, 1);
				if(n.getDefinition().getId() == 50)
					Combat.poisonEntity(attack);
				Combat.npcRangeAttack(n, attack, 394, 40, false);
			} else if(attackId == 1) {
				//ice freeze
				n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				n.cE.predictedAtk = (System.currentTimeMillis() + 2000);
				Combat.npcAttack(n, attack, Combat.random(45), 1000, 1);
				if(n.getDefinition().getId() == 50)
					attack.setFreezeTimer(20000);
				Combat.npcRangeAttack(n, attack, 396, 40, false);
			} else {
				//firebreath
				n.cE.doAnim(n.getDefinition().getAtkEmote(2));
				n.cE.predictedAtk = (System.currentTimeMillis() + 3000);

				boolean antiFire = (System.currentTimeMillis() - attack.getPlayer().antiFireTimer < 360000) && attack.getPlayer().superAntiFire;
                    if (System.currentTimeMillis() - attack.getPlayer().antiFireTimer < 360000 && attack.getPlayer().superAntiFire)
                        Combat.npcAttack(n, attack.getOpponent(), 0, 1000, 3);
				else if(attack.getPlayer().getEquipment().get(Equipment.SLOT_SHIELD) != null && (attack.getPlayer().getEquipment().get(Equipment.SLOT_SHIELD).getId() == 1540 || attack.getPlayer().getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11283 || attack.getPlayer().getEquipment().get(Equipment.SLOT_SHIELD).getId() == 11284))
					if(System.currentTimeMillis() - attack.getPlayer().antiFireTimer < 360000)
						Combat.npcAttack(n, attack, 0, 1000, 3);
					else
						Combat.npcAttack(n, attack, Combat.random(10), 1000, 3);
				else if(System.currentTimeMillis() - attack.getPlayer().antiFireTimer < 360000)
					Combat.npcAttack(n, attack, Combat.random(10), 1000, 3);
				else
					Combat.npcAttack(n, attack, Combat.random(60), 1000, 3);
				Combat.npcRangeAttack(n, attack, 393, 40, false);
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