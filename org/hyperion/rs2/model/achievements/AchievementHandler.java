package org.hyperion.rs2.model.achievements;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.achievements.KillStreakAchievement;
import org.hyperion.rs2.model.content.achievements.SteppedAchievement;
import org.hyperion.rs2.model.content.achievements.TotalLevelAchievement;

import java.util.ArrayList;

public class AchievementHandler {

    static {
        /* Very easy achievements */
        Achievement achievement = new SteppedAchievement("Kill 5 Players", AchievementData.KILL_5);
        achievement = new SteppedAchievement("Teleport to FunPk", AchievementData.FUN_PK);
        achievement = new SteppedAchievement("New Prayers", AchievementData.NEW_PRAYERS);
        achievement = new TotalLevelAchievement("Total Level 800", AchievementData.TOTAL_800, 800);

        /* Easy achievements */
        achievement = new SteppedAchievement("Kill 25 Players", AchievementData.KILL_25);
        achievement = new SteppedAchievement("5 Dungeon Runs", AchievementData.DUNGEON_5);

        /* Medium achievements */
        achievement = new SteppedAchievement("Kill 50 Players", AchievementData.KILL_50);
        achievement = new KillStreakAchievement("Streak of 6", AchievementData.STREAK_6, 6);
        achievement = new SteppedAchievement("Win 20 Duels", AchievementData.DUEL_20);
        achievement = new SteppedAchievement("15 Dungeon Runs", AchievementData.DUNGEON_15);
        achievement = new TotalLevelAchievement("Total Level 1100", AchievementData.TOTAL_1100, 1100);

        /* Hard achievements */
        achievement = new SteppedAchievement("Kill 100 Players", AchievementData.KILL_100);
        achievement = new SteppedAchievement("Win 50 Duels", AchievementData.DUEL_50);
        achievement = new TotalLevelAchievement("Total Level 1500", AchievementData.TOTAL_1500, 1500);

        /* Very hard achievements */
        achievement = new SteppedAchievement("Kill 250 Players", AchievementData.KILL_250);
        achievement = new KillStreakAchievement("Streak of 10", AchievementData.STREAK_10, 10);
        achievement = new SteppedAchievement("50 Dungeon Runs", AchievementData.DUNGEON_50);
        achievement = new TotalLevelAchievement("Total Level 1800", AchievementData.TOTAL_1800, 1800);

        /* Legendary achievements */
        achievement = new SteppedAchievement("Kill 500 Players", AchievementData.KILL_500);
        achievement = new SteppedAchievement("200 Dungeon Runs", AchievementData.DUNGEON_200);
    }

    public static boolean achievementButton(Player player, int buttonId) {
        if(true)
            return false;
        for (int i = 29457; i <= 29462; i++) {
            if(buttonId == i) {
                Difficulty newDifficulty = Difficulty.values()[i - 29457];
                AchievementHandler.openInterface(player, newDifficulty, true);
                player.setViewingDifficulty(newDifficulty);
                return true;
            }
        }
        if(buttonId > 28885 && buttonId < 28950) {
            ArrayList<Achievement> achievements = getAchievementByDifficulty(player, player.getViewingDifficulty());
            int index = buttonId - 28886;
            player.getActionSender().sendString(28883, achievements.get(index).getName() + " Guide");
            achievements.get(index).loadInformation(player);
            if (achievements.get(index) instanceof SteppedAchievement) {
                SteppedAchievement achievement = (SteppedAchievement) achievements.get(index);
                if (achievement.getAchievementData().getSteps() > 1)
                    player.getActionSender().sendString(28989, "Current Progress: " + achievement.getCurrentStep() + "/" + achievement.getAchievementData().getSteps());
            }
            return true;
        }
        return false;
    }

    public static ArrayList<Achievement> getAchievementByDifficulty(Player player, Difficulty difficulty) {
        ArrayList<Achievement> achievements = new ArrayList<>();
        for(Achievement achievement : player.getAchievements()) {
            if(achievement == null)
                continue;
            if(achievement.getAchievementData().getDifficulty() == difficulty)
                achievements.add(achievement);
        }
        return achievements;
    }

    public static void openInterface(Player player, Difficulty difficulty, boolean refresh) {
        if(true)
            return;
        clearInterface(player);
        ArrayList<Achievement> achievements = getAchievementByDifficulty(player, difficulty);
        int interfaceId = 28886;
        for(Achievement achievement : achievements) {
            player.getActionSender().sendString(interfaceId, getTextColor(achievement.getCurrentStep(), achievement.getAchievementData().getSteps()) + achievement.getName());
            interfaceId++;
        }
        player.getActionSender().sendString(28881, difficulty.getName() + " Achievement Diary");
        if(!refresh)
            player.getActionSender().showInterface(28880);
    }

    private static void clearInterface(Player player) {
        for(int i = 28989; i < 29014; i++) {
            player.getActionSender().sendString(i, "");
        }
        for(int i = 29402; i < 29409; i++) {
            player.getActionSender().sendString(i, "");
        }
        for(int i = 28886; i <= 28886 + player.getAchievements().size(); i++) {
            player.getActionSender().sendString(i, "");
        }
        player.getActionSender().sendString(28883, "Achievement Guide");
    }

    private static String getTextColor(int currentStep, int steps) {
        if(currentStep == 0)
            return "@red@";
        else if(currentStep > 0 && currentStep < steps)
            return "@yel@";
        else
            return "@gre@";
    }

    /**
     * Progresses every achievement of this type.
     * @param player Player player
     * @param type Type type of achievement.
     */
    public static void progressAchievement(Player player, String type) {
        if(true)
            return;
        for(int i = 0; i < player.getAchievements().size(); i++) {
            Achievement achievement = player.getAchievements().get(i);
            if(achievement.getAchievementData().getType() == null) {
                if(achievement.getName().equals(type)) {
                    player.getAchievements().get(i).progress(player);
                }
            } else if(achievement.getAchievementData().getType().equals(type)) {
                player.getAchievements().get(i).progress(player);
            }
        }
    }

    public static void initAchievements(Player player, int[] progress) {
        if(true)
            return;
        for(int i = 0; i < Achievement.achievements.size(); i++) {
            player.getAchievements().add(Achievement.achievements.get(i));
            player.getAchievements().get(i).setCurrentStep(progress[i]);
        }
    }

}
