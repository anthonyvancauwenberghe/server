package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.AchievementHandler;
import org.hyperion.rs2.model.content.achievements.SteppedAchievement;
import org.hyperion.rs2.saving.SaveObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by User on 6/25/2015.
 */

public class SaveAchievements {

    /**
     * Constructs a new SaveObject with the specified name.
     *
     * @param
     */
    public boolean save(Player player, BufferedWriter writer) throws IOException {
        /* Updates all of the states to the array */
        for(int i = 0; i < player.getAchievements().size(); i++) {
            Achievement achievement = player.getAchievements().get(i);
            if(achievement != null) {
                if (achievement instanceof SteppedAchievement) {
                    player.setAchievementProgress(i, ((SteppedAchievement) achievement).getCurrentStep());
                } else {
                    player.setAchievementProgress(i, achievement.getCurrentStep());
                }
            }
        }
       // writer.write(getName());
        writer.newLine();
        for(int i = 0; i < player.getAchievementProgress().length; i++) {
            writer.write(player.getAchievementProgress()[i] + "");
            writer.newLine();
        }
        return false;
    }

    public void load(Player player, String values, BufferedReader reader) throws IOException {
        String line;
        int index = 0;
        while((line = reader.readLine()).length() > 0) {
            int value = Integer.parseInt(line);
            player.setAchievementProgress(index++, value);
        }
    }
}
