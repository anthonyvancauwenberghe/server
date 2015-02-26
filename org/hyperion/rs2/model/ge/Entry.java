package org.hyperion.rs2.model.ge;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.PacketBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jet on 2/25/2015.
 */
public class Entry {

    private final int id;
    private final int itemId;
    private final int itemQuantity;
    private final int minBidPrice;
    private final int buyItNowPrice;
    private final String owner;

    private final Map<Integer, Bid> bids;

    public Entry(final int id, final int itemId, final int itemQuantity, final int minBidPrice, final int buyItNowPrice, final String owner){
        this.id = id;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.minBidPrice = minBidPrice;
        this.buyItNowPrice = buyItNowPrice;
        this.owner = owner;

        bids = new HashMap<>();
    }

    public int getId(){
        return id;
    }

    public int getItemId(){
        return itemId;
    }

    public int getItemQuantity(){
        return itemQuantity;
    }

    public int getMinBidPrice(){
        return minBidPrice;
    }

    public int getBuyItNowPrice(){
        return buyItNowPrice;
    }

    public String getOwnerName(){
        return owner;
    }

    public Player getOwner(){
        return World.getWorld().getPlayer(owner);
    }

    public int getNextBidId(){
        for(int i = 0; i < Short.MAX_VALUE; i++)
            if(!bids.containsKey(i))
                return i;
        return -1; //will definitely never happen (lol)
    }

    public void addBid(final Bid bid){
        bid.entry = this;
        bids.put(bid.getId(), bid);
    }

    public Bid getBid(final int bidId){
        return bids.get(bidId);
    }

    public void removeBid(final Bid bid){
        bid.entry = null;
        bids.remove(bid.getId());
    }

    public Stream<Bid> bids(){
        return bids.values().stream();
    }

    public List<Bid> getBids(final Predicate<Bid> filter){
        return bids().filter(
                filter != null ? filter : Objects::nonNull
        ).collect(Collectors.toList());
    }

    public void append(final PacketBuilder bldr){
        bldr.putShort(id)
                .putShort(itemId)
                .putInt(itemQuantity)
                .putInt(minBidPrice)
                .putInt(buyItNowPrice)
                .putRS2String(owner);
    }

    public Element toElement(final Document doc){
        final Element entry = doc.createElement("entry");
        entry.setAttribute("id", Integer.toString(id));
        entry.setAttribute("owner", owner);
        final Element item = doc.createElement("item");
        item.setAttribute("id", Integer.toString(itemId));
        item.setAttribute("quantity", Integer.toString(itemQuantity));
        final Element minBidPrice = doc.createElement("minBidPrice");
        minBidPrice.setTextContent(Integer.toString(this.minBidPrice));
        final Element buyItNowPrice = doc.createElement("buyItNowPrice");
        buyItNowPrice.setTextContent(Integer.toString(this.buyItNowPrice));
        final Element bids = doc.createElement("bids");
        this.bids.values().stream().map(
                b -> b.toElement(doc)
        ).forEach(bids::appendChild);
        entry.appendChild(item);
        entry.appendChild(minBidPrice);
        entry.appendChild(buyItNowPrice);
        entry.appendChild(bids);
        return entry;
    }

    public static Entry parse(final Element e){
        final int id = Integer.parseInt(e.getAttribute("id"));
        final String owner = e.getAttribute("owner");
        final Element item = (Element) e.getElementsByTagName("item").item(0);
        final int itemId = Integer.parseInt(item.getAttribute("id"));
        final int itemQuantity = Integer.parseInt(item.getAttribute("quantity"));
        final int minBidPrice = Integer.parseInt(e.getElementsByTagName("minBidPrice").item(0).getTextContent());
        final int buyItNowPrice = Integer.parseInt(e.getElementsByTagName("buyItNowPrice").item(0).getTextContent());
        final Entry entry = new Entry(id, itemId, itemQuantity, minBidPrice, buyItNowPrice, owner);
        final Element bids = (Element) e.getElementsByTagName("bids").item(0);
        final NodeList list = bids.getElementsByTagName("bid");
        for(int i = 0; i < list.getLength(); i++){
            final Node n = list.item(i);
            if(n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            final Element b = (Element) n;
            final Bid bid = Bid.parse(b);
            entry.addBid(bid);
        }
        return entry;
    }
}
