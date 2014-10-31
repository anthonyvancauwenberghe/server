package org.hyperion.rs2.model.content.skill;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentTemplate;


/**
 * Slayer Class Mad Turnip
 */

public class Slayer implements ContentTemplate {

	/**
	 * Class constructor.
	 */
	public Slayer() {

	}

	private static final int EXPMULTIPLIER = 5;

	private static Map<Integer, Byte> monsterForLevel = new HashMap<Integer, Byte>();

	public static void talkMaster(Player player, int master, int npcIndex, boolean setTask) {
		player.setInteractingEntity(World.getWorld().getNPCs().get(npcIndex));
		if(player.slayerTask > 0) {
			//already has a task
			DialogueManager.openDialogue(player, 22);
		} else {
			if(setTask) {
				if(/*master == 1598 && */player.getSkills().getCombatLevel() >= 100) {
					player.slayerTask = level100[random(level100.length - 1)];
					player.slayerExp = 300;
				} else if(/*master == 1598 && */player.getSkills().getCombatLevel() >= 70) {
					player.slayerTask = level70[random(level70.length - 1)];
					player.slayerExp = 200;
				} else if(/*master == 1597 && */player.getSkills().getCombatLevel() >= 40) {
					player.slayerTask = level40[random(level40.length - 1)];
					player.slayerExp = 100;
				} else if(/*master == 1596 && */player.getSkills().getCombatLevel() >= 20) {
					player.slayerTask = level20[random(level20.length - 1)];
					player.slayerExp = 80;
				} else if(/*master == 70 && */player.getSkills().getCombatLevel() >= 3) {
					player.slayerTask = level3[random(level3.length - 1)];
					player.slayerExp = 50;
				}
				player.slayerCooldown = 120;
				player.slayerAm = 20 + random(20/*player.getSkills().getCombatLevel()*/);
				DialogueManager.openDialogue(player, 26);
			} else {
				DialogueManager.openDialogue(player, 28);
			}
		}
	}

	public static void getSlayerXp(Player player) {
		int combat = player.getSkills().getCombatLevel();
		if(combat >= 100) {
			player.slayerExp = 3500;
		} else if(combat >= 70) {
			player.slayerExp = 2000;
		} else if(combat >= 40) {
			player.slayerExp = 1000;
		} else if(combat >= 20) {
			player.slayerExp = 500;
		} else if(combat >= 3) {
			player.slayerExp = 300;
		}

	}

	public static void addSlayerPoints(Player player) {
		int combat = player.getSkills().getCombatLevel();
		if(combat >= 100) {
			player.slayerPoints += 18;
		} else if(combat >= 70) {
			player.slayerPoints += 15;
		} else if(combat >= 40) {
			player.slayerPoints += 12;
		} else if(combat >= 20) {
			player.slayerPoints += 10;
		} else if(combat >= 3) {
			player.slayerPoints += 4;
		}

	}

	public static int getSlayerLevel(int npcId) {
		if(monsterForLevel.get(npcId) != null)
			return (int) monsterForLevel.get(npcId);
		return 0;
	}

	private static int random(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}
	
	
	private static int[] level100 = {269, 749, 84, 1590, 1591, 1592, 55,};
	private static int[] level70 = {4381, 110, 3026, 3027, 3028, 6285, 52, 82, 83,};
	private static int[] level40 = {110, 1976, 125, 111, 117, 112, 119,};
	private static int[] level20 = {1265, 103, 125, 111, 117, 112,};
	private static int[] level3 = {1265, 103,};


	/**
	 * Loads the XML file of cooking.
	 *
	 * @throws FileNotFoundException
	 */

	/*
	 *vanMaster  Levels Required  Quests Required  Map
		Turael / Spria  3 Combat  None    
		Mazchna / Achtryn  20 Combat  Priest in Peril    
		Vannaka  40 Combat  None    
		Chaeldar  70 Combat  Lost City    
		Sumona  85 Combat; 35 Slayer  Smoking Kills    
		Duradel / Lapalok  100 Combat; 50 Slayer  Shilo Village    
		Kuradal  110 Combat; 75 Slayer  Ancient Cavern 
		 
	 *
	 */
	@Override
	public void init() throws FileNotFoundException {

	}


	@Override
	public int[] getValues(int type) {
		if(type == 10) {
			int[] j = {1599,};
			return j;
		}
		if(type == 1) {
			int[] j = {4155,};
			return j;
		}
		return null;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int itemId, final int slot, final int objId, final int a) {
		if(type == 1) {
			client.getActionSender().sendMessage("You have to slay " + client.slayerAm + " more " + NPCDefinition.forId(client.slayerTask).getName() + "s.");
			return true;
		}
		talkMaster(client, itemId, a, false);
		return true;
	}

	static {
		monsterForLevel.put(84, (byte) 1); // Black demons
		monsterForLevel.put(54, (byte) 1); // Black dragons
		monsterForLevel.put(55, (byte) 1); // Blue dragons
		monsterForLevel.put(1582, (byte) 1); // Fire giants
		monsterForLevel.put(6218, (byte) 1); // Goraks
		monsterForLevel.put(83, (byte) 1); // Greater demons
		monsterForLevel.put(6210, (byte) 1); // Hellhounds
		monsterForLevel.put(1591, (byte) 1); // Iron dragons
		monsterForLevel.put(5363, (byte) 1); // Mithril dragons
		monsterForLevel.put(1592, (byte) 1); // Steel dragons
		monsterForLevel.put(5361, (byte) 1); // Waterfiends
		monsterForLevel.put(6215, (byte) 50); // Bloodvelds
		monsterForLevel.put(1618, (byte) 50); // Bloodvelds
		monsterForLevel.put(1619, (byte) 50); // Bloodvelds
		monsterForLevel.put(1637, (byte) 52); // Jellies
		monsterForLevel.put(1607, (byte) 60); // Aberrant spectres
		monsterForLevel.put(1624, (byte) 65); // Dust devils (drops dragon chain)
		monsterForLevel.put(3068, (byte) 72); // Skeletal wyverns (drops dragonic visage)
		monsterForLevel.put(1610, (byte) 75); // Gargoyles (drops granite maul)
		monsterForLevel.put(1613, (byte) 80); // Nechryael (drops black mask)
		monsterForLevel.put(6221, (byte) 83); // Spiritual mages (drops d boots)
		monsterForLevel.put(6231, (byte) 83); // Spiritual mages (drops d boots)
		monsterForLevel.put(6257, (byte) 83); // Spiritual mages (drops d boots)
		monsterForLevel.put(6278, (byte) 83); // Spiritual mages (drops d boots)
		monsterForLevel.put(1615, (byte) 85); // Abyssal demons (drops whips)
		monsterForLevel.put(2783, (byte) 90); // Dark beast (drops dark bow)
	}

}