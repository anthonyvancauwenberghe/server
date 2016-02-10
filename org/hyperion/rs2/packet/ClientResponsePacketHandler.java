package org.hyperion.rs2.packet;

import org.hyperion.rs2.event.impl.ClientConfirmEvent;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * Created by Gilles on 10/02/2016.
 */
public class ClientResponsePacketHandler implements PacketHandler {
    @Override
    public void handle(Player player, Packet packet) {
        ClientConfirmEvent.addResponse(player.getName(), packet.getShort());
    }
}
