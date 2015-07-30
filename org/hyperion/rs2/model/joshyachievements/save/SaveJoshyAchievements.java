package org.hyperion.rs2.model.joshyachievements.save;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveJoshyAchievements extends SaveString{

    public SaveJoshyAchievements(){
        super("joshyachievements");
    }

    public String getValue(final Player player){
        return player.getJoshyAchievementTracker().toSaveString();
    }

    public void setValue(final Player player, final String value){
        if(!value.trim().isEmpty())
            player.getJoshyAchievementTracker().loadFromSaveString(value.trim());
    }
}
