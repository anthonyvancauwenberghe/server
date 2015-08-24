package org.hyperion.rs2.packet;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.saving.PlayerSaving;

public class PickupItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		switch(packet.getOpcode()) {
			case 236:
	        /*
			 * Option 1.
			 */
				option1(player, packet);
				break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 *
	 * @param player
	 * @param packet
	 */
	private void option1(final Player player, Packet packet) {
		final int itemY = packet.getLEShort();
		final int itemID = packet.getShort();
		final int itemX = packet.getLEShort();
		final Location loc = Location.create(itemX, itemY, 0);
		World.getWorld().submit(new Event(600, "checked") {
			int timeout = 0;

			@Override
			public void execute() {
                if(loc.distance(player.getLocation()) == 1 && timeout > 0) {

                    World.getWorld().getGlobalItemManager().pickupItem(player, itemID, itemX, itemY);
                    player.playAnimation(Animation.create(7270));
                    player.getWalkingQueue().finish();
                    player.getWalkingQueue().reset();
                    this.stop();
                } else if(loc.distance(player.getLocation()) == 0) {
					//player.getLogging().log("Picked up item : " + itemID);
                    World.getWorld().getGlobalItemManager().pickupItem(player, itemID, itemX, itemY);
                    this.stop();
                } else if(++ timeout >= 10) {
					this.stop();
				}
			}

		});
	}


}

