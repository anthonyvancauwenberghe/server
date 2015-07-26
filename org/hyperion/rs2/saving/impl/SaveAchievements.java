package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.AchievementData;
import org.hyperion.rs2.saving.SaveObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by User on 6/25/2015.
 */

public class SaveAchievements extends SaveObject {

    /**
     * Constructs a new SaveObject with the specified name.
     *
     * @param
     */
    public SaveAchievements(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public boolean save(Player player, BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.newLine();
        for(int i = 0; i < AchievementData.values().length; i++) {
            if(player.getAchievementsProgress().get(AchievementData.values()[i]) == null)
                writer.write(0 + "");
            else
                writer.write(player.getAchievementsProgress().get(AchievementData.values()[i]) + "");
            writer.newLine();
        }
        return false;
    }

    public void load(Player player, String values, BufferedReader reader) throws IOException {
        String line;
        int index = 0;
        while((line = reader.readLine()).length() > 0) {
            int value = Integer.parseInt(line);
            player.getAchievementsProgress().put(AchievementData.values()[index++], value);
        }
    }
}
