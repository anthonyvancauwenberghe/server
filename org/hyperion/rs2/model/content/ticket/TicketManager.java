package org.hyperion.rs2.model.content.ticket;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.PacketBuilder;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/9/15
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketManager {

    private final Set<Ticket> tickets = new TreeSet<>((id_1, id_2) -> Integer.valueOf(id_1.id).compareTo(Integer.valueOf(id_2.id)));

    public synchronized void add(final Ticket ticket) {
        tickets.add(ticket);
    }

    public synchronized void remove(final Ticket ticket) {
        tickets.remove(ticket);
    }

    public synchronized void display(final Player player) {

    }

}
