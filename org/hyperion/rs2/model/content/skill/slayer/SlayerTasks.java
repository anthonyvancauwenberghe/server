package org.hyperion.rs2.model.content.skill.slayer;

import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/2/14
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
	/*
	private static int[] level100 = {269, 749, 84, 1590, 1591, 1592, 55,};
	private static int[] level70 = {4381, 110, 3026, 3027, 3028, 6285, 52, 82, 83,};
	private static int[] level40 = {110, 1976, 125, 111, 117, 112, 119,};
	private static int[] level20 = {1265, 103, 125, 111, 117, 112,};
	private static int[] level3 = {1265, 103,};

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
		monsterForLevel.put(2783, (byte) 90); // Dark beast (drops dark bow) */
public enum SlayerTasks {

    /** The elite tasks */
    TORMENTED_DEMON(Difficulty.ELITE, 1, 634, 8349),
    MITHRIL_DRAGON(Difficulty.ELITE, 1, 522, 5363),
    ICE_WYRM(Difficulty.ELITE, 1, 522, 9463),
    KING_BLACK_DRAGON(Difficulty.ELITE, 1, 722, 50),

    /** The hard tasks */
    BLACK_DEMON(Difficulty.HARD, 1, 170, 84),
    GORAK(Difficulty.HARD, 68, 120, 6218),
    HELL_HOUND(Difficulty.HARD, 1, 119, 6210),
    DARK_BEAST(Difficulty.HARD, 95, 347,2783),
    ABYSSAL_DEMON(Difficulty.HARD, 85, 240, 2783),
    GARGOYLE(Difficulty.HARD, 75, 190, 1610),
    STEEL_DRAGON(Difficulty.HARD, 1, 263, 1592),

    /** Medium tasks  */
     FIRE_GIANT(Difficulty.MEDIUM, 1, 353, 1582),
     BLOOD_VELD(Difficulty.MEDIUM, 50, 277, 6215, 1618, 1619),
     JELLY(Difficulty.MEDIUM, 52, 102, 1637),
     BLUE_DRAGON(Difficulty.MEDIUM, 1, 205, 55),

    /** Easy tasks */

    SKELETON(Difficulty.EASY, 1, 34, 89,459),
    EXPERIMENT(Difficulty.EASY, 1, 42, 1678, 1677),
    ROCK_CRAB(Difficulty.EASY, 1, 57, 1265,1266),
    CHAOS_DRUID(Difficulty.EASY, 1, 60, 181),
    GIANT_BAT(Difficulty.EASY, 1, 35, 78);

    private final Difficulty difficulty;
    private final int slayerLevel, slayerXP;
    private final List<Integer> ids = new ArrayList<>();
    private static final int EXP_MULTIPLIER = 3;

    private SlayerTasks(final Difficulty difficulty, final int slayerLevel, final int slayerXP, final int... ids) {
        this.difficulty = difficulty;
        this.slayerLevel = slayerLevel;
        this.slayerXP = slayerXP * EXP_MULTIPLIER;
        for(int i : ids)
            this.ids.add(i);
    }

    public Difficulty getDifficulty() { return difficulty; }
    public List<Integer> getIds() { return ids; }
    public int getXP() { return slayerXP; }

    public static SlayerTasks forLevel(final int slayerLevel) {
        final SlayerTasks task = values()[Misc.random(values().length - 1)];
        if(slayerLevel >= task.slayerLevel && Math.abs(slayerLevel - task.difficulty.slayerLevel) <= 25) //ensure task is not too easy and they have the level for it
            return task;
        else return forLevel(slayerLevel);
    }

    static enum Difficulty {
        EASY(10, 40, 0, 4),
        MEDIUM(20, 80, 26, 6),
        HARD(60, 150, 50, 8),
        ELITE(5, 10, 75, 8);

        private final int minAmount, maxAmount, slayerLevel, slayerPoints;
        private Difficulty(final int minAmount, final int maxAmount, final int slayerLevel, final int slayerPoints) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.slayerLevel = slayerLevel;
            this.slayerPoints = slayerPoints;
        }

        public int getAmount() {
            return minAmount + Misc.random(maxAmount - minAmount);
        }

        public int getSlayerPoints() {
            return slayerPoints;
        }


    }

    @Override public String toString() {
        return TextUtils.titleCase(super.toString().replaceAll("_", " ").toLowerCase());
    }
    /**
     * Get all npc ids for slayer npc death for contenttemplate
     * @return all slayer task npc ids
     */
    public static final int[] getTasks() {
        final List<Integer> list = new ArrayList<>();
        for(final SlayerTasks tasks : SlayerTasks.values()) {
            for(final int i : list)
                list.add(i);
        }
        final int[] n = new int[list.size()];
        for(int i = 0; i < n.length; i++) {
            n[i] = list.get(i);
        }
        return n;
    }

    public static SlayerTasks taskForId(int npcID) {
        for(final SlayerTasks task : SlayerTasks.values()) {
            if(task.ids.contains(npcID))
                return task;
        }
        return null;
    }

    public static int getLevelById(int npcID) {
        SlayerTasks task = taskForId(npcID);
        if(task != null)
            return task.slayerLevel;
        else return 0;
    }

}