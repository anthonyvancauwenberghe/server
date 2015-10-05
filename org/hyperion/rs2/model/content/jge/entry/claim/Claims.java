package org.hyperion.rs2.model.content.jge.entry.claim;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

/**
 * Created by Administrator on 9/24/2015.
 */
public class Claims {

    public final Entry entry;

    public ClaimSlot progressSlot;
    public ClaimSlot returnSlot;

    public Claims(final Entry entry){
        this.entry = entry;

        progressSlot = ClaimSlot.createDefault();
        returnSlot = ClaimSlot.createDefault();
    }

    public Claims copy(){
        final Claims copy = new Claims(entry);
        copy.progressSlot = progressSlot.copy();
        copy.returnSlot = returnSlot.copy();
        return copy;
    }

    public boolean empty(){
        return progressSlot.empty() && returnSlot.empty();
    }

    public void addProgress(final int itemId, final int itemQuantity){
        add(entry, progressSlot, itemId, itemQuantity);
    }

    public boolean claimProgress(){
        return claim(entry.player(), entry, progressSlot);
    }

    public void addReturn(final int itemId, final int itemQuantity){
        add(entry, returnSlot, itemId, itemQuantity);
    }

    public boolean claimReturn(){
        return claim(entry.player(), entry, returnSlot);
    }

    public String toSaveString(){
        return String.format("%s,%s", progressSlot.toSaveString(), returnSlot.toSaveString());
    }

    public static Claims fromSaveString(final Entry entry, final String claim){
        final String[] split = claim.split(",");
        final Claims claims = new Claims(entry);
        claims.progressSlot = ClaimSlot.fromSaveString(split[0]);
        claims.returnSlot = ClaimSlot.fromSaveString(split[1]);
        return claims;
    }

    private static void add(final Entry entry, final ClaimSlot slot, final int itemId, final int itemQuantity){
        if(slot.valid() && slot.holding(itemId))
            slot.add(itemQuantity);
        else
            slot.set(itemId, itemQuantity);
    }

    private static boolean claim(final Player player, final Entry entry, final ClaimSlot slot){
        if(!slot.valid()){
            player.sendf("Nothing to claim!");
            return true;
        }
        final Item item = slot.item();
        if(player.getInventory().hasRoomFor(item)){
            slot.reset();
            if(!JGrandExchange.getInstance().updateClaims(entry)){
                player.sendf("Please try again later!");
                slot.set(item);
                return false;
            }
            final String name = (item.getCount() == 1 ? TextUtils.ucFirst(Misc.aOrAn(item.getDefinition().getName())) : item.getCount()) + " " + item.getDefinition().getName() + (item.getCount() > 1 ? "s" : "");
            //final String name = String.format("%s x %,d", item.getDefinition().getName(), item.getCount());
            player.sendf("%s has been added to your inventory", name);
            player.getInventory().add(item);
            return true;
        } else {
            final int free = player.getInventory().freeSlots();
            if(free < 1){
                player.sendf("You need to have at least 1 free inventory slot!");
                return false;
            }
            final int quantity = free > slot.itemQuantity() ? slot.itemQuantity() : free;
            slot.add(-quantity);
            if(slot.itemQuantity() == 0)
                slot.reset();
            if(!JGrandExchange.getInstance().updateClaims(entry)){
                player.sendf("Please try again later!");
                slot.set(item);
                return false;
            }
            player.getInventory().add(Item.create(slot.itemId(), quantity));
            final String name = (quantity == 1 ? Misc.aOrAn(item.getDefinition().getName()) : item.getCount()) + " " + item.getDefinition().getName() + (quantity > 1 ? "s" : "");
            player.sendf("%s has been added to your inventory", name);
            return true;
        }
    }
}
