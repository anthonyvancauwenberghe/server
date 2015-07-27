package org.hyperion.rs2.model.joshyachievements.reward;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

public class ItemReward implements Reward{

    private final int id;
    private final int amount;
    private final boolean preferInventory;

    public ItemReward(final int id, final int amount, final boolean preferInventory){
        this.id = id;
        this.amount = amount;
        this.preferInventory = preferInventory;
    }

    public int getId(){
        return id;
    }

    public int getAmount(){
        return amount;
    }

    public boolean isPreferInventory(){
        return preferInventory;
    }

    public Item getItem(){
        return Item.create(id, amount);
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
}
