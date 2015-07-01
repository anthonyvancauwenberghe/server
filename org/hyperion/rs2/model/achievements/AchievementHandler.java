package org.hyperion.rs2.model.achievements;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.achievements.KillStreakAchievement;
import org.hyperion.rs2.model.content.achievements.SteppedAchievement;

public class AchievementHandler {

    public static final int TOTAL_ACHIEVEMENTS = 50;

    public static boolean achievementButton(Player player, int buttonId) {
        for (int i = 29457; i <= 29462; i++) {
            if(buttonId == i) {
                Difficulty newDifficulty = Difficulty.values()[i - 29457];
                AchievementHandler.openInterface(player, newDifficulty, true);
                player.setViewingDifficulty(newDifficulty);
                return true;
            }
        }
        Achievement[] achievements = getAchievementByDifficulty(player, player.getViewingDifficulty());
        for (int i = 0; i < achievements.length; i++) {
            if(achievements[i].getInterfaceId() == buttonId) {
                player.getActionSender().sendString(28883, achievements[i].getName() + " Guide");
                achievements[i].loadInformation(player);
                if(achievements[i] instanceof SteppedAchievement) {
                    SteppedAchievement achievement = (SteppedAchievement) achievements[i];
                    player.getActionSender().sendString(28989, "Current Progress: " + achievement.getCurrentStep() + "/" + achievement.getSteps());
                }
                return true;
            }
        }
        return false;
    }

    public static Achievement[] getAchievementByDifficulty(Player player, Difficulty difficulty) {
        Achievement[] achievements = new Achievement[difficulty.getNumberOfAchievements()];
        int index = 0;
        for(Achievement achievement : player.getAchievements()) {
            if(achievement == null)
                continue;
            if(achievement.getDifficulty() == difficulty)
                achievements[index++] = achievement;
        }
        return achievements;
    }

    public static void openInterface(Player player, Difficulty difficulty, boolean refresh) {
        clearInterface(player);
        Achievement[] achievements = getAchievementByDifficulty(player, difficulty);
        for(Achievement achievement : achievements) {
            player.getActionSender().sendString(achievement.getInterfaceId(), getTextColor(achievement.getState()) + achievement.getName());
        }
        player.getActionSender().sendString(28881, difficulty.getName() + " Achievement Diary");
        if(!refresh)
            player.getActionSender().showInterface(28880);
    }

    public static int getAchievementIndex(Player player, String name) {
        for(int i = 0; i < player.getAchievements().length; i++) {
            if(player.getAchievements()[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static void progress(Player player, String name) {
        int index = getAchievementIndex(player, name);
        if(player.getAchievements()[index].getState() != 2)
            player.getAchievements()[index].progress(player);
    }

    public static void progressAchievement(Player player, String type) {
        switch(type) {
            case "Kill Player":
                AchievementHandler.progress(player, "Kill 5 Players");
                AchievementHandler.progress(player, "Kill 25 Players");
                AchievementHandler.progress(player, "Kill 50 Players");
                AchievementHandler.progress(player, "Kill 100 Players");
                AchievementHandler.progress(player, "Kill 250 Players");
                AchievementHandler.progress(player, "Kill 500 Players");
                break;
            case "Kill Streak":
                AchievementHandler.progress(player, "Streak of 6");
                AchievementHandler.progress(player, "Streak of 10");
                break;
        }
    }

    private static void clearInterface(Player player) {
        for(int i = 28989; i < 29014; i++) {
            player.getActionSender().sendString(i, "");
        }
        for(int i = 29402; i < 29409; i++) {
            player.getActionSender().sendString(i, "");
        }
        for(int i = 28886; i <= 28886 + TOTAL_ACHIEVEMENTS; i++) {
            player.getActionSender().sendString(i, "");
        }
        player.getActionSender().sendString(28883, "Achievement Guide");
    }

    private static String getTextColor(int state) {
        switch(state) {
            case 0:
                return "@red@";
            case 1:
                return "@yel@";
            case 2:
                return "@gre@";
        }
        return null;
    }

    public static void initAchievements(Player player, int[] progress) {
        int index = 0;
        int interfaceId = 28886;

        /* Very easy difficulty achievements*/
        Difficulty difficulty = Difficulty.VERY_EASY;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 5 Players", difficulty, interfaceId++, 5, progress[index - 1], 250, 0, new String[] {"", "", "250 pkp"},
                "", "", "You must defeat 5 players in the wilderness", "in order to complete this achievement.");

        /* Easy difficulty achievements*/
        interfaceId = 28886;
        difficulty = Difficulty.EASY;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 25 Players", difficulty, interfaceId++, 25, progress[index - 1], 500, 0, new String[] {"", "", "500 pkp"},
                "", "", "You must defeat 25 players in the wilderness", "in order to complete this achievement.");

        /* Medium difficulty achievements*/
        interfaceId = 28886;
        difficulty = Difficulty.MEDIUM;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 50 Players", difficulty, interfaceId++, 50, progress[index - 1], 1000, 0, new String[] {"", "100x Rocktail", "1000 pkp"},
                "", "", "You must defeat 50 players in the wilderness", "in order to complete this achievement.") {
            @Override
            public void giveReward(Player player, int pkp, int dp) {
                super.giveReward(player, pkp, dp);
                player.getBank().add(new Item(15272, 100));
                player.getActionSender().sendMessage("100 Rocktails were added to your bank.");
            }
        };
        player.getAchievements()[index++] = new KillStreakAchievement("Streak of 6", difficulty, interfaceId++, 6, progress[index - 1], new String[] {"", "50 donator points", "500 pkp"},
                "", "", "You must kill 6 players in the wilderness", "without dying once. If you die before you", "reach 6 kills, your killstreak will reset!");

        /* Hard difficulty achievements*/
        interfaceId = 28886;
        difficulty = Difficulty.HARD;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 100 Players", difficulty, interfaceId++, 100, progress[index - 1], 1750, 100, new String[] {"", "100 donator points", "1750 pkp"},
                "", "", "You must defeat 100 players in the wilderness", "in order to complete this achievement.");

        /* Very hard difficulty achievements*/
        interfaceId = 28886;
        difficulty = Difficulty.VERY_HARD;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 250 Players", difficulty, interfaceId++, 250, progress[index - 1], 2500, 150, new String[] {"", "150 donator points", "2500 pkp"},
                "", "", "You must defeat 250 players in the wilderness", "in order to complete this achievement.");
        player.getAchievements()[index++] = new KillStreakAchievement("Streak of 10", difficulty, interfaceId++, 10, progress[index - 1], new String[] {"", "50 donator points", "500 pkp", "20 Overload Potions"},
                "", "", "You must kill 10 players in the wilderness", "without dying once. If you die before you", "reach 10 kills, your killstreak will reset!");

        /* Legendary difficulty achievements */
        interfaceId = 28886;
        difficulty = Difficulty.LEGENDARY;
        player.getAchievements()[index++] = new SteppedAchievement("Kill 500 Players", difficulty, interfaceId++, 500, progress[index - 1], 3500, 200, new String[] {"", "200 donator points", "3500 pkp", "1x TokHaar-Kal"},
                "", "", "You must defeat 500 players in the wilderness", "in order to complete this achievement.") {
            @Override
            public void giveReward(Player player, int pkp, int dp) {
                super.giveReward(player, pkp, dp);
                player.getBank().add(new Item(19111, 1));
                player.getActionSender().sendMessage("1 TokHaar-Kal was added to your bank.");
            }
        };
    }

}
