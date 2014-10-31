package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPCFacing;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.TextUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @author SaosinHax/Linus/Vegas/Flux/Tinderbox/Jack Daniels/Arsen/Jolt <- All same person
 */

public class BankersFacing extends Event {

	/**
	 * The delay in milliseconds between consecutive facing.
	 */
	public static final int CYCLETIME = 3000;

	/**
	 * Creates the Bankers facing event each second.
	 */
	public BankersFacing() {
		super(CYCLETIME);
	}

	@Override
	public void execute() {
		NPCFacing.faceBankers();
		HashMap<String, Object> map = new HashMap<String, Object>();
		//THIS might be causing the xlog dupe - rushes to unregister 2 if 2 of same acc are logged in
		for(Player player : World.getWorld().getPlayers()) {
			String name = player.getName().replaceAll(" ", "_").toLowerCase();
			if(map.containsKey(name)) {
				System.out.println("DUPER WITH USERNAME: " + name);
				TextUtils.writeToFile("./data/multi_loggers.log", new Date().toString() + " : " + name);
				//World.getWorld().unregister2(player);
				//player.getSession().close(true);
			} else {
				map.put(name, new Object());
			}
		}
	}

}
