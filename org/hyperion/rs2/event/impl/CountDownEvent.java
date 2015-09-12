package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.util.Time;

import java.io.IOException;

public class CountDownEvent extends Event {

    final Runnable run;
    final String command;
    final String name;
    final Location location;
    final String message;
    final boolean safe;
	
	public CountDownEvent(ServerMinigame.CountDownEventBuilder builder) {
		super(1000);
        this.name = builder.name;
        this.command = builder.command;
        this.location = builder.location;
        this.run = builder.run;
        this.message = builder.message;
        this.safe = builder.safe;
	}
	
	private int counter = 120; //2 minutes
	public void execute() {
		if(counter == 120) {
			Events.fireNewEvent(name, safe, counter, location);
            World.getWorld().getPlayers().stream().forEach(p -> p.sendServerMessage(name + " event is starting in 2 minutes!"));
		}
		if(--counter == 0) {
			run.run();
            World.getWorld().submit(new ResetEvent());
            this.stop();
		}
        if(counter%10 == 0) {
            for(NPC npc : World.getWorld().getNPCs()) {
                if(npc != null)
                    npc.forceMessage(name + " event in " + counter + " seconds! Go to " + command + " for " + message + "!");

            }
        }


	}

    private static final class ResetEvent extends Event {
        public ResetEvent() {
            super(Time.THIRTY_MINUTES);
        }

        @Override
        public void execute() throws IOException {
            Events.resetEvent();
            this.stop();
        }

    }

}
