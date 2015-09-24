package org.hyperion.rs2.model.content.jge.entry;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.jge.entry.claim.Claims;
import org.hyperion.rs2.model.content.jge.entry.progress.ProgressManager;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Administrator on 9/23/2015.
 */
public class Entry {

    public enum Type{

        BUYING("Buy Offer"),
        SELLING("Sell Offer");

        public final String name;

        Type(final String name){
            this.name = name;
        }

        public Type opposite(){
            return this == BUYING ? SELLING : BUYING;
        }
    }

    public final String playerName;
    public final Type type;
    public final int slot;
    public final int itemId;
    public final int itemQuantity;
    public final int unitPrice;
    public final int totalPrice;

    public final ProgressManager progress;
    public final Claims claims;

    public Entry(final String playerName, final Type type, final int slot, final int itemId, final int itemQuantity, final int unitPrice){
        this.playerName = playerName;
        this.type = type;
        this.slot = slot;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.unitPrice = unitPrice;

        totalPrice = unitPrice * itemQuantity;

        progress = new ProgressManager(this);
        claims = new Claims(this);
    }

    public boolean finished(){
        return progress.completed()
                && claims.empty();
    }

    public Item item(){
        return Item.create(itemId, itemQuantity);
    }

    public Optional<Player> playerOpt(){
        return Optional.ofNullable(World.getWorld().getPlayer(playerName));
    }

    public void player(final Consumer<Player> action){
        playerOpt().ifPresent(action);
    }

    public Player player(){
        return playerOpt().orElse(null);
    }

    public static EntryBuilder build(final String playerName, final Type type, final int slot){
        return new EntryBuilder(playerName, type, slot);
    }
}
