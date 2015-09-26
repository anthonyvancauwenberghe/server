package org.hyperion.rs2.model.content.jge;

import org.hyperion.rs2.model.content.jge.entry.Entry;

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

    public void check(final Entry entry){
        final Optional<Entry> opt = stream(entry.type.opposite())
                .filter(e -> {
                    if(e.cancelled || e.finished() || e.itemId != entry.itemId)
                        return false;
                    if(e.unitPrice > entry.unitPrice)
                        return false;
                    if(e.playerName.equalsIgnoreCase(entry.playerName))
                        return false;
                    //maybe some other criteria
                    return true;
                })
                .sorted(Comparator.comparingInt(e -> e.unitPrice))
                .min(Comparator.comparing(e -> e.date));
        if(!opt.isPresent())
            return;
        final Entry matched = opt.get();
        //fgure out how much they can buy/sell
        switch(entry.type){
            case BUYING: {
                final int remaining = entry.progress.remainingQuantity();
                break;
            }
            case SELLING: {

                break;
            }
        }
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
