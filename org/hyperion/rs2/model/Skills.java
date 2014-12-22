package org.hyperion.rs2.model;

import java.util.Calendar;

import org.hyperion.rs2.event.impl.OverloadStatsEvent;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.util.Misc;


/**
 * Represents a player's skill and experience levels.
 *
 * @author Graham Edgecombe
 */
public class Skills {


	/**
	 * The largest allowed experience.
	 */
	public static final int MAXIMUM_EXP = 200000000;

	/**
	 * The skill names.
	 */
	public static final String[] SKILL_NAME = {"Attack", "Defence",
			"Strength", "Hitpoints", "Range", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching",
			"Fishing", "Firemaking", "Crafting", "Smithing",
			"Mining", "Herblore", "Agility", "Thieving",
			"Slayer", "Farming", "Runecrafting", "Construction", "Hunter", "Summoning", "Dungeoneering"};


	/**
	 * The bonus skill.
	 */
	public static int BONUS_SKILL = ((Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 8) % (Skills.SKILL_COUNT-7)) + 7;

	/**
	 * The number of skills.
	 */
	public static final int SKILL_COUNT = SKILL_NAME.length;

	/**
	 * Constants for the skill numbers.
	 */
	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2,
			HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9,
			FISHING = 10, FIREMAKING = 11, CRAFTING = 12,
			SMITHING = 13, MINING = 14, HERBLORE = 15,
			AGILITY = 16, THIEVING = 17, SLAYER = 18,
			FARMING = 19, RUNECRAFTING = 20, CONSTRUCTION = 21, HUNTER = 22, SUMMONING = 23, DUNGEONINEERING = 24;

	/**
	 * The player object.
	 */
	private Player player;

	/**
	 * The levels array.
	 */
	private int[] levels = new int[SKILL_COUNT];

	/**
	 * Holds all levels that have been changed during gameplay session,
	 * only those levels are being updated to the highscores.
	 */
	private boolean[] changedLevels = new boolean[SKILL_COUNT];


	public int[] getLevels() {
		return levels;
	}

	/**
	 * The experience array.
	 */
	private int[] exps = new int[SKILL_COUNT];

	public int[] getXps() {
		return exps;
	}

	/**
	 * Returns the actual levels array.
	 */
	public int[] getRealLevels() {
		int[] levelsarray = new int[levels.length];
		for(int i = 0; i < exps.length; i++) {
			levelsarray[i] = getLevelForExp(i);
		}
		return levelsarray;
	}

	/**
	 * Creates a skills object.
	 *
	 * @param player The player whose skills this object represents.
	 */
	public Skills(Player player) {
		this.player = player;
		reset();

	}

	public void reset() {
		for(int i = 0; i < SKILL_COUNT; i++) {
			levels[i] = 1;
			exps[i] = 0;
		}
		levels[3] = 10;
		exps[3] = 1184;
	}

	/**
	 * Gets the total level.
	 *
	 * @return The total level.
	 */
	public int getTotalLevel() {
		int total = 0;
		for(int i = 0; i < levels.length; i++) {
			total += getLevelForExp(i);
		}
		return total;
	}

	/**
	 * Gets the total experience.
	 *
	 * @return The total exp.
	 */
	public long getTotalExp() {
		long totalexp = 0L;
		for(int exp : exps) {
			totalexp += exp;
		}
		return totalexp;
	}

	/**
	 * Gets the experience for a requested level.
	 * @return Minimum experience required for that level.
	 */

	public int getCombatLevel(){
		return (int) Math.floor(
				(0.25*((getLevelForExp(DEFENCE))+(getLevelForExp(HITPOINTS))+Math.floor((getLevelForExp(PRAYER))/2))) +
				/*(0.125 * getLevelForExp(23)) +*/
				Math.max(
						(0.325*((getLevelForExp(ATTACK))+(getLevelForExp(STRENGTH)))),
						Math.max(
								(0.325*(Math.floor((getLevelForExp(RANGED))/2)+(getLevelForExp(RANGED)))),
								(0.325*(Math.floor((getLevelForExp(MAGIC))/2)+(getLevelForExp(MAGIC))))
						)
				)
		);
	}

	/**
	 * Sets a skill.
	 *
	 * @param skill The skill id.
	 * @param level The level.
	 * @param exp   The experience.
	 */
	public void setSkill(int skill, int level, int exp) {
		levels[skill] = level;
		exps[skill] = exp;
		player.getActionSender().sendSkill(skill);
	}

	/**
	 * Sets a level.
	 *
	 * @param skill The skill id.
	 * @param level The level.
	 */
	public void setLevel(int skill, int level) {
		levels[skill] = level;
		player.getActionSender().sendSkill(skill);
	}

	/**
	 * Sets experience.
	 *
	 * @param skill The skill id.
	 * @param exp   The experience.
	 */
	public void setExperience(int skill, int exp) {
		int oldLvl = getLevelForExp(skill);
		exps[skill] = exp;
		player.getActionSender().sendSkill(skill);
		int newLvl = getLevelForExp(skill);
		if(oldLvl != newLvl) {
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * Increments a level.
	 *
	 * @param skill The skill to increment.
	 */
	public void incrementLevel(int skill) {
		levels[skill]++;
		player.getActionSender().sendSkill(skill);
	}

	/**
	 * Decrements a level.
	 *
	 * @param skill The skill to decrement.
	 */
	public void decrementLevel(int skill) {
		levels[skill]--;
		player.getActionSender().sendSkill(skill);
	}

	/**
	 * Detracts a given level a given amount.
	 *
	 * @param skill  The level to detract.
	 * @param amount The amount to detract from the level.
	 */
	public void detractLevel(int skill, int amount) {
		if(levels[skill] == 0) {
			amount = 0;
		}
		if(amount > levels[skill]) {
			amount = levels[skill];
		}
		levels[skill] = levels[skill] - amount;
		player.getActionSender().sendSkill(skill);
	}

    public void incrementLevel(int skill, int amount) {
        if(levels[skill] == 0) {
            amount = 0;
        }
        if(amount + levels[skill] > 99) {
            amount = (amount + levels[skill])-99;
        }
        levels[skill] = levels[skill] + amount;
        player.getActionSender().sendSkill(skill);
    }

	/**
	 * Normalizes a level (adjusts it until it is at its normal value).
	 *
	 * @param skill The skill to normalize.
	 */
	public void normalizeLevel(int skill) {
		int norm = getLevelForExp(skill);
		if(levels[skill] > norm) {
			if(isCombatSkill(skill)) {
				if(player.getExtraData().get(OverloadStatsEvent.KEY) != null)
					return;
			}
			levels[skill]--;
			player.getActionSender().sendSkill(skill);

		} else if(levels[skill] < norm) {
			levels[skill]++;
			player.getActionSender().sendSkill(skill);
		}
	}

	public void speedNormalizeLevel(int skill) {
		int norm = getLevelForExp(skill);
		if(levels[skill] > norm) {
			levels[skill] = norm;
			player.getActionSender().sendSkill(skill);
		}
	}

	/**
	 * Gets a level.
	 *
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevel(int skill) {
		return (int) levels[skill];
	}

	/**
	 * Gets a level.
	 *
	 * @param skill The skill id.
	 * @return The level.
	 */
	public double getLevel2(int skill) {
		return levels[skill];
	}

	/**
	 * Where Xps[n] is the minimum experience for level n.
	 * e.g. Minimum xp for level 10 is Xps[10].
	 */
	private static int[] EXPERIENCE_PER_LEVEL = {- 1, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154,
			1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018,
			5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833,
			16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224,
			41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721,
			101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254,
			224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428,
			496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
			1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068,
			2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294,
			4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614,
			8771558, 9684577, 10692629, 11805606, 13034431,
	};

	/**
	 * Calculates MAX HP which increases when using Nex Armors.
	 *
	 * @return
	 */
	public int calculateMaxLifePoints() {
		int lifePoints = getLevelForExp(3);
		if(!Location.inAttackableArea(player)) {
			if(Rank.hasAbility(player, Rank.SUPER_DONATOR)) {
				lifePoints *= 1.12;
			}
			if(Rank.hasAbility(player, Rank.DONATOR)) {
				lifePoints *= 1.08;
			}
		}
		lifePoints += FightPits.getLifePointBoost(player);
		if(player.getEquipment().get(Equipment.SLOT_HELM) != null) {
			switch(player.getEquipment().get(Equipment.SLOT_HELM).getId()) {
				case 19713:
				case 19716:
				case 19719:
					lifePoints += 7;
			}
		}
		if(player.getEquipment().get(Equipment.SLOT_CHEST) != null)
			switch(player.getEquipment().get(Equipment.SLOT_CHEST).getId()) {
				case 19714:
				case 19717:
				case 19720:
					lifePoints += 20;
			}
		if(player.getEquipment().get(Equipment.SLOT_BOTTOMS) != null)
			switch(player.getEquipment().get(Equipment.SLOT_BOTTOMS).getId()) {
				case 19715:
				case 19718:
				case 19721:
					lifePoints += 13;
			}
		return lifePoints;
	}

	/**
	 * Gets a level by experience.
	 *
	 * @param skill The skill id.
	 * @return The level.
	 */
	public int getLevelForExp(int skill) {
		if(skill > SKILL_COUNT - 1)
			return 1;
		int exp = exps[skill] + 1;
		for(int i = 99; i >= 0; i--) {
			if(exp >= EXPERIENCE_PER_LEVEL[i]) {
				return i;
			}
		}
		return 1;
		
		/*int points = 0;
		int output = 0;

		for (int lvl = 1; lvl < 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp)
				return lvl;
		}
		return 99;*/
	}


	public int getExperienceTilLevel(int skill, int targetLevel) {
		int startXp = getXPForLevel(getLevelForExp(skill));
		startXp = getXPForLevel(targetLevel) - startXp;
		return startXp;
	}

	/**
	 * Gets a experience from the level.
	 *
	 * @param level The level.
	 * @return The experience.
	 */
	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for(int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if(lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	/**
	 * Gets experience.
	 *
	 * @param skill The skill id.
	 * @return The experience.
	 */
	public int getExperience(int skill) {
		return exps[skill];
	}

	public int[] getExps() {
		return exps;
	}

	/**
	 * Adds experience.
	 *
	 * @param skill The skill.
	 * @param exp   The experience to add.
	 */
	public void addExperience(int skill, double exp) {


        try {

            synchronized (World.getWorld()) {

                Calendar c = Calendar.getInstance();
                int dayOfYear = (c.get(Calendar.DAY_OF_YEAR) + 4);
                int bonusSkill = (dayOfYear % (Skills.SKILL_COUNT-8)) + 7;

                int lastSkillChange = -1;
                if(World.getWorld().getProperty("lastSkillChange") != null) {
                    lastSkillChange = World.getWorld().getProperty("lastSkillChange");
                }

                if(dayOfYear != lastSkillChange) {
                    if(bonusSkill != BONUS_SKILL) {
                        while(bonusSkill == 21) {
                            bonusSkill = Misc.random(Skills.SKILL_COUNT-8)+7;
                        }
                        BONUS_SKILL = bonusSkill;
                        for(Player player1 : World.getWorld().getPlayers())
                            player1.getQuestTab().sendBonusSkill();
                        PushMessage.pushGlobalMessage("The bonus skill has been changed to: "+Misc.getSkillName(Skills.BONUS_SKILL));
                    }
                    World.getWorld().putProperty("lastSkillChange", dayOfYear);
                }
            }


        } catch (Exception e){
        }
        

        if(player.xpLock && skill <= MAGIC)
            return;
        if(player.getEquipment().getItemId(Equipment.SLOT_HELM) == 17279) {
        	exp *= 1.02;
        }
        if(skill > 6)
            exp *= 1.25;
		if(skill == BONUS_SKILL)
			exp *= 2;
		int oldLevel = (int) getLevelForExp(skill);
		exps[skill] += exp;
		if(exps[skill] > MAXIMUM_EXP) {
			exps[skill] = MAXIMUM_EXP;
			return;
		}
		changedLevels[skill] = true;
		int newLevel = getLevelForExp(skill);
		int levelDiff = newLevel - oldLevel;
		if(levelDiff > 0) {
			player.getActionSender().levelUp(skill);
			player.playGraphics(Graphic.create(199));
			levels[skill] += levelDiff;
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
		player.getActionSender().sendSkill(skill);
	}

	public void destroy() {
		player = null;
	}


	/**
	 * Checks if the specified skill has been modified.
	 *
	 * @param skill
	 * @return
	 */
	public boolean hasChanged(int skill) {
		return changedLevels[skill];
	}


	public static boolean isCombatSkill(int id) {
		switch(id) {
			case 0:
			case 1:
			case 2:
			case 4:
			case 6:
				return true;
		}
		return false;
	}
}
