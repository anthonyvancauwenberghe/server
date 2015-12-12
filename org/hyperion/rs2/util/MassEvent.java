package org.hyperion.rs2.util;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public enum MassEvent {
    INSTANCE;

    MassEvent() {
    }

    public static final MassEvent getSingleton() {
        return INSTANCE;
    }

    public final void executeEvent(final EventBuilder e) {
        World.getWorld().submit(new Event(e.getDelay()) {
            public void execute() {
                if(e.checkStop())
                    this.stop();
                for(final Player p : World.getWorld().getPlayers())
                    e.execute(p);
                if(e.getDelay() == 0)
                    EventBuilder.stopEvent(e);
            }
        });
    }
}


