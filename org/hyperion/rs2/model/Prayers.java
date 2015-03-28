package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;

import java.util.*;

/**
 * @author Arsen Maxyutov.
 */
public class Prayers {
	
	public static final int[] OVERHEADS =  {
		16, 17, 18, 21, 22, 23, 37, 38, 39
	};

	/**
	 * The prayer id.
	 */
	public static final int
			PRAYER_THICK_SKIN = 0,
			PRAYER_BURST_OF_STRENGTH = 1,
			PRAYER_ATTACK_LOW = 2,
			PRAYER_SHARP_EYE = 3,
			PRAYER_MAGE_LOW = 4,
			PRAYER_ROCK_SKIN = 5,
			PRAYER_SUPERHUMAN_STRENGTH = 6,
			PRAYER_ATTACK_MID = 7,
			PRAYER_RAPID_RESTORE = 8,
			PRAYER_RAPID_HEAL = 9,
			PRAYER_PROTECT_ITEM = 10,
			PRAYER_HAWK_EYE = 11,
			PRAYER_MAGE_MID = 12,
			PRAYER_TEEL_SKIN = 13,
			PRAYER_ULTIMATE_STRENGTH = 14,
			PRAYER_ATTACK_HIGH = 15,
			PRAYER_PROTECT_FROM_MAGE = 16,
			PRAYER_PROTECT_FROM_RANGE = 17,
			PRAYER_PROTECT_FROM_MELEE = 18,
			PRAYER_EAGLE_EYE = 19,
			PRAYER_MAGE_HIGH = 20,
			PRAYER_RETRIBUTION = 21,
			PRAYER_REDEMPTION = 22,
			PRAYER_SMITE = 23,
			PRAYER_CHIVALRY = 24,
			PRAYER_PIETY = 25,
            PRAYER_RIGOUR = 26,
            PRAYER_AUGURY = 27,
			CURSE_PROTECT_ITEM = 30,
			CURSE_SAP_WARRIOR = 31,
			CURSE_SAP_RANGED = 32,
			CURSE_SAP_MAGE = 33,
			CURSE_SAP_SPIRIT = 34,
			CURSE_BERSERKER = 35,
			CURSE_DEFLECT_SUMMONING = 36,
			CURSE_DEFLECT_MAGIC = 37,
			CURSE_DEFLECT_RANGED = 38,
			CURSE_DEFLECT_MELEE = 39,
			CURSE_LEECH_ATTACK = 40,
			CURSE_LEECH_RANGED = 41,
			CURSE_LEECH_MAGE = 42,
			CURSE_LEECH_DEFENCE = 43,
			CURSE_LEECH_STRENGTH = 44,
			CURSE_LEECH_ENERGY = 45,
			CURSE_LEECH_SPECIAL = 46,
			CURSE_WRATH = 47,
			CURSE_SOULSPLIT = 48,
			CURSE_TURMOIL = 49,
			SIZE = 50;


	/**
	 * The bonus prayers.
	 */
	public static final BonusPrayer[] BONUS_PRAYERS = {
			new BonusPrayer(PRAYER_THICK_SKIN, Skills.DEFENCE, 1.05),
			new BonusPrayer(PRAYER_ROCK_SKIN, Skills.DEFENCE, 1.10),
			new BonusPrayer(PRAYER_TEEL_SKIN, Skills.DEFENCE, 1.15),
			new BonusPrayer(PRAYER_ATTACK_LOW, Skills.ATTACK, 1.05),
			new BonusPrayer(PRAYER_ATTACK_MID, Skills.ATTACK, 1.10),
			new BonusPrayer(PRAYER_ATTACK_HIGH, Skills.ATTACK, 1.15),
			new BonusPrayer(PRAYER_BURST_OF_STRENGTH, Skills.STRENGTH, 1.05),
			new BonusPrayer(PRAYER_SUPERHUMAN_STRENGTH, Skills.STRENGTH, 1.10),
			new BonusPrayer(PRAYER_ULTIMATE_STRENGTH, Skills.STRENGTH, 1.15),
			new BonusPrayer(PRAYER_SHARP_EYE, Skills.RANGED, 1.05),
			new BonusPrayer(PRAYER_HAWK_EYE, Skills.RANGED, 1.10),
			new BonusPrayer(PRAYER_EAGLE_EYE, Skills.RANGED, 1.15),
			new BonusPrayer(PRAYER_MAGE_LOW, Skills.MAGIC, 1.05),
			new BonusPrayer(PRAYER_MAGE_MID, Skills.MAGIC, 1.10),
			new BonusPrayer(PRAYER_MAGE_HIGH, Skills.MAGIC, 1.15),
			new BonusPrayer(PRAYER_CHIVALRY, Skills.DEFENCE, 1.18),
			new BonusPrayer(PRAYER_CHIVALRY, Skills.STRENGTH, 1.18),
			new BonusPrayer(PRAYER_PIETY, Skills.STRENGTH, 1.23),
            new BonusPrayer(PRAYER_RIGOUR, Skills.RANGED, 1.25),
            new BonusPrayer(PRAYER_AUGURY, Skills.MAGIC, 1.25),
	    /*
		 * Curses
		 */
			//Strength
			new BonusPrayer(CURSE_SAP_WARRIOR, Skills.STRENGTH, 1.05),
			new BonusPrayer(CURSE_LEECH_STRENGTH, Skills.STRENGTH, 1.15),
			new BonusPrayer(CURSE_TURMOIL, Skills.STRENGTH, 1.32),
			//Ranged
			new BonusPrayer(CURSE_SAP_RANGED, Skills.RANGED, 1.05),
			new BonusPrayer(CURSE_LEECH_RANGED, Skills.RANGED, 1.15),
			//Magic
			new BonusPrayer(CURSE_SAP_MAGE, Skills.MAGIC, 1.05),
			new BonusPrayer(CURSE_LEECH_MAGE, Skills.MAGIC, 1.15),
			//Attack
			new BonusPrayer(CURSE_SAP_WARRIOR, Skills.ATTACK, 1.05),
			new BonusPrayer(CURSE_LEECH_ATTACK, Skills.ATTACK, 1.15),
			new BonusPrayer(CURSE_TURMOIL, Skills.ATTACK, 1.20),
			//Defense
			new BonusPrayer(CURSE_SAP_WARRIOR, Skills.DEFENCE, 1.05),
			new BonusPrayer(CURSE_LEECH_DEFENCE, Skills.DEFENCE, 1.15),
			new BonusPrayer(CURSE_TURMOIL, Skills.DEFENCE, 1.20),
	};

	/**
	 * The prayers array.
	 */
	private boolean[] prayers;

	/**
	 * The default prayerbook flag.
	 */
	private boolean default_prayerbook;


	/**
	 * The disabled prayers list
	 */
	private final ArrayList<DisabledPrayer> disabledPrayers = new ArrayList<>();

	/**
	 * The leeches queue.
	 */
	private Queue<Integer> leeches = new LinkedList<Integer>();

	/**
	 * Constructs a new Prayers.
	 *
	 * @param curses
	 */
	public Prayers(boolean default_prayerbook) {
		prayers = new boolean[SIZE];
		this.default_prayerbook = default_prayerbook;
	}

	/**
	 * Checks if the prayer with the given index is enabled.
	 *
	 * @param index
	 * @return
	 */
	public boolean isEnabled(int index) {
		return prayers[index];
	}

	/**
	 * Sets the prayer's enabled state to the specified boolean.
	 *
	 * @param index
	 * @param b
	 */
	public boolean setEnabled(int index, boolean b) {
		if(index >= CURSE_LEECH_ATTACK && index <= CURSE_LEECH_SPECIAL) {
			if(b)
				leeches.add(index);
			else
				leeches.remove(index);
		}
		prayers[index] = b;
		return true;
	}

	/**
	 * Checks if an extra item is being protected upon death.
	 *
	 * @return
	 */
	public boolean isProtectingItem() {
		return prayers[PRAYER_PROTECT_ITEM] || prayers[CURSE_PROTECT_ITEM];
	}


	/**
	 * Polls a leech curse.
	 *
	 * @return a leech curse id if there's active leeches, otherwise null.
	 */
	public Integer pollLeech() {
		Integer leech = leeches.poll();
		if(leech != null)
			leeches.add(leech);
		return leech;
	}

	/**
	 * Checks if there's active leeches enabled.
	 *
	 * @return
	 */
	public boolean isLeeching() {
		return activeLeeches() > 0;
	}

	/**
	 * Gets the amount of active leeches.
	 *
	 * @return
	 */
	public int activeLeeches() {
		return leeches.size();
	}

	/**
	 * Checks if the prayer book is the default one.
	 *
	 * @return
	 */
	public boolean isDefaultPrayerbook() {
		return default_prayerbook;
	}

	/**
	 * Changes the prayerbook.
	 */
	public void setPrayerbook(boolean default_prayerbook) {
		this.default_prayerbook = default_prayerbook;
	}

	/**
	 * Clears all active prayers and leeches.
	 */
	public void clear() {
		for(int i = 0; i < SIZE; i++)
			prayers[i] = false;
		leeches.clear();
	}

	/**
	 * Gets the bonus for the specified skill id.
	 *
	 * @param skill
	 * @return
	 */
	public double getBonus(int skill) {
		for(BonusPrayer bonusprayer : BONUS_PRAYERS) {
			if(bonusprayer.getSkill() == skill && prayers[bonusprayer.getId()]) {
				return bonusprayer.getBonus();
			}
		}
		return 1.0;
	}

	static {
		CommandHandler.submit(new Command("prayers", Rank.PLAYER) {

			@Override
			public boolean execute(Player player, String input) throws Exception {
				for(int i = 0; i < Prayers.SIZE; i++) {
					if(player.getPrayers().isEnabled(i))
						player.getActionSender().sendMessage("Active prayer: " + i);
				}
				return true;
			}

		});
	}

	public void disableFor(int time, int... prayers) {
		synchronized (disabledPrayers) {
			disabledPrayers.add(new DisabledPrayer(time, prayers));
		}
	}

	public ArrayList<DisabledPrayer> getDisabled() {
		return disabledPrayers;
	}

	public static class DisabledPrayer {

		private long prayersDisabledAt = -1;
		private long prayersDisabledFor = -1;
		private int[] prayersDisabled;

		public DisabledPrayer(long prayersDisabledFor, int[] prayersDisabled) {
			this.prayersDisabledAt = System.currentTimeMillis();
			this.prayersDisabledFor = prayersDisabledFor;
			this.prayersDisabled = prayersDisabled;
		}

		public long getPrayersDisabledAt() {
			return prayersDisabledAt;
		}

		public long getPrayersDisabledFor() {
			return prayersDisabledFor;
		}

		public int[] getPrayersDisabled() {
			return prayersDisabled;
		}
	}

	public static class BonusPrayer {

		private final int id;

		private final int skill;

		private final double bonus;

		/**
		 * @param id
		 * @param skill
		 * @param bonus
		 */
		public BonusPrayer(int id, int skill, double bonus) {
			this.id = id;
			this.skill = skill;
			this.bonus = bonus;
		}


		public int getSkill() {
			return skill;
		}

		public double getBonus() {
			return bonus;
		}

		public int getId() {
			return id;
		}
	}

}
