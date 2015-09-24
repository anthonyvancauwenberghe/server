package org.hyperion.rs2.model.content.jge.entry;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;

/**
 * Created by Administrator on 9/24/2015.
 */
public class EntryBuilder {

    private final String playerName;
    private final Entry.Type type;
    private final int slot;
    private int itemId;
    private int itemQuantity;
    private int unitPrice;

    public EntryBuilder(final String playerName, final Entry.Type type, final int slot){
        this.playerName = playerName;
        this.type = type;
        this.slot = slot;
        itemId = -1;
    }

    public Entry.Type type(){
        return type;
    }

    public int slot(){
        return slot;
    }

    public int itemId(){
        return itemId;
    }

    public boolean validItem(){
        return itemId > -1 && itemQuantity > 0;
    }

    public Item item(){
        return validItem() ? Item.create(itemId, itemQuantity) : null;
    }

    public boolean itemId(final int itemId){
        if(itemId() == itemId)
            return false;
        this.itemId = itemId < 0 || itemId > ItemDefinition.MAX_ID ? -1 : itemId;
        return true;
    }

    public int itemQuantity(){
        return itemQuantity;
    }

    public boolean itemQuantity(final int itemQuantity){
        if(itemQuantity < 1 || itemQuantity() == itemQuantity)
            return false;
        this.itemQuantity = itemQuantity;
        return true;
    }

    public boolean decreaseItemQuantity(){
        return itemQuantity(itemQuantity - 1);
    }

    public boolean increaseItemQuantity(){
        return itemQuantity(itemQuantity + 1);
    }

    public int unitPrice(){
        return unitPrice;
    }

    public boolean unitPrice(final int unitPrice){
        if(unitPrice < 1 || unitPrice() == unitPrice)
            return false;
        this.unitPrice = unitPrice;
        return true;
    }

    public boolean decreaseUnitPrice(){
        return unitPrice(unitPrice - 1);
    }

    public boolean increaseUnitPrice(){
        return unitPrice(unitPrice + 1);
    }

    public boolean decreaseUnitPricePercent(){
        return unitPrice((int)(unitPrice - (unitPrice * 0.05)));
    }

    public boolean increaseUnitPricePercent(){
        return unitPrice((int)(unitPrice + (unitPrice * 0.05)));
    }

    public int totalPrice(){
        return unitPrice * itemQuantity;
    }

    public boolean canBuild(){
        return validItem()
                && itemQuantity > 0
                && unitPrice > 0;
    }

    public Entry build(){
        return new Entry(playerName, type, slot, itemId, itemQuantity, unitPrice);
    }
}
