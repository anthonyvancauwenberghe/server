package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.minigame.FightPits;

public class CountDownEvent extends Event {
	
	public CountDownEvent() {
		super(1000);
	}
	
	private int counter = 300; //5minutes
	public void execute() {
		if(counter == 300) {
			Events.fireNewEvent("Fight pits", true, 0, Location.create(2399, 5178, 0));
		}
		if(--counter == 0) {
			FightPits.startEvent();
			this.stop();
		}

		for(NPC npc : World.getWorld().getNPCs()) {
			if(npc != null)
				npc.forceMessage("Fight pits event in "+counter+" seconds! Go to ::fightpits");
		}
	}

}
