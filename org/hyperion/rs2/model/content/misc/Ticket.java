package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.util.TextUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Ticket {
    public static final HashMap<Player, TicketBuilder> tickets = new HashMap<Player, TicketBuilder>();

    private static final List<String> helpers = new LinkedList<String>();

    public static synchronized void removeRequest(final Player p) {
        tickets.remove(p);
    }

    public static synchronized void putRequest(final Player p, final String reason) {
        tickets.put(p, new TicketBuilder(reason, System.currentTimeMillis()));
    }

    public static synchronized TicketBuilder getRequest(final Player p) {
        return tickets.get(p);
    }

    public static synchronized void clearOffline() {
        for(final Player p : tickets.keySet()){
            if(p == null || !p.isActive()){
                removeRequest(p);
            }
            final long deltaMS = System.currentTimeMillis() - getRequest(p).startTime();
            if((getRequest(p).isAnswered() && deltaMS > 180000) || (deltaMS > 1200000)){
                removeRequest(p);
            }
        }
    }

    public static void checkTickets(final Player checker) {
        clearOffline();
        for(final Player player : tickets.keySet()){
            if(player != null){
                helpers.add(checker.getName());
                checker.getActionSender().sendMessage(TextUtils.titleCase(player.getName()) + "| @blu@" + tickets.get(player).getReason());
            }
        }
    }

    public static boolean hasTicket(final Player player) {
        if(getRequest(player) != null && !getRequest(player).isAnswered())
            return true;
        return false;
    }
}
