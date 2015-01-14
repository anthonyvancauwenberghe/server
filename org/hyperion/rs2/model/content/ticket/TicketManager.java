package org.hyperion.rs2.model.content.ticket;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.packet.InterfacePacketHandler;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/9/15
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketManager {

    private final Set<Ticket> tickets = new TreeSet<>((Ticket id_1, Ticket id_2) -> Integer.valueOf(id_1.id).compareTo(Integer.valueOf(id_2.id)));

    public synchronized void add(final Ticket ticket) {
        tickets.add(ticket);
        for(final Player player : World.getWorld().getPlayers()) {
            if (Rank.getPrimaryRank(player).ordinal() >= ticket.min_rank.ordinal()) {
                player.sendMessage("@red@New ticket submitted--------------",
                        "Name: @blu@" + ticket.name, "Title: @blu@" + ticket.title, "Reason: @blu@" + ticket.request, "Rank: @blu@" + ticket.min_rank);
            }
        }

        refreshSizeForStaff();

    }

    private int getSizeForPlayer(final Player player) {
        if(!Rank.isStaffMember(player))
            return -1;
        int size = 0;
        for(final Ticket ticket : tickets) {
            if(Rank.getPrimaryRank(player).ordinal() >= ticket.min_rank.ordinal())
                size++;
        }
        return size;
    }

    private void refreshSizeForStaff() {

        for(final Player player : World.getWorld().getPlayers()) {
            final int size = getSizeForPlayer(player);
            if (size != -1) {
                final PacketBuilder builder = new PacketBuilder(InterfacePacketHandler.DATA_OPCODE, Packet.Type.VARIABLE).putShort(5);
                builder.putTriByte(size);
                player.write(builder.toPacket());
            }
        }

    }

    public synchronized void remove(final Ticket ticket) {
        tickets.remove(ticket);
        refreshSizeForStaff();
    }

    public void assist(final Player player, final int id) {
        if(!Rank.isStaffMember(player))
            return;
        if(!ItemSpawning.canSpawn(player))
            return;
        Ticket tick = null;
        for(final Ticket ticket : tickets)
            if(ticket.id == id)
                tick = ticket;
        if(tick != null) {
            final Player p = World.getWorld().getPlayer(tick.name);
            if(p != null) {
                if(!p.getLocation().inDuel()) {
                    Magic.teleport(p, player.getLocation(), true);
                    remove(tick);
                }
                        else {
                        player.sendMessage("You can't be assisted while you are in a duel or wilderness");
                    }

            } else {
                player.sendMessage("Player is offline");
                remove(tick);
            }
        } else {
            player.sendMessage("Ticket doesn't exist");
        }
    }

    public void display(final Player player) {
        final PacketBuilder builder = new PacketBuilder(InterfacePacketHandler.DATA_OPCODE, Packet.Type.VARIABLE).putShort(3);
        builder.putShort((short) getSizeForPlayer(player));
        for(final Ticket ticket : tickets) {
            if(Rank.getPrimaryRank(player).ordinal() >= ticket.min_rank.ordinal()) {
                builder.putRS2String(ticket.title);
                builder.putRS2String(ticket.name);
                builder.putRS2String(ticket.request);
                builder.putTriByte(ticket.id);
                builder.put((byte)revert(ticket.min_rank));
            }
        }

        player.write(builder.toPacket());


    }

    public static Rank convert(final int read) {

        switch(read) {
            case 0:
                return Rank.HELPER;
            case 1:
                return Rank.MODERATOR;
            case 2:
                return Rank.ADMINISTRATOR;
        }

        return Rank.ADMINISTRATOR;

    }

    public static int revert(final Rank min_rank) {
        switch(min_rank) {
            case HELPER:
                return 0;
            case MODERATOR:
                return 1;
            case ADMINISTRATOR:
                return 2;
        }
        return 0;
    }

}
