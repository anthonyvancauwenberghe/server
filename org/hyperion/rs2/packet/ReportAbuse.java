package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.NameUtils;

public class ReportAbuse implements PacketHandler {

    @Override
    public void handle(final Player player, final Packet packet) {
        final long l = packet.getLong();
        final boolean mute = packet.get() == 1 ? true : false;
        try{
            final String name = NameUtils.longToName(l);
            final int rule = packet.get();
            //World.getWorld().getAbuseHandler().reportAbuse(player,name,rule);
        }catch(final ArrayIndexOutOfBoundsException e2){
            //errors todo with input will be handled here
        }catch(final Exception e){
            e.printStackTrace();//shouldnt need to be called
        }
    }
}
