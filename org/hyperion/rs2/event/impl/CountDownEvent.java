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
		if(--counter == 0) {
			FightPits.startEvent();
			for(Player player : World.getWorld().getPlayers()) {
				player.getQuestTab().updateQuestTab();
			}
			this.stop();
		}

		Events.fireNewEvent("Fight pits", true, counter, Location.create(2399, 5178, 0));

		for(NPC npc : World.getWorld().getNPCs()) {
			if(npc != null)
				npc.forceMessage("Fight pits event in "+counter+" seconds! Go to ::fightpits");
		}
	}

}
