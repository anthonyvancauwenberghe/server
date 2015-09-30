package org.hyperion.rs2.model.content.jge.tracker;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.entry.EntryBuilder;
import org.hyperion.rs2.model.content.jge.entry.EntryManager;
import org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface.*;

/**
 * Created by Administrator on 9/23/2015.
 */
public class JGrandExchangeTracker {

    public final Player player;

    public final EntryManager entries;

    public int activeSlot;

    private EntryBuilder newEntry;

    public JGrandExchangeTracker(final Player player){
        this.player = player;

        entries = new EntryManager(player);

        activeSlot = -1;
    }

    public Optional<Entry> activeEntryOpt(){
        return Optional.ofNullable(activeEntry());
    }

    public Entry activeEntry(){
        return activeSlot != -1 ? entries.get(activeSlot) : null;
    }

    public boolean hasActiveEntry(){
        return activeEntry() != null;
    }

    public void ifActiveEntry(final Consumer<Entry> action, final String fmt, final String... args){
        final Entry activeEntry = activeEntry();
        if(activeEntry != null)
            action.accept(activeEntry);
        else
            player.sendf(fmt, args);
    }

    public boolean canOpenInterface(){
        return true;
    }

    public boolean openInterface(){
        if(!canOpenInterface())
            return false;
        JGrandExchangeInterface.open(player);
        return true;
    }

    public boolean buildingNewEntry(){
        return newEntry != null;
    }

    public void ifNewEntry(final Consumer<EntryBuilder> action, final String fmt, final Object... args){
        if(newEntry != null)
            action.accept(newEntry);
        else
            player.sendf(fmt, args);
    }

    public boolean ifNewEntry(final Predicate<EntryBuilder> condition){
        return newEntry != null && condition.test(newEntry);
    }

    public EntryBuilder newEntry(){
        return newEntry;
    }

    public void nullifyNewEntry(){
        newEntry = null;
    }

    public void showEntries(){

    }

    public boolean startNewEntry(final Entry.Type type, final int slot){
        if(buildingNewEntry()){
            player.sendf("You are already building a new entry!");
            return false;
        }
        if(entries.used(slot)){
            player.sendf("You can't create a new entry in this slot!");
            return false;
        }
        activeSlot = slot;
        newEntry = Entry.build(player.getName(), type, slot);
        return true;
    }

    public boolean view(final int slot){
        if(entries.empty(slot)){
            player.sendf("Nothing to view at this slot!");
            return false;
        }
        if(ifNewEntry(e -> e.slot() == slot)){
            player.sendf("You are building an entry in this slot!");
            return false;
        }
        if(hasActiveEntry()){
            player.sendf("You are already viewing an entry!");
            return false;
        }
        activeSlot = slot;
        return true;
    }

    public boolean handleInterfaceInteraction(final int id){
        switch(id){
            case SLOT_1_BUY:
                if(startNewEntry(Entry.Type.BUYING, 0))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_1_SELL:
                if(startNewEntry(Entry.Type.SELLING, 0))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_1_VIEW:
                if(view(0))
                    setEntry(player, entries.get(0));
                return true;
            case SLOT_2_BUY:
                if(startNewEntry(Entry.Type.BUYING, 1))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_2_SELL:
                if(startNewEntry(Entry.Type.SELLING, 1))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_2_VIEW:
                if(view(1))
                    setEntry(player, entries.get(1));
                return true;
            case SLOT_3_BUY:
                if(startNewEntry(Entry.Type.BUYING, 2))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_3_SELL:
                if(startNewEntry(Entry.Type.SELLING, 2))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_3_VIEW:
                if(view(2))
                    setEntry(player, entries.get(2));
                return true;
            case SLOT_4_BUY:
                if(startNewEntry(Entry.Type.BUYING, 3))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_4_SELL:
                if(startNewEntry(Entry.Type.SELLING, 3))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_4_VIEW:
                if(view(3))
                    setEntry(player, entries.get(3));
                return true;
            case SLOT_5_BUY:
                if(startNewEntry(Entry.Type.BUYING, 4))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_5_SELL:
                if(startNewEntry(Entry.Type.SELLING, 4))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_5_VIEW:
                if(view(4))
                    setEntry(player, entries.get(4));
                return true;
            case SLOT_6_BUY:
                if(startNewEntry(Entry.Type.BUYING, 5))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_6_SELL:
                if(startNewEntry(Entry.Type.SELLING, 5))
                    setNewEntry(player, newEntry);
                return true;
            case SLOT_6_VIEW:
                if(view(5))
                    setEntry(player, entries.get(5));
                return true;
            case BACK:
                if(buildingNewEntry())
                    nullifyNewEntry();
                activeSlot = -1;
                showEntries();
                return true;
            case DECREASE_QUANTITY:
                ifNewEntry(e -> {
                    if(e.decreaseItemQuantity())
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before decreasing quantity");
                return true;
            case INCREASE_QUANTITY:
                ifNewEntry(e -> {
                    if(e.increaseItemQuantity())
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before increasing quantity");
                return true;
            case SET_QUANTITY_1:
                ifNewEntry(e -> {
                    if(e.itemQuantity(1))
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before setting quantity");
                return true;
            case SET_QUANTITY_10:
                ifNewEntry(e -> {
                    if(e.itemQuantity(10))
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before setting quantity");
                return true;
            case SET_QUANTITY_100:
                ifNewEntry(e -> {
                    if(e.itemQuantity(100))
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before setting quantity");
                return true;
            case SET_QUANTITY_500:
                ifNewEntry(e -> {
                    if(e.itemQuantity(500))
                        setItemQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice());
                }, "You must create a new entry before setting quantity");
                return true;
            case ENTER_QUANTITY:

                return true;
            case DECREASE_PRICE:
                ifNewEntry(e -> {
                    if(e.decreaseUnitPrice())
                        setItemUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice());
                }, "You must create a new entry before decreasing price");
                return true;
            case INCREASE_PRICE:
                ifNewEntry(e -> {
                    if(e.increaseUnitPrice())
                        setItemUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice());
                }, "You must create a new entry before increasing price");
                return true;
            case DECREASE_PRICE_PERCENT:
                ifNewEntry(e -> {
                    if(e.decreaseUnitPricePercent())
                        setItemUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice());
                }, "You must create a new entry before decreasing price");
                return true;
            case EQUATE_PRICE:

                return true;
            case ENTER_PRICE:

                return true;
            case INCREASE_PRICE_PERCENT:
                ifNewEntry(e -> {
                    if(e.increaseUnitPricePercent())
                        setItemUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice());
                }, "You must create a new entry before increasing price");
                return true;
            case CONFIRM:
                ifNewEntry(e -> {
                    if(!e.canBuild()){
                        player.sendf("Entry is not valid!");
                        return;
                    }
                    if(entries.used(e.slot())){
                        player.sendf("This slot is already in use");
                        return;
                    }
                    final Entry entry = newEntry.build();
                    entries.add(entry);
                    nullifyNewEntry();
                }, "You are not building a new entry right now!");
                return true;
            case CANCEL:
                ifActiveEntry(e -> {

                }, "You are not viewing an entry right now");
                return true;
            case CLAIM_PROGRESS_SLOT:
                ifActiveEntry(e -> {
                    if(e.claims.claimProgress())
                        player.sendf("You successfully claim your progress");
                }, "You are not viewing an entry right now");
                return true;
            case CLAIM_RETURN_SLOT:
                ifActiveEntry(e -> {
                    if(e.claims.claimReturn())
                        player.sendf("You successfully claim your returns");
                }, "You are not viewing an entry right now");
                return true;
            case VIEW_BACK:
                activeSlot = -1;
                showEntries();
                return true;
            default: return false;
        }
    }
}
