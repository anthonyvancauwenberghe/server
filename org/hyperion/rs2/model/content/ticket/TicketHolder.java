package org.hyperion.rs2.model.content.ticket;

import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/9/15
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class TicketHolder {

    private Ticket ticket;
    private long lastTicket = 0L;

    public final Optional<Ticket> get() {
        return Optional.ofNullable(ticket);
    }

    public final void create(final String name, final String title, final String reason, final Rank min_rank) {
        if(ticket != null)
            World.getWorld().getTicketManager().remove(ticket);
        this.ticket = new Ticket(name, reason, title, min_rank);
        lastTicket = System.currentTimeMillis();
        System.out.println("HEREZ2");
        World.getWorld().getTicketManager().add(this.ticket);
    }

    public final boolean canMakeTicket() {
        if(System.currentTimeMillis() - lastTicket < 60000)
            return false;
        return true;
    }

    public final void fireOnLogout() {
        if(ticket != null)
            World.getWorld().getTicketManager().remove(ticket);
    }


}
