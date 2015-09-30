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
        final String name = String.format("%s x %,d", item.getDefinition().getName(), item.getCount());
        if(player.getInventory().hasRoomFor(item)){
            if(!player.getInventory().add(item)){
                player.sendf("There was an error giving %s", name);
                return false;
            }
            player.sendf("%s has been added to your inventory", name);
            slot.reset();
            return true;
        }else{
            player.sendf("Please make enough room for %s", name);
            return false;
        }
    }
}
