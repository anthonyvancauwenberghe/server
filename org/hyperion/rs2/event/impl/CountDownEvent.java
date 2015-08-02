package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
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
				player.getActionSender().sendString(ServerMinigame.name == null ? "Event Dormant" : ServerMinigame.name, 7332);
			}
			this.stop();
		}
		for(Player player : World.getWorld().getPlayers()) {
            try {
			    player.getActionSender().sendString("@gre@Event in @red@"+counter+" @gre@secs!", 7332);
            }catch(Exception e) {
                this.stop();
            }
		}
		for(NPC npc : World.getWorld().getNPCs()) {
			if(npc != null)
				npc.forceMessage("Fight pits event in "+counter+" seconds! Go to ::fightpits");
		}
	}

}
