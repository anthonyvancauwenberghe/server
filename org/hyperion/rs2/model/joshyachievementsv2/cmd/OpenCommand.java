package org.hyperion.rs2.model.joshyachievementsv2.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTracker;

/**
 * Created by Administrator on 9/15/2015.
 */
public class OpenCommand extends Command {

    public OpenCommand(){
        super("achievements", Rank.PLAYER);
    }

    @Override
    public boolean execute(Player player, String input) throws Exception {
        if(!AchievementTracker.active)
            return false;
        player.getAchievementTracker().openInterface();
        return true;
    }
}
