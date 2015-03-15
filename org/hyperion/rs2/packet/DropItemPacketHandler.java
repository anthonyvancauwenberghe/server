package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.net.Packet;

public class DropItemPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int itemId = packet.getShortA();
	    /*junk?*/
		packet.get();
		packet.get();
		int itemSlot = packet.getShortA();
		if(itemId < 0 || itemId > ItemDefinition.MAX_ID || itemSlot < 0 || itemSlot > 27)
			return;
		if((player.isDead() || System.currentTimeMillis() - player.cE.lastHit < 10000) && !ItemSpawning.canSpawn(itemId)) {
			player.getActionSender().sendMessage("You can't drop items, while in combat.");
			return;
		}
		Item toRemove = player.getInventory().get(itemSlot);
		if(toRemove == null)
			return;
        if(itemId == 15707) {
            player.sendMessage("Perks: ");
            player.sendMessage(player.getDungoneering().perks.boosts());
            return;
        }
		if(! player.getDropping().canDrop(toRemove.getId())) {
			player.getActionSender().sendMessage("Please confirm you want to drop this item by dropping it again.");
			return;
		}
		if(Rank.hasAbility(player, Rank.MODERATOR))
			World.getWorld().getGlobalItemManager().dropItem(player, itemId, itemSlot);
		player.getInventory().remove(toRemove);
		player.getActionSender().sendMessage("Your item dissapears.");
		player.getDropping().reset();


	}


}

