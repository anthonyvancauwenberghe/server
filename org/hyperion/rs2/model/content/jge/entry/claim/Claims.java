package org.hyperion.rs2.model.content.jge.entry.claim;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.entry.Entry;

/**
 * Created by Administrator on 9/24/2015.
 */
public class Claims {

    public final Entry entry;

    public final ClaimSlot progressSlot;
    public final ClaimSlot returnSlot;

    public Claims(final Entry entry){
        this.entry = entry;

        progressSlot = ClaimSlot.createDefault();
        returnSlot = ClaimSlot.createDefault();
    }

    public boolean empty(){
        return progressSlot.empty() && returnSlot.empty();
    }

    public void addProgress(final int itemId, final int itemQuantity){
        add(progressSlot, itemId, itemQuantity);
    }

    public boolean claimProgress(){
        return claim(entry.player(), progressSlot);
    }

    public void addReturn(final int itemId, final int itemQuantity){
        add(returnSlot, itemId, itemQuantity);
    }

    public boolean claimReturn(){
        return claim(entry.player(), returnSlot);
    }

    private static void add(final ClaimSlot slot, final int itemId, final int itemQuantity){
        if(slot.valid() && slot.holding(itemId))
            slot.add(itemQuantity);
        else
            slot.set(itemId, itemQuantity);
    }

    private static boolean claim(final Player player, final ClaimSlot slot){
        if(!slot.valid()){
            player.sendf("Nothing to claim!");
            return true;
        }
        final Item item = slot.item();
        if(player.getInventory().hasRoomFor(item)){
            final String name = String.format("%s x %,d", item.getDefinition().getName(), item.getCount());
            if(!player.getInventory().add(item)){
                player.sendf("There was an error giving %s", name);
                return false;
            }
            player.sendf("%s has been added to your inventory", name);
            slot.reset();
            return true;
        }else{
            final int free = player.getInventory().freeSlots();
            if(free < 1){
                player.sendf("Please allow at least one free slot!");
                return false;
            }
            final int quantity = free > slot.itemQuantity() ? slot.itemQuantity() : free;
            final String name = String.format("%s x %,d", item.getDefinition().getName(), quantity);
            if(!player.getInventory().add(Item.create(slot.itemId(), quantity))){
                player.sendf("There was an error giving %s", name);
                return false;
            }
            player.sendf("%s has been added to your inventory", name);
            slot.add(-quantity);
            if(slot.itemQuantity() == 0)
                slot.reset();
            return true;
        }
    }
}
