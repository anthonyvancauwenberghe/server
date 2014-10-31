package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.util.Misc;

public class CommonMagicAttack implements Attack {

	public String getName() {
		return "CommonMagicAttack";
	}

	public int handleAttack(final NPC n, final CombatEntity attack) {
		int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
		if(distance < (8 + ((n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2))) {
			Combat.npcAttack(n, attack, Misc.random(35), 1000, Misc.random(2));
			if(Misc.random(3) == 0 && attack.getEntity() instanceof Player) {
				handleEffect(attack.getPlayer());
			}
			return 5;
		} else if(n.getLocation().isWithinDistance(n.cE.getOpponent().getEntity().getLocation(), 14)) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int[] npcIds() {
		int[] j = {,};
		return j;
	}
	
	public void handleEffect(final Player player) {
		final int playerX = player.getLocation().getX()-4;
		final int playerY = player.getLocation().getY()-4;
		player.setTeleportTarget(Location.create(playerX + Misc.random(10), playerY + Misc.random(10), 0));
	}
}
