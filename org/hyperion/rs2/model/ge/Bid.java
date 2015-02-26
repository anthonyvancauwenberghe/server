package org.hyperion.rs2.model.ge;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.PacketBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by Jet on 2/25/2015.
 */
public class Bid {

    public enum State{
        NONE,
        ACCEPTED,
        DECLINED
    }

    private final int id;
    private final int value;
    private final String owner;

    private State state;
    protected Entry entry;

    public Bid(final int id, final int value, final String owner, final State state){
        this.id = id;
        this.value = value;
        this.owner = owner;
    }

    public Bid(final int id, final int value, final String owner){
        this(id, value, owner, State.NONE);
    }

    public Entry getEntry(){
        return entry;
    }

    public int getId(){
        return id;
    }

    public int getValue(){
        return value;
    }

    public String getOwnerName(){
        return owner;
    }

    public Player getOwner(){
        return World.getWorld().getPlayer(owner);
    }

    public State getState(){
        return state;
    }

    public void setState(final State state){
        this.state = state;
    }

    public boolean is(final State state){
        return this.state == state;
    }

    public void append(final PacketBuilder bldr){
        bldr.putShort(id)
                .putInt(value)
                .putRS2String(owner)
                .put((byte)state.ordinal());
    }

    public Element toElement(final Document doc){
        final Element bid = doc.createElement("bid");
        bid.setAttribute("id", Integer.toString(id));
        bid.setAttribute("owner", owner);
        bid.setAttribute("state", state.name());
        final Element value = doc.createElement("value");
        value.setTextContent(Integer.toString(this.value));
        bid.appendChild(value);
        return bid;
    }

    public static Bid parse(final Element e){
        final int id = Integer.parseInt(e.getAttribute("id"));
        final String owner = e.getAttribute("owner");
        final State state = State.valueOf(e.getAttribute("state"));
        final int value = Integer.parseInt(e.getElementsByTagName("value").item(0).getTextContent());
        return new Bid(id, value, owner, state);
    }
}
