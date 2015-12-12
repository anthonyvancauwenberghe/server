package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveName extends SaveString {

    public SaveName(final String name) {
        super(name);
    }

    //stopped because it doesn't need to LOAD the name again, there it's called the player already has his name lmao,
    //it's like saving his IOSession, stupid af
    @Override
    public void setValue(final Player player, final String value) {
        //value = TextUtils.ucFirst(value);
       /* if(!player.getName().equalsIgnoreCase(value))
            player.display = value;*/
    }

    @Override
    public String getValue(final Player player) {
        return player.getDisplay();
    }

}
