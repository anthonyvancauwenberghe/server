package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;

/**
 * @author Arsen Maxyutov.
 */
public class OverloadDrinkingEvent extends Event {

    public static final int HIT_TIMES = 5;

    public static final int DAMAGE = 10;

    private final Player player;

    private int counter = HIT_TIMES;

    public OverloadDrinkingEvent(final Player player) {
        super(1100, "ovl");
        this.player = player;
    }

    @Override
    public void execute() {
        ContentEntity.startAnimation(player, 3170);
        player.cE.hit(DAMAGE, null, false, Constants.EMPTY);
        counter--;
        if(counter <= 0){
            this.stop();
        }
    }

}
