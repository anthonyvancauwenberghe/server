package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.AchievementData;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 6/29/2015.
 */
public class KillStreakAchievement extends Achievement {

    private int killCount;

    public KillStreakAchievement(String name, AchievementData achievementData, int killCount) {
        super(name, achievementData);
        this.killCount = killCount;
    }

    @Override
    public void progress(Player player) {
        if(getCurrentStep() != 1) {
            if (player.getKillCount() >= killCount) {
                setCurrentStep(1);
                giveReward(player);
            }
        }
    }

}
