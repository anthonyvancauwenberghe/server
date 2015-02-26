package org.hyperion.rs2.model.ge;

import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.PacketBuilder;

/**
 * Created by Jet on 2/26/2015.
 */
public class GrandExchangeInterface extends Interface {

    private static final GrandExchangeInterface INSTANCE = new GrandExchangeInterface();

    public static final int ID = 13;

    private static final int ADD_ENTRY = 1;
    private static final int REMOVE_ENTRY = 2;

    private static final int ADD_BID = 3;
    private static final int REMOVE_BID = 4;

    private static final int ACCEPT_BID = 5;
    private static final int BID_STATE = 6;

    public GrandExchangeInterface(){
        super(ID);
    }

    public void handle(final Player player, final Packet pkt){
        switch(pkt.get()){
            case ADD_ENTRY:
                handleAddEntry(player, pkt);
                break;
            case REMOVE_ENTRY:

                break;
            case ADD_BID:

                break;
            case REMOVE_BID:

                break;
            case ACCEPT_BID:

                break;
        }
    }

    private void handleAddEntry(final Player player, final Packet pkt){
        final int itemId = pkt.getShort();
        final int itemQuantity = pkt.getInt();
        final int minBidPrice = pkt.getInt();
        final int buyItNowPrice = pkt.getInt();
        final int realItemQuantity = player.getInventory().getCount(itemId);
        if(itemQuantity > player.getInventory().getCount(itemId)){
            player.sendf("You don't have that many " + ItemDefinition.forId(itemId).getName());
            return;
        }
        if(minBidPrice < 1){
            player.sendf("Mid bid price must be at least 1");
            return;
        }
        if(buyItNowPrice < 1){
            player.sendf("Buy it now price must be at least 1");
            return;
        }
        if(minBidPrice > buyItNowPrice){
            player.sendf("Buy it now price must be >= min bid price");
            return;
        }
        final Entry entry = new Entry(GrandExchange.getNextEntryId(), itemId, itemQuantity, minBidPrice, buyItNowPrice, player.getName());
        GrandExchange.addEntry(entry, true);
    }

    public Packet createAddEntry(final Entry entry){
        final PacketBuilder bldr = createDataBuilder();
        bldr.put((byte)ADD_ENTRY);
        entry.append(bldr);
        return bldr.toPacket();
    }

    public Packet createRemoveEntry(final Entry entry){
        final PacketBuilder bldr = createDataBuilder();
        bldr.put((byte)REMOVE_ENTRY);
        bldr.putShort(entry.getId());
        return bldr.toPacket();
    }

    public Packet createAddBid(final Bid bid){
        final PacketBuilder bldr = createDataBuilder();
        bldr.put((byte) ADD_BID);
        bldr.putShort(bid.getEntry().getId());
        bid.append(bldr);
        return bldr.toPacket();
    }

    public Packet createRemoveBid(final Bid bid){
        final PacketBuilder bldr = createDataBuilder();
        bldr.put((byte)REMOVE_BID);
        bldr.putShort(bid.getEntry().getId());
        bldr.putShort(bid.getId());
        return bldr.toPacket();
    }

    public Packet createBidState(final Bid bid){
        final PacketBuilder bldr = createDataBuilder();
        bldr.put((byte)BID_STATE);
        bldr.putShort(bid.getEntry().getId());
        bldr.putShort(bid.getId());
        bldr.put((byte)bid.getState().ordinal());
        return bldr.toPacket();
    }

    public static GrandExchangeInterface get(){
        return INSTANCE;
    }
}
