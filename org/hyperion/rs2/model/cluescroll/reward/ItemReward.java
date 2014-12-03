package org.hyperion.rs2.model.cluescroll.reward;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.cluescroll.util.ClueScrollUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemReward extends Reward{

    private int id;

    public ItemReward(final int id, final int minAmount, final int maxAmount, final int chance){
        super(Type.ITEM, minAmount, maxAmount, chance);
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setId(final int id){
        this.id = id;
    }

    protected boolean give(final Player player, final int amount){
        final Item item = Item.create(id, amount);
        if(player.getInventory().hasRoomFor(item)){
            player.getInventory().add(item);
            player.sendf("@red@%s x%,d@blu@ has been added to your inventory", item.getDefinition().getName(), amount);
        }else{
            player.getBank().add(item);
            player.sendf("@red@%s x%,d@blu@ has been added to your inventory", item.getDefinition().getName(), amount);
        }
        return true;
    }

    protected void append(final Document doc, final Element root){
        root.appendChild(ClueScrollUtils.createElement(doc, "id", id));
    }

    public String toString(){
        return String.format("%s: %d", super.toString(), id);
    }

    public static ItemReward parse(final Element element){
        final int itemId = ClueScrollUtils.getInteger(element, "id");
        final int minAmount = ClueScrollUtils.getInteger(element, "maxAmount");
        final int maxAmount = ClueScrollUtils.getInteger(element, "minAmount");
        final int chance = ClueScrollUtils.getInteger(element, "chance");
        return new ItemReward(itemId, minAmount, maxAmount, chance);
    }
}
