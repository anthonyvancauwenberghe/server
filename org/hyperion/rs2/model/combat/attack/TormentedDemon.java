package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.util.Misc;


/**
 * @author Simplemind/Hoodlum
 */
public class TormentedDemon implements Attack {


	public String getName() {
		return "TormentedDemon";
	}

	@Override
	public int[] npcIds() {
		int[] j = {8349};
		return j;
	}
	
	public static int getReduction(NPC npc, Player player,int originalDamg) {
		if(npc == null || player == null || npc.getDefinition().getId() != 8349)
			return originalDamg;
		if(Misc.random(3) == 1) { //1/4 chance of soak
			originalDamg = originalDamg/3;
			player.sendMessage("The tormented demon soaks in your damage!");
			npc.health += originalDamg;
			if(npc.health > npc.maxHealth)
				npc.health = npc.maxHealth;
		} else if(Misc.random(3) == 1)
			originalDamg = originalDamg/2;
		return originalDamg;
	}

	public static final int DEATH_EMOTE = 10924;
	public static final int MAGE_EMOTE = 10918;
	public static final int RANGE_EMOTE = 10919;
	public static final int MELEE_EMOTE = 10922;

	private static final int maxMelee = 19, maxRange = 27, maxMage = 27;


	public int handleAttack(final NPC n, final CombatEntity attack) {
		int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
		if(distance < (3 + ((n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2))) {
			if(n.cE.predictedAtk > System.currentTimeMillis()) {
				return 6;
			}


			int attackId = Combat.random(3);
			if(attackId == 0 && distance - ((n.getDefinition().sizeX() + n.getDefinition().sizeY()) / 2) > 1)
				attackId = 1 + Combat.random(1);
			if(attackId == 0) {
				//melee attack
				n.cE.doAnim(MELEE_EMOTE);
				n.cE.doGfx(1886);
				n.cE.predictedAtk = (System.currentTimeMillis() + 2200);
				Combat.npcAttack(n, attack, maxMelee, 500, 0);
			} else if(attackId == 1) {
				n.cE.doAnim(RANGE_EMOTE);
				n.cE.predictedAtk = (System.currentTimeMillis() + 2400);
				World.getWorld().submit(new Event(1000) {
					@Override
					public void execute() {
						//range attack
						n.getCombat().doGfx(1885, 0);
						Combat.npcAttack(n, attack, Combat.random(maxRange), 1200, 1);
						this.stop();
					}
				});
			} else if(attackId == 2) {
				//mage attack
				n.cE.doAnim(MAGE_EMOTE);
				n.cE.predictedAtk = (System.currentTimeMillis() + 3300);
				World.getWorld().submit(new Event(1500) {
					@Override
					public void execute() {
						//offset values for the projectile
						int offsetY = ((n.cE.getAbsX() + n.cE.getOffsetX()) - attack.getAbsX()) * - 1;
						int offsetX = ((n.cE.getAbsY() + n.cE.getOffsetY()) - attack.getAbsY()) * - 1;
						//find our lockon target
						int hitId = attack.getSlotId((Entity) n);
						//extra variables - not for release
						int distance = attack.getEntity().getLocation().distance((Location.create(n.cE.getEntity().getLocation().getX() + n.cE.getOffsetX(), n.cE.getEntity().getLocation().getY() + n.cE.getOffsetY(), n.cE.getEntity().getLocation().getZ())));
						int timer = 1;
						int min = 16;
						if(distance > 8) {
							timer += 2;
						} else if(distance >= 4) {
							timer++;
						}
						min -= (distance - 1) * 2;
						int speed = 75 - min;
						int slope = 7 + distance;
						//create the projectile
                        if(attack.getPlayer() != null){
                            attack.getPlayer().getActionSender().createGlobalProjectile(n.cE.getAbsY() + n.cE.getOffsetY(), n.cE.getAbsX() + n.cE.getOffsetX(), offsetY, offsetX, 50, speed, 1883, 99, 35, hitId, slope);
                            attack.getPlayer().getActionSender().createGlobalProjectile(n.cE.getAbsY() + n.cE.getOffsetY(), n.cE.getAbsX() + n.cE.getOffsetX(), offsetY, offsetX, 50, speed + 10, 1884, 99, 35, hitId, slope);
                            attack.getPlayer().getActionSender().createGlobalProjectile(n.cE.getAbsY() + n.cE.getOffsetY(), n.cE.getAbsX() + n.cE.getOffsetX(), offsetY, offsetX, 50, speed + 20, 1884, 99, 35, hitId, slope);
                            Combat.npcAttack(n, attack, Combat.random(maxMage), 500, 2);
                        }
						this.stop();
					}
				});
			}
			return 5;
		} else if(n.getLocation().isWithinDistance(n.cE.getOpponent().getEntity().getLocation(), 100)) {
			return 0;
		} else {
			return 1;
		}
	}

}