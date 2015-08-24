package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.minigame.FightPits;

public class CountDownEvent extends Event {

    final Runnable run;
    final String command;
    final String name;
    final Location location;
	
	public CountDownEvent(ServerMinigame.CountDownEventBuilder builder) {
		super(1000);
        this.name = builder.name;
        this.command = builder.command;
        this.location = builder.location;
        this.run = builder.run;
	}
	
	private int counter = 200; //5minutes
	public void execute() {
		if(counter == 200) {
			Events.fireNewEvent(name, true, 0, location);
		}
		if(--counter == 0) {
			run.run();
            Events.resetEvent();
            this.stop();
		}

		for(NPC npc : World.getWorld().getNPCs()) {
			if(npc != null)
				npc.forceMessage(name+" event in "+counter+" seconds! Go to "+command + " (5x PKP)");
		}
	}

}
