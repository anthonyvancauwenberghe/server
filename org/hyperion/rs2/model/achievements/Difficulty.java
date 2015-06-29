package org.hyperion.rs2.model.achievements;

public enum Difficulty {

    VERY_EASY(1, "Very Easy"), EASY(1, "Easy"), MEDIUM(2, "Medium"), HARD(1, "Hard"), VERY_HARD(2, "Very Hard"), LEGENDARY(1, "Legendary");

    private int numberOfAchievements;
    private String name;

    Difficulty(int numberOfAchievements, String name) {
        this.numberOfAchievements = numberOfAchievements;
        this.name = name;
    }

    public int getNumberOfAchievements() {
        return numberOfAchievements;
    }

    public String getName() { return name; }

}
