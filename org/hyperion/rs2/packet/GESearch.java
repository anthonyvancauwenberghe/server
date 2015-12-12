package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.grandexchange.GrandExchangeV2;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.NameUtils;

public class GESearch implements PacketHandler {

    @Override
    public void handle(final Player player, final Packet packet) {
        final Long message = packet.getLong();
        String itemName = "";
        try{
            itemName = NameUtils.longToName(message);
        }catch(final Exception e){
            return;
        }
        if(itemName.equals(player.lastSearch))
            return;
        player.lastSearch = itemName;
        GrandExchangeV2.searchGE(player, itemName);
    }

}
