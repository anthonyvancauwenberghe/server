package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 6/29/2015.
 */
public class KillStreakAchievement extends Achievement {

    private int killCount;

    public KillStreakAchievement(String name, Difficulty difficulty, int interfaceId, int killCount, int state, String[] reward, String... instructions) {
        super(name, difficulty, interfaceId, reward, instructions);
        this.killCount = killCount;
        setState(state);
    }

    @Override
    public void progress(Player player) {
        if(getState() != 2) {
            if (player.getKillCount() >= killCount) {
                setState(2);
                this.giveReward(player, 500, 50);
            }
        }
    }

    @Override
    public void giveReward(Player player, int pkp, int dp) {
        super.giveReward(player, pkp, dp);
        if(player.getKillCount() == 10) {
            player.getBank().add(new Item(15332, 20));
            player.getActionSender().sendMessage("20 Overloads were added to your bank.");
        }
    }

}
