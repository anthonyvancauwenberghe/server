package org.hyperion.rs2.packet;

import org.hyperion.engine.task.impl.ClientConfirmTask;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * Created by Gilles on 10/02/2016.
 */
public class ClientResponsePacketHandler implements PacketHandler {
    @Override
    public void handle(Player player, Packet packet) {
        ClientConfirmTask.addResponse(player.getName(), packet.getShort());
    }
}
