package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.DoorManager;
import org.hyperion.rs2.model.content.skill.Farming;
import org.hyperion.rs2.net.Packet;

// Referenced classes of package org.hyperion.rs2.packet:
//            PacketHandler

public class ReloadRegion implements PacketHandler {

    @Override
    public void handle(final Player player, final Packet packet) {
        DoorManager.refresh(player);
        World.getWorld().getGlobalItemManager().displayItems(player);
        Farming.farming.refreshFarmObjects(player);
        World.getWorld().getObjectMap().load(player);
        player.getWalkingQueue().reset();
    }
}
