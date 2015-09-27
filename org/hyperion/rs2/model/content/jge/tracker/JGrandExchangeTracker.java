package org.hyperion.rs2.model.content.jge.tracker;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.entry.EntryBuilder;
import org.hyperion.rs2.model.content.jge.entry.EntryManager;

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

        loadEntries();
    }

    public void loadEntries(){
        JGrandExchange.getInstance().get(player.getName().toLowerCase())
                .forEach(entries::add);
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

    public void openInterface(){
        Entries.open(player, entries);
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
        activeSlot = -1;
        openInterface();
    }

    public boolean startNewEntry(final Entry.Type type, final int slot){
        if(buildingNewEntry())
            nullifyNewEntry();
        if(entries.used(slot)){
            player.sendf("You can't create a new entry in this slot!");
            return false;
        }
        newEntry = Entry.build(player, type, slot, player.hardMode() ? Entry.Currency.COINS : Entry.Currency.PK_TICKETS);
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
        if(hasActiveEntry())
            activeSlot = -1;
        activeSlot = slot;
        return true;
    }

    public boolean handleInterfaceInteraction(final int id){
        switch(id){
            case SLOT_1_BUY:
                if(startNewEntry(Entry.Type.BUYING, 0))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_1_SELL:
                if(startNewEntry(Entry.Type.SELLING, 0))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_1_VIEW:
                if(view(0))
                    ViewingEntry.open(player, entries.get(0));
                return true;
            case SLOT_2_BUY:
                if(startNewEntry(Entry.Type.BUYING, 1))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_2_SELL:
                if(startNewEntry(Entry.Type.SELLING, 1))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_2_VIEW:
                if(view(1))
                    ViewingEntry.open(player, entries.get(1));
                return true;
            case SLOT_3_BUY:
                if(startNewEntry(Entry.Type.BUYING, 2))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_3_SELL:
                if(startNewEntry(Entry.Type.SELLING, 2))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_3_VIEW:
                if(view(2))
                    ViewingEntry.open(player, entries.get(2));
                return true;
            case SLOT_4_BUY:
                if(startNewEntry(Entry.Type.BUYING, 3))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_4_SELL:
                if(startNewEntry(Entry.Type.SELLING, 3))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_4_VIEW:
                if(view(3))
                    ViewingEntry.open(player, entries.get(3));
                return true;
            case SLOT_5_BUY:
                if(startNewEntry(Entry.Type.BUYING, 4))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_5_SELL:
                if(startNewEntry(Entry.Type.SELLING, 4))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_5_VIEW:
                if(view(4))
                    ViewingEntry.open(player, entries.get(4));
                return true;
            case SLOT_6_BUY:
                if(startNewEntry(Entry.Type.BUYING, 5))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_6_SELL:
                if(startNewEntry(Entry.Type.SELLING, 5))
                    NewEntry.open(player, newEntry);
                return true;
            case SLOT_6_VIEW:
                if(view(5))
                    ViewingEntry.open(player, entries.get(5));
                return true;
            case BACK:
                if(buildingNewEntry())
                    nullifyNewEntry();
                showEntries();
                return true;
            case DECREASE_QUANTITY:
                ifNewEntry(e -> {
                    if(e.validItem() && e.decreaseItemQuantity())
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before decreasing quantity");
                return true;
            case INCREASE_QUANTITY:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseItemQuantity())
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before increasing quantity");
                return true;
            case INCREASE_QUANTITY_1:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseItemQuantity(1))
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before setting quantity");
                return true;
            case INCREASE_QUANTITY_10:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseItemQuantity(10))
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before setting quantity");
                return true;
            case INCREASE_QUANTITY_100:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseItemQuantity(100))
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before setting quantity");
                return true;
            case INCREASE_QUANTITY_500:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseItemQuantity(500))
                        NewEntry.setQuantityAndTotalPrice(player, e.itemQuantity(), e.totalPrice(), e.currency());
                }, "You must create a new entry before setting quantity");
                return true;
            case ENTER_QUANTITY:
                ifNewEntry(e -> {
                    if (e.validItem())
                        DialogueManager.openDialogue(player, 600);
                }, "You are not building a new entry right now!");
                return true;
            case DECREASE_PRICE:
                ifNewEntry(e -> {
                    if(e.validItem() && e.decreaseUnitPrice())
                        NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
                }, "You must create a new entry before decreasing price");
                return true;
            case INCREASE_PRICE:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseUnitPrice())
                        NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
                }, "You must create a new entry before increasing price");
                return true;
            case DECREASE_PRICE_PERCENT:
                ifNewEntry(e -> {
                    if(e.validItem() && e.decreaseUnitPricePercent())
                        NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
                }, "You must create a new entry before decreasing price");
                return true;
            case EQUATE_PRICE:
                ifNewEntry(e -> {
                    if(e.validItem() && e.unitPrice(JGrandExchange.getInstance().defaultItemUnitPrice(e.itemId())))
                        NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
                }, "You must create a new entry before equating price");
                return true;
            case ENTER_PRICE:
                ifNewEntry(e -> {
                    if (e.validItem())
                        DialogueManager.openDialogue(player, 601);
                }, "You are not building a new entry right now!");
                return true;
            case INCREASE_PRICE_PERCENT:
                ifNewEntry(e -> {
                    if(e.validItem() && e.increaseUnitPricePercent())
                        NewEntry.setUnitPriceAndTotalPrice(player, e.unitPrice(), e.totalPrice(), e.currency());
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
                    switch(e.type()){
                        case BUYING: {
                            final int max = player.getInventory().getCount(e.currency().itemId);
                            if(e.totalPrice() > max){
                                player.sendf("You need %,d more %s to %s %,d %s!",
                                        e.totalPrice() - max, e.currency().shortName, e.type().singleName,
                                        e.itemQuantity(), e.item().getDefinition().getName());
                                return;
                            }
                            if(player.getInventory().remove(Item.create(e.currency().itemId, e.totalPrice())) != e.totalPrice()){
                                player.sendf("Something went wrong!");
                                return;
                            }
                            break;
                        }
                        case SELLING: {
                            final int max = player.getInventory().getCount(e.itemId());
                            if(e.itemQuantity() > max){
                                player.sendf("You don't have that many %s!", e.item().getDefinition().getName());
                                return;
                            }
                            if(player.getInventory().remove(Item.create(e.itemId(), e.itemQuantity())) != e.itemQuantity()){
                                player.sendf("Something went wrong!");
                                return;
                            }
                            break;
                        }
                    }
                    final Entry entry = newEntry.build();
                    entries.add(entry);
                    JGrandExchange.getInstance().add(entry);
                    nullifyNewEntry();
                    showEntries();
                    JGrandExchange.getInstance().submit(entry);
                }, "You are not building a new entry right now!");
                return true;
            case CANCEL:
                ifActiveEntry(e -> {
                    if(e.cancelled){
                        player.sendf("This entry is already cancelled!");
                        return;
                    }
                    if(e.progress.completed()){
                        if(e.claims.empty())
                            return;
                        player.sendf("This entry is already completed!");
                        return;
                    }
                    e.cancelled = true;
                    switch(e.type){
                        case BUYING:
                            e.claims.addReturn(e.currency.itemId, e.progress.remainingQuantity() * e.unitPrice);
                            ViewingEntry.setReturnClaim(player, e.claims.returnSlot.item());
                            break;
                        case SELLING:
                            e.claims.addProgress(e.itemId, e.progress.remainingQuantity());
                            ViewingEntry.setReturnClaim(player, e.claims.returnSlot.item());
                            break;
                    }
                    ViewingEntry.setProgressBar(player, e);
                }, "You are not viewing an entry right now");
                return true;
            case CLAIM_PROGRESS_SLOT:
                ifActiveEntry(e -> {
                    if(e.claims.progressSlot.valid() && e.claims.claimProgress()){
                        ViewingEntry.setProgressClaim(player, null);
                        player.sendf("You successfully claim your progress");
                        if((e.cancelled && e.claims.empty()) || e.finished()){
                            entries.remove(e);
                            JGrandExchange.getInstance().remove(e);
                            showEntries();
                        }
                    }
                }, "You are not viewing an entry right now");
                return true;
            case CLAIM_RETURN_SLOT:
                ifActiveEntry(e -> {
                    if(e.claims.returnSlot.valid() && e.claims.claimReturn()){
                        ViewingEntry.setReturnClaim(player, null);
                        player.sendf("You successfully claim your returns");
                        if((e.cancelled && e.claims.empty()) || e.finished()){
                            entries.remove(e);
                            JGrandExchange.getInstance().remove(e);
                            showEntries();
                        }
                    }
                }, "You are not viewing an entry right now");
                return true;
            case VIEW_BACK:
                showEntries();
                return true;
            default: return false;
        }
    }
}
