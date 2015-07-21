package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.AchievementData;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 6/23/2015.
 */
public class SteppedAchievement extends Achievement {

    public SteppedAchievement(String name, AchievementData achievementData) {
        super(name, achievementData);
    }

    @Override
    public void progress(Player player) {
        if(currentStep == getAchievementData().getSteps())
            return;
        currentStep++;
        if(currentStep == getAchievementData().getSteps())
            giveReward(player);
    }

}
