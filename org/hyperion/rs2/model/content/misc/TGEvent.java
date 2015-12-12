package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;

/**
 * Created by FUZENSETH on 16.11.2014.
 *
 * @information Thanks giving event.
 */
public class TGEvent {

    public static final int LOBBY_X = 2461;
    public static final int LOBBY_Y = 5285;


    public static final void teleport(final Player p) {
        ContentEntity.teleport(p, LOBBY_X, LOBBY_Y, 0);
        ContentEntity.sendMessage(p, "Speak to Grandpa Jack to receive some instructions.");
    }

}
