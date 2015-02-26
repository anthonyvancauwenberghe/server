package org.hyperion.rs2.model.ge;

import org.hyperion.rs2.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jet on 2/26/2015.
 */
public class GrandExchangeTracker {

    private final Player player;

    private int viewingItemId;
    private int viewingEntryId;
    private boolean isOpen;

    public GrandExchangeTracker(final Player player){
        this.player = player;

        viewingItemId = viewingEntryId = -1;
    }

    public Player getPlayer(){
        return player;
    }

    public List<Entry> getEntries(){
        return GrandExchange.getEntries(
                e -> e.getOwnerName().equalsIgnoreCase(player.getName())
        );
    }

    public List<Bid> getBids(){
        final List<Bid> bids = new ArrayList<>();
        GrandExchange.entries().map(
                e -> e.getBids(
                        b -> b.getOwnerName().equalsIgnoreCase(player.getName())
                )
        ).forEach(bids::addAll);
        return bids;
    }

    public int getViewingItemId(){
        return viewingItemId;
    }

    public void setViewingItemId(final int viewingItemId){
        this.viewingItemId = viewingItemId;
        viewingEntryId = -1;
    }

    public Entry getViewingEntry(){
        return GrandExchange.getEntry(viewingEntryId);
    }

    public void setViewingEntry(final Entry entry){
        viewingEntryId = entry != null ? entry.getId() : -1;
    }

    public boolean isOpen(){//shit code
        return isOpen;
    }

    public void open(){
        isOpen = true;
        GrandExchangeInterface.get().show(player);
    }

    public void close(){
        isOpen = false;
        GrandExchangeInterface.get().hide(player);
    }

}
