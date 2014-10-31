package org.hyperion.rs2.model.combat.attack;

import org.hyperion.map.WorldMap;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;

public class GodWarsBandos implements Attack {

	public static final int BANDOS_BOSS = 6260;

	public String getName() {
		return "GodWarsBandos";
	}

	public int handleAttack(final NPC n, final CombatEntity attack) {
		int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
		if(distance < (10 + ((n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2))) {
			if(n.cE.predictedAtk > System.currentTimeMillis()) {
				if(n.getDefinition().getId() == 6261) {
					if(distance > (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2)) {
						return 0;
					}
				}
				return 6;//we dont want to reset attack but just wait another 500ms or so...
			}
			if(n.getDefinition().getId() == 6265) {
				if(! WorldMap.projectileClear(n.getLocation().getZ(), n.getDefinition().sizeX() + n.getLocation().getX(), n.getDefinition().sizeY() + n.getLocation().getY(), attack.getAbsX(), attack.getAbsY()))
					return 0;
				//range
				n.cE.doAnim(n.getDefinition().getAtkEmote(0));
				n.cE.predictedAtk = (System.currentTimeMillis() + 3000);
				Combat.npcAttack(n, attack, Combat.random(19), 700, 1);
				Combat.npcRangeAttack(n, attack, 1206, 43, false);

			} else if(n.getDefinition().getId() == 6263) {
				if(! WorldMap.projectileClear(n.getLocation().getZ(), n.getDefinition().sizeX() + n.getLocation().getX(), n.getDefinition().sizeY() + n.getLocation().getY(), attack.getAbsX(), attack.getAbsY()))
					return 0;
				//mage
				n.cE.doAnim(n.getDefinition().getAtkEmote(0));
				n.cE.predictedAtk = (System.currentTimeMillis() + 3000);
				Combat.npcAttack(n, attack, Combat.random(17), 700, 2);
				Combat.npcRangeAttack(n, attack, 1203, 43, true);
			} else if(n.getDefinition().getId() == 6261) {

				//melee
				if(distance <= (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2)) {
					n.cE.doAnim(n.getDefinition().getAtkEmote(0));
					n.cE.predictedAtk = (System.currentTimeMillis() + 3000);
					Combat.npcAttack(n, attack, Combat.random(25), 500, 0);
				} else
					return 0;
			} else if(n.getDefinition().getId() == BANDOS_BOSS) {
				int attackId = Combat.random(4);
				//melee
				if(attackId >= 2) {
					if(distance <= (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2)) {
						n.cE.doAnim(n.getDefinition().getAtkEmote(0));
						n.cE.predictedAtk = (System.currentTimeMillis() + 3000);
						Combat.npcAttack(n, attack, Combat.random(64), 500, 0);
					} else
						return 0;
				} else {
					if(! WorldMap.projectileClear(n.getLocation().getZ(), n.getDefinition().sizeX() + n.getLocation().getX(), n.getDefinition().sizeY() + n.getLocation().getY(), attack.getAbsX(), attack.getAbsY()))
						return 0;
					//range
					n.cE.doAnim(n.getDefinition().getAtkEmote(1));
					n.cE.predictedAtk = (System.currentTimeMillis() + 3000);
					for(Player p : World.getWorld().getRegionManager().getLocalPlayers(n)) {
						int distance2 = p.getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
						if(distance2 <= 10) {
							p.cE.doGfx(1177, 0);
							Combat.npcAttack(n, p.cE, Combat.random(37), 500, 1);
						}
					}
				}
				if(distance > (1 + (n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2))
					return 0;
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
		int[] j = {6265, 6263, 6261, 6260,};
		return j;
	}

}
