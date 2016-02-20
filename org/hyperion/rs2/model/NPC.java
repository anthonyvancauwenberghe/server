package org.hyperion.rs2.model;

import org.hyperion.engine.task.impl.NpcDeathTask;
import org.hyperion.map.WorldMap;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.shops.LegendaryStore;

/**
 * <p>Represents a non-player character in the in-game world.</p>
 *
 * @author Graham Edgecombe
 */
public class NPC extends Entity {


	/**
	 * The definition.
	 */
	private final NPCDefinition definition;

	public NPC(NPCDefinition npcdefinition, int respawntime, Location loc) {
		health = 10;
		maxHealth = 10;
		npcDeathTimer = - 1;
		agressiveDis = - 1;
		ownerId = - 1;
		serverKilled = false;
		definition = npcdefinition;
		if(respawntime >= - 1) {
			npcDeathTimer = respawntime;
		} else {
			npcDeathTimer = npcdefinition.spawnTime();
		}
		maxHealth = npcdefinition.maxHp();
		health = npcdefinition.maxHp();
		spawnLocation = loc;
		setLocation(loc);
		isHidden(false);
	}


	public String lastAttacker = "";


	private final Location spawnLocation;

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * Gets the NPC definition.
	 *
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return definition;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	public int inflictDamage(int damg, Entity source, boolean poison, int style) {
		HitType h1 = HitType.NORMAL_DAMAGE;
        if(source instanceof Player && LegendaryStore.ThirdAgeSet.setFor(style).has(((Player) source).getEquipment())) {
            damg *= 1.20;
        }
		if(damg > health) {
			damg = health;
		}
		if(poison)
			h1 = HitType.POISON_DAMAGE;
		else if(damg <= 0)
			h1 = HitType.NO_DAMAGE;
		Hit h2 = new Hit(damg, h1, style);
		inflictDamage(h2, source);
		return damg;
	}

	public void inflictDamage(Hit inc, Entity source) {
		if(! getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if(! getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			} else {
				getDamage().setHit3(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_3);
			}
		}
		health -= inc.getDamage();
		if(health <= 0) {
			if(! this.isDead()) {
				charm = NPCManager.getCharms(this.definition.getId(), this.definition.getName());
				World.submit(new NpcDeathTask(this));
			}
			this.setDead(true);
		}
	}

	@Override
	public void inflictDamage(int damage, HitType type) {
		// TODO Auto-generated method stub

	}

	public int health = 10;
	public int maxHealth = 10;

	public int npcDeathTimer = - 1;

	public int agressiveDis;
	public int ownerId;
	public boolean serverKilled;

	public int walkToXMax = getLocation().getX();
	public int walkToXMin = getLocation().getX();
	public int walkToYMax = getLocation().getY();
	public int walkToYMin = getLocation().getY();
	public boolean randomWalk = false;
	public int bones;
	public int charm;
	public boolean summoned = false;

	public static void randomWalk(NPC npc) {
		if(npc.cE.isFrozen() || ! npc.randomWalk || Combat.random(5) != 1) {
			return;
		}
		int walkToX = npc.getLocation().getX() + (Combat.random(1) == 0 ? 1 : - 1);
		int walkToY = npc.getLocation().getY() + (Combat.random(1) == 0 ? 1 : - 1);
		if(walkToX > npc.walkToXMax)
			walkToX = npc.getLocation().getX();
		else if(walkToX < npc.walkToXMin)
			walkToX = npc.getLocation().getX();
		if(walkToY > npc.walkToYMax)
			walkToY = npc.getLocation().getY();
		else if(walkToY < npc.walkToYMin)
			walkToY = npc.getLocation().getY();
		int size = npc.getDefinition().sizeX() * 2;
		boolean cant = false;
		if(size <= 0) size = 1;
		for(int i = 0; i < size; i++) {
			for(int i2 = 0; i2 < size; i2++) {
				if(! WorldMap.checkPos(npc.getLocation().getZ(), npc.getLocation().getX() + i, npc.getLocation().getY() + i2, walkToX + i, walkToY + i2, 0)) {
					cant = true;
				}
			}
		}
		if(! cant) {
			npc.getWalkingQueue().reset();
			npc.getWalkingQueue().addStep(walkToX, walkToY);
			npc.getWalkingQueue().finish();
		}
	}
}
