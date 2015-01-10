package org.hyperion.rs2.model.content.ticket;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/9/15
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Ticket {

    public static int current_ticket = 0;

    public final int id;
    public final String name, request, title;

    public Ticket(final String name, final String request, final String title) {
        this.id = current_ticket++;
        this.name = name;
        this.request = request;
        this.title = title;
    }

}
