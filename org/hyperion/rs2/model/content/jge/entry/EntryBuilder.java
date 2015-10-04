package org.hyperion.rs2.model.content.jge.entry;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Date;

/**
 * Created by Administrator on 9/24/2015.
 */
public class EntryBuilder {

    private final Player player;
    private final Entry.Type type;
    private final int slot;
    private final Entry.Currency currency;
    private int itemId;
    private int itemQuantity;
    private int unitPrice;

    public EntryBuilder(final Player player, final Entry.Type type, final int slot, final Entry.Currency currency){
        this.player = player;
        this.type = type;
        this.slot = slot;
        this.currency = currency;

        itemId = -1;
    }

    public Entry.Currency currency(){
        return currency;
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

    public boolean decreaseItemQuantity(final int amount){
        return itemQuantity(itemQuantity - amount);
    }

    public boolean increaseItemQuantity(final int amount){
        return itemQuantity(itemQuantity + amount);
    }

    public boolean increaseItemQuantity(){
        return increaseItemQuantity(1);
    }

    public boolean decreaseItemQuantity(){
        return decreaseItemQuantity(1);
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
        if(this.itemId != -1){
            final ItemDefinition def = ItemDefinition.forId(this.itemId);
            if(def != null && def.isNoted() && def.getNormalId() != -1)
                this.itemId = def.getNormalId();
        }
        return new Entry(OffsetDateTime.now(), player.getName(), type, slot, itemId, itemQuantity, unitPrice, currency);
    }
}
