package org.hyperion.rs2.model;


import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.SummoningData;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.misc.BunyipEvent;
import org.hyperion.rs2.model.content.skill.Summoning;
import org.hyperion.util.Misc;

/**
 * @author Vegas/Arsen/Linus/Jolt/Flux <- Same Person
 */
public class SummoningMonsters {

	// public static ArrayList<NPC> Monsters = new ArrayList<NPC>();
	
	public static final int[] SUMMONING_MONSTERS = {7343, 6823, 6869};
	public static NPCDefinition loadDefinition(int id) {
		int[] bonus = new int[10];
		switch(id) {
		case 7343:
			bonus = new int[10];
			for(int i = 0; i < bonus.length; i++)
				bonus[i] = 500;
			int[] atk = {8183};
			return NPCDefinition.create(7343, 300, 20, bonus, 8184, 8185, atk, 2, "Steel Titan", 0);
		case 6823:
			bonus = new int[10];
			for(int i = 0; i < bonus.length; i++)
				bonus[i] = 150;
			int[] atk1 = {6376};
			return NPCDefinition.create(id, 300, 81, bonus, 6377, 6376, atk1, 2, "Unicorn Stallion", 0);
		case 6869:
			return NPCDefinition.create(id, 250, 139, new int[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10}, 
					8305, 8304, new int[]{8303}, 1, "Wolpertinger", 0);
		}
		return null;
	}
	public static void runEvent(Player p) {
		//for (Player p : World.getWorld().getPlayers()) {
		if(p == null || p.cE.summonedNpc == null) {
			return;
		}

		p.SummoningCounter--;
	        /*if(p.cE.summonedNpc.cE.getOpponent() == null)
				p.cE.summonedNpc.cE.face(p.getLocation().getX(), p.getLocation().getY());*/
		int distance = Misc.distance(p.getLocation().getX(), p
				.getLocation().getY(), p.cE.summonedNpc.getLocation().getX(),
				p.cE.summonedNpc.getLocation().getY());
		if(distance > 8) {
			Location newlocation = p.getLocation().getCloseLocation();
			p.cE.summonedNpc.setTeleportTarget(newlocation);
			//p.cE.summonedNpc.setLocation(newlocation);
			p.cE.summonedNpc.ownerId = p.getIndex();
			p.cE.summonedNpc.playGraphics(Graphic.create(1315));
			p.cE.summonedNpc.cE.setOpponent(null);
			Combat.follow(p.cE.summonedNpc.cE, p.cE);
			p.cE.summonedNpc.setInteractingEntity(p);
			
		} else if(/*distance > p.cE.summonedNpc.getDefinition().sizeX() && */distance >= 2) {
			if(p.cE.summonedNpc.cE.getOpponent() == null || p.cE.summonedNpc.cE.getOpponent().getEntity().isDead()) {
				p.cE.summonedNpc.cE.setOpponent(null);
				Combat.follow(p.cE.summonedNpc.cE, p.cE);
			}
		}
		if(p.SummoningCounter <= 0) {
			World.getWorld().resetSummoningNpcs(p);
		} else if(p.SummoningCounter == 100) {
			p.getActionSender().sendMessage(
					"Your Summoning monster will die in a minute..");
		} else if(p.SummoningCounter == 200) {
			p.getActionSender().sendMessage(
					"Your Summoning monster will die in 2 minutes");
		}
		//}
	}


	public static void SummonNewNPC(final Player p, int npcID, int itemId) {
		int req = SummoningData.getRequirementForNpcId(npcID);
		if(p.getSkills().getLevel(23) < req) {
			p.getActionSender().sendMessage(
					"You need a Summoning Level of " + req
							+ " to summon this npc.");
			return;
		}
		if(p.duelAttackable > 0) {
			p.getActionSender().sendMessage("You can't do this in the duel arena");
			return;
		}
		if(Summoning.isBoB(npcID))
			BoB.dropBoB(p.getLocation(), p);
		for(int i = 0; i < BOB_NPCS.length; i++) {
			if(BOB_NPCS[i][0] == npcID || BOB_NPCS[i][0] - 1 == npcID)
				p.setBob(BOB_NPCS[i][1]);
		}
		ContentEntity.deleteItemA(p, itemId, 1);
		if(p.cE.summonedNpc != null) {
			World.getWorld().resetSummoningNpcs(p);
		}
		SummonNewNPC2(p, npcID);
	}

	public static void SummonNewNPC2(final Player p, int npcID) {

		final NPC monster = World
				.getWorld()
				.getNPCManager()
				.addNPC(p.getLocation().getX(), p.getLocation().getY(),
						p.getLocation().getZ(), npcID, - 1);
		p.SummoningCounter = SummoningData.getTimerById(npcID);
		if(npcID == 6813) {
			World.getWorld().submit(new BunyipEvent(p));
		}
		monster.ownerId = p.getIndex();
		Combat.follow(monster.getCombat(), p.getCombat());
		monster.summoned = true;
		p.cE.summonedNpc = monster;
		monster.playGraphics(Graphic.create(1315));

	}

	public static final int[][] BOB_NPCS = {
			//id,space,specific item
			{6807, 3},//Thorny snail
			{6868, 9},//bull ant
			{6795, 12},//spirit terrorbird
			{6816, 18},//war tortoise
			{6874, 30},//pack yack
			{7350, 28},//abyssal titan
			{6822, 18},//abyssal lurker
			{6820, 7},//abyssal parasite
	};


}
