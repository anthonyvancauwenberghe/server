package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 7/2/2015.
 */
public class TotalLevelAchievement extends Achievement {

    private int totalLevel, pkp, dp;

    public TotalLevelAchievement(String name, Difficulty difficulty, int interfaceId, int totalLevel, int state, int pkp, int dp, String[] reward, String... instructions) {
        super(name, difficulty, interfaceId, reward, instructions);
        this.totalLevel = totalLevel;
        this.pkp = pkp;
        this.dp = dp;
        setState(state);

    }

    @Override
    public void progress(Player player) {
        if(getState() != 2) {
            if (player.getSkills().getTotalLevel() >= totalLevel) {
                setState(2);
                giveReward(player, pkp, dp);
            }
        }
    }
}
