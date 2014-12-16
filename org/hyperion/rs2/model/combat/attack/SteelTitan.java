package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.combat.Constants;

public class SteelTitan implements Attack {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Steel Titan";
	}

	@Override
	public int[] npcIds() {
		// TODO Auto-generated method stub
		return new int[] {7343, 7344};
	}

	@Override
	public int handleAttack(NPC n, CombatEntity attack) {
		int distance = n.getLocation().distance(attack.getEntity().getLocation());
		if(n.cE.predictedAtk > System.currentTimeMillis())
			return 6;	
		attack.doAtkEmote();

		n.cE.predictedAtk = System.currentTimeMillis() + 2200;
		if(distance > 2 && distance < 8) {
			Combat.npcAttack(n, attack, Combat.random(22), 1700, Constants.RANGE);
		} else if (distance < 8) {
			Combat.npcAttack(n, attack, Combat.random(17), 700, Constants.MELEE);
		} else if (distance < 11) {
            return 0;
        } else if (distance >= 11) {
            return 1;
        }
		return 5;
	}

}
