package org.hyperion.rs2.model.content.jge;

import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Administrator on 9/23/2015.
 */
public class JGrandExchange {

    private static JGrandExchange instance;

    private static final Function<Entry, Object> ITEM_KEY = e -> e.itemId;
    private static final Function<Entry, Object> PLAYER_KEY = e -> e.playerName;
    private static final Function<Entry, Object> TYPE_KEY = e -> e.type;

    public static final int DEFAULT_UNIT_PRICE = 1000;

    private final Map<Object, List<Entry>> map;

    public JGrandExchange(){
        map = new HashMap<>();
    }

    private void add(final Entry entry, final Function<Entry, Object> key){
        final Object k = key.apply(entry);
        map.putIfAbsent(k, new ArrayList<>());
        map.get(k).add(entry);
    }

    private void remove(final Entry entry, final Function<Entry, Object> key){
        final Object k = key.apply(entry);
        if(map.containsKey(k))
            map.get(k).remove(entry);
    }

    public void add(final Entry entry){
        add(entry, ITEM_KEY);
        add(entry, PLAYER_KEY);
        add(entry, TYPE_KEY);
    }

    public void remove(final Entry entry){
        remove(entry, ITEM_KEY);
        remove(entry, PLAYER_KEY);
        remove(entry, TYPE_KEY);
    }

    public void submit(final Entry submitEntry){
        final Optional<Entry> opt = stream(submitEntry.type.opposite())
                .filter(e -> {
                    if(e.cancelled || e.progress.completed() || e.itemId != submitEntry.itemId)
                        return false;
                    if(e.type != submitEntry.type.opposite())
                        return false;
                    if(submitEntry.type == Entry.Type.BUYING && e.unitPrice > submitEntry.unitPrice)
                        return false;
                    if(submitEntry.type == Entry.Type.SELLING && e.unitPrice < submitEntry.unitPrice)
                        return false;
                    if(e.playerName.equalsIgnoreCase(submitEntry.playerName))
                        return false;
                    //maybe some other criteria
                    return true;
                })
                .sorted(Comparator.comparingInt(e -> e.unitPrice))
                .min(Comparator.comparing(e -> e.date));
        if(!opt.isPresent())
            return;
        final Entry matchedEntry = opt.get();
        final int submitRemaining = submitEntry.progress.remainingQuantity();
        final int matchedRemaining = matchedEntry.progress.remainingQuantity();
        final int maxQuantity = submitRemaining > matchedRemaining ? matchedRemaining : submitRemaining;
        switch(submitEntry.type){
            case BUYING:
                //matchedEntry = selling entry
                submitEntry.progress.add(matchedEntry.playerName, maxQuantity);
                submitEntry.claims.addProgress(submitEntry.itemId, maxQuantity);
                if(submitEntry.unitPrice > matchedEntry.unitPrice)
                    submitEntry.claims.addReturn(submitEntry.currency.itemId, submitEntry.unitPrice - matchedEntry.unitPrice);
                matchedEntry.progress.add(submitEntry.playerName, maxQuantity);
                matchedEntry.claims.addProgress(matchedEntry.currency.itemId, maxQuantity * matchedEntry.unitPrice);
                break;
            case SELLING:
                //matchedEntry = buying entry
                matchedEntry.progress.add(submitEntry.playerName, maxQuantity);
                matchedEntry.claims.addProgress(submitEntry.itemId, maxQuantity);
                if(matchedEntry.unitPrice > submitEntry.unitPrice)
                    matchedEntry.claims.addReturn(matchedEntry.currency.itemId, matchedEntry.unitPrice - submitEntry.unitPrice);
                submitEntry.progress.add(submitEntry.playerName, maxQuantity);
                submitEntry.claims.addProgress(submitEntry.currency.itemId, maxQuantity * submitEntry.unitPrice);
                break;
        }
        submitEntry.ifPlayer(p -> {
            //p.sendf("[GE Update] %s %s %s x %,d %s you @ %,d %s!",
            //        matchedEntry.playerName, matchedEntry.type.pastTense,
            //        submitEntry.item().getDefinition().getName(), maxQuantity,
            //        submitEntry.type == Entry.Type.BUYING ? "to" : "from",
            //        submitEntry.unitPrice, submitEntry.currency.shortName);
            p.sendf("One or more of your Grand Exchange offers have been updated!");
            if (p.getGrandExchangeTracker().activeSlot == submitEntry.slot)
                JGrandExchangeInterface.ViewingEntry.set(p, submitEntry);
            else
                JGrandExchangeInterface.Entries.setAll(p, p.getGrandExchangeTracker().entries);
        });
        matchedEntry.ifPlayer(p -> {
            //p.sendf("[GE Update] %s %s %s x %,d %s you @ %,d %s!",
            //        submitEntry.playerName, submitEntry.type.pastTense,
            //        submitEntry.item().getDefinition().getName(), maxQuantity,
            //        matchedEntry.type == Entry.Type.BUYING ? "to" : "from",
            //        submitEntry.unitPrice, submitEntry.currency.shortName);
            p.sendf("One or more of your Grand Exchange offers have been updated!");
            if (p.getGrandExchangeTracker().activeSlot == matchedEntry.slot)
                JGrandExchangeInterface.ViewingEntry.set(p, matchedEntry);
            else
                JGrandExchangeInterface.Entries.setAll(p, p.getGrandExchangeTracker().entries);
        });
        if(!submitEntry.progress.completed())
            submit(submitEntry); //what if there are other entries
    }

    public List<Entry> get(final Object playerOrItemId){
        return map.getOrDefault(playerOrItemId, Collections.emptyList());
    }

    public Stream<Entry> stream(final Object playerOrItemId){
        return get(playerOrItemId).stream();
    }

    public boolean contains(final Object playerOrItemId){
        return !get(playerOrItemId).isEmpty();
    }

    public IntSummaryStatistics itemUnitPriceStats(final int itemId){
        return stream(itemId)
                .mapToInt(e -> e.unitPrice)
                .summaryStatistics();
    }

    public int defaultItemUnitPrice(final int itemId){
        return contains(itemId) ?
                (int)itemUnitPriceStats(itemId).getAverage()
                : DEFAULT_UNIT_PRICE;
    }

    public static JGrandExchange getInstance(){
        if(instance == null)
            instance = new JGrandExchange();
        return instance;
    }

    public static void init(){
        instance = new JGrandExchange();
    }
}
