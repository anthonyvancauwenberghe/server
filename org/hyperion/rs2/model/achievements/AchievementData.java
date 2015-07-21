package org.hyperion.rs2.model.achievements;

import org.hyperion.rs2.model.Item;

public enum AchievementData {

    KILL_5("Kill", Difficulty.VERY_EASY, 5, 250, 0,
            new String[] {"", "", "You must defeat 5 players in the wilderness", "in order to complete this achievement."}, new String[] {"", "", "250 pkp"}, null),
    KILL_25("Kill", Difficulty.EASY, 25, 500, 0,
            new String[] {"", "", "You must defeat 25 players in the wilderness", "in order to complete this achievement."}, new String[] {"", "", "500 pkp"}, null);

    private int steps, pkp, dp;
    private String type;
    private String[] instructions, rewards;
    private Difficulty difficulty;
    private Item[] items;

    AchievementData(String type, Difficulty difficulty, int steps, int pkp, int dp, String[] instructions, String[] rewards, Item[] items) {
        this.type = type;
        this.difficulty = difficulty;
        this.steps = steps;
        this.pkp = pkp;
        this.dp = dp;
        this.instructions = instructions;
        this.rewards = rewards;
        this.items = items;
    }

    public int getSteps() {
        return steps;
    }

    public int getPkp() {
        return pkp;
    }

    public int getDp() {
        return dp;
    }

    public String getType() {
        return type;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public String[] getRewards() {
        return rewards;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Item[] getItems() {
        return items;
    }

}