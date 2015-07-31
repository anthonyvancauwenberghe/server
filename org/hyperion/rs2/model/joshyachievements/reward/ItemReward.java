package org.hyperion.rs2.model.joshyachievements.reward;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

public class ItemReward implements Reward{

    private final int itemId;
    private final int quantity;
    private final boolean preferInventory;

    public ItemReward(final int itemId, final int quantity, final boolean preferInventory){
        this.itemId = itemId;
        this.quantity = quantity;
        this.preferInventory = preferInventory;
    }

    public int getItemId(){
        return itemId;
    }

    public int getQuantity(){
        return quantity;
    }

    public boolean isPreferInventory(){
        return preferInventory;
    }

    public Item getItem(){
        return Item.create(itemId, quantity);
    }

    public void apply(final Player player){
        final Item item = getItem();
        if(preferInventory && player.getInventory().hasRoomFor(item)){
            player.getInventory().add(item);
            player.sendf("%s (x%,d) has been added to your inventory!", item.getDefinition().getName(), item.getCount());
        }else{
            player.getBank().add(item);
            player.sendf("%s (x%,d) has been added to your bank!", item.getDefinition().getName(), item.getCount());
        }
    }

    public String toString(){
        return String.format("ItemReward(itemId=%d,quantity=%,d)", itemId, quantity);
    }
}
