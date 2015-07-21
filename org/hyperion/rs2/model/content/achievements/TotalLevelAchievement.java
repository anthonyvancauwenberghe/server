package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.AchievementData;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 7/2/2015.
 */
public class TotalLevelAchievement extends Achievement {

    private int totalLevel;

    public TotalLevelAchievement(String name, AchievementData achievementData, int totalLevel) {
        super(name, achievementData);
        this.totalLevel = totalLevel;
    }

    @Override
    public void progress(Player player) {
        if(getCurrentStep() != 1) {
            if (player.getSkills().getTotalLevel() >= totalLevel) {
                setCurrentStep(1);
                giveReward(player);
            }
        }
    }
}
