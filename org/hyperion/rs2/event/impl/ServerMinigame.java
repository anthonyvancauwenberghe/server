package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.util.Time;

public class ServerMinigame extends Event {

    public static int x, y, z;
    public static String name = null;
	
	public ServerMinigame() {
		super(Time.TEN_HOURS);
	}
	@Override
	public void execute() {
		World.getWorld().submit(new CountDownEvent());
	}

}
