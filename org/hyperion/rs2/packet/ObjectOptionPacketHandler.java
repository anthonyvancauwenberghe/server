package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * Object option packet handler.
 *
 * @author Graham Edgecombe
 */
public class ObjectOptionPacketHandler implements PacketHandler {

    /**
     * Option 1 opcode.
     */
    private static final int OPTION_1 = 132, OPTION_2 = 252;

    @Override
    public void handle(final Player player, final Packet packet) {

        switch(packet.getOpcode()){
            case OPTION_1:
                handleOption1(player, packet);
                break;
            case OPTION_2:
                handleOption2(player, packet);
                break;
        }
    }

    /**
     * Handles the option 1 packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOption1(final Player player, final Packet packet) {
        final int x = packet.getLEShortA() & 0xFFFF;
        final int id = packet.getShort() & 0xFFFF;
        final int y = packet.getShortA() & 0xFFFF;

        //player.getLogging().log("Object clicked 1: id,x,y " + id + "," + x + "," + y);
        //System.out.println("id " + id);
        if(id != 2513){
            player.delayObjectClick[0] = id;
            player.delayObjectClick[1] = x;
            player.delayObjectClick[2] = y;
            player.delayObjectClick[3] = 1;
        }else{ //see? is that ok? cuz i didnt see any other way of doing this
            player.getWalkingQueue().reset();//ohyea this "target' thing u shoot at and stuff, yeah cool, wheres checkers shit
            ObjectClickHandler.clickObject(player, id, x, y, 1);
        }
    }

    /**
     * Handles the option 2 packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOption2(final Player player, final Packet packet) {
        final int id = packet.getLEShortA() & 0xFFFF;
        final int y = packet.getLEShort() & 0xFFFF;
        final int x = packet.getShortA() & 0xFFFF;

        //player.getLogging().log("Object clicked 2: id,x,y " + id + "," + x + "," + y);
        player.delayObjectClick[0] = id;
        player.delayObjectClick[1] = x;
        player.delayObjectClick[2] = y;
        player.delayObjectClick[3] = 2;
    }


}
