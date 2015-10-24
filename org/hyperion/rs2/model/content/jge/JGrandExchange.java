package org.hyperion.rs2.model.content.jge;

import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.jge.entry.claim.Claims;
import org.hyperion.rs2.model.content.jge.entry.progress.ProgressManager;
import org.hyperion.rs2.model.content.jge.itf.JGrandExchangeInterface;
import org.hyperion.rs2.model.iteminfo.ItemInfo;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.sql.MySQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Administrator on 9/23/2015.
 */
public class JGrandExchange {

    public static boolean enabled = true;

    private static JGrandExchange instance;

    private static final Function<Entry, Object> ITEM_KEY = e -> e.itemId;
    private static final Function<Entry, Object> PLAYER_KEY = e -> e.playerName;
    private static final Function<Entry, Object> TYPE_KEY = e -> e.type;

    public static final int DEFAULT_UNIT_PRICE = 1000;

    private final MySQLConnection sql;
    private final Map<Object, List<Entry>> map;

    public JGrandExchange(final MySQLConnection sql){
        this.sql = sql;

        map = new HashMap<>();
    }

    public Stream<Entry> stream(){
        return map.values().stream()
                .flatMap(List::stream)
                .distinct();
    }

    public boolean delete(final Entry entry){
        try(final PreparedStatement delete = sql.prepare("DELETE FROM ge_entries WHERE playerName = ? AND slot = ?")){
            delete.setString(1, entry.playerName);
            delete.setByte(2, (byte)entry.slot);
            if(delete.executeUpdate() != 1)
                return false;
            final String progress = entry.progress.toSaveString();
            if(progress.isEmpty())
                return true;
            try(final PreparedStatement insert = sql.prepare("INSERT INTO ge_history (created, playerName, type, slot, itemId, itemQuantity, unitPrice, currency, progress, cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
                insert.setString(1, entry.date.toString());
                insert.setString(2, entry.playerName);
                insert.setString(3, entry.type.name());
                insert.setByte(4, (byte)entry.slot);
                insert.setShort(5, (short)entry.itemId);
                insert.setInt(6, entry.itemQuantity);
                insert.setInt(7, entry.unitPrice);
                insert.setString(8, entry.currency.name());
                insert.setString(9, progress);
                insert.setBoolean(10, entry.cancelled);
                return insert.executeUpdate() == 1;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean insert(final Entry entry){
        try(final PreparedStatement stmt = sql.prepare("INSERT INTO ge_entries (created, playerName, type, slot, itemId, itemQuantity, unitPrice, currency, progress, claims, cancelled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            stmt.setString(1, entry.date.toString());
            stmt.setString(2, entry.playerName);
            stmt.setString(3, entry.type.name());
            stmt.setByte(4, (byte)entry.slot);
            stmt.setShort(5, (short)entry.itemId);
            stmt.setInt(6, entry.itemQuantity);
            stmt.setInt(7, entry.unitPrice);
            stmt.setString(8, entry.currency.name());
            stmt.setString(9, entry.progress.toSaveString());
            stmt.setString(10, entry.claims.toSaveString());
            stmt.setBoolean(11, entry.cancelled);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateCancelAndClaims(final Entry entry){
        try(final PreparedStatement stmt = sql.prepare("UPDATE ge_entries SET cancelled = ?, claims = ? WHERE playerName = ? AND slot = ?")){
            stmt.setBoolean(1, entry.cancelled);
            stmt.setString(2, entry.claims.toSaveString());
            stmt.setString(3, entry.playerName);
            stmt.setByte(4, (byte)entry.slot);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateProgressAndClaims(final Entry entry){
        try(final PreparedStatement stmt = sql.prepare("UPDATE ge_entries SET progress = ?, claims = ? WHERE playerName = ? AND slot = ?")){
            stmt.setString(1, entry.progress.toSaveString());
            stmt.setString(2, entry.claims.toSaveString());
            stmt.setString(3, entry.playerName);
            stmt.setByte(4, (byte)entry.slot);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateProgress(final Entry entry){
        try(final PreparedStatement stmt = sql.prepare("UPDATE ge_entries SET progress = ? WHERE playerName = ? AND slot = ?")){
            stmt.setString(1, entry.progress.toSaveString());
            stmt.setString(2, entry.playerName);
            stmt.setByte(3, (byte)entry.slot);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateClaims(final Entry entry){
        try(final PreparedStatement stmt = sql.prepare("UPDATE ge_entries SET claims = ? WHERE playerName = ? AND slot = ?")){
            stmt.setString(1, entry.claims.toSaveString());
            stmt.setString(2, entry.playerName);
            stmt.setByte(3, (byte)entry.slot);
            return stmt.executeUpdate() == 1;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public boolean load(){
        try(final ResultSet rs = sql.query("SELECT * FROM ge_entries")){
            while(rs.next()){
                final OffsetDateTime date = OffsetDateTime.parse(rs.getString("created"));
                final String playerName = rs.getString("playerName");
                final Entry.Type type = Entry.Type.valueOf(rs.getString("type"));
                final int slot = rs.getByte("slot");
                final int itemId = rs.getShort("itemId");
                final int itemQuantity = rs.getInt("itemQuantity");
                final int unitPrice = rs.getInt("unitPrice");
                final Entry.Currency currency = Entry.Currency.valueOf(rs.getString("currency"));
                final String progress = rs.getString("progress");
                final String claims = rs.getString("claims");
                final boolean cancelled = rs.getBoolean("cancelled");
                final Entry entry = new Entry(date, playerName, type, slot, itemId, itemQuantity, unitPrice, currency);
                entry.cancelled = cancelled;
                entry.progress = ProgressManager.fromSaveString(entry, progress);
                entry.claims = Claims.fromSaveString(entry, claims);
                add(entry);
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
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
        if(!enabled || submitEntry.cancelled || submitEntry.progress.completed() || submitEntry.progress.remainingQuantity() == 0)
            return;
        final Optional<Entry> opt = stream(submitEntry.type.opposite())
                .filter(e -> {
                    if(e.cancelled || e.progress.completed() || e.progress.remainingQuantity() == 0 || e.itemId != submitEntry.itemId)
                        return false;
                    if(e.type != submitEntry.type.opposite())
                        return false;
                    if(submitEntry.type == Entry.Type.BUYING && e.unitPrice > submitEntry.unitPrice)
                        return false;
                    if(submitEntry.type == Entry.Type.SELLING && e.unitPrice < submitEntry.unitPrice)
                        return false;
                    if(e.playerName.equalsIgnoreCase(submitEntry.playerName))
                        return false;
                    if(e.currency != submitEntry.currency)
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
        final ProgressManager submitProgress = submitEntry.progress.copy();
        final Claims submitClaims = submitEntry.claims.copy();
        final ProgressManager matchedProgress = matchedEntry.progress.copy();
        final Claims matchedClaims = matchedEntry.claims.copy();
        switch(submitEntry.type){
            case BUYING:
                //matchedEntry = selling entry
                submitEntry.progress.add(matchedEntry.playerName, Math.min(matchedEntry.unitPrice, submitEntry.unitPrice), maxQuantity);
                submitEntry.claims.addProgress(submitEntry.itemId, maxQuantity);
                if(submitEntry.unitPrice > matchedEntry.unitPrice)
                    submitEntry.claims.addReturn(submitEntry.currency.itemId, (submitEntry.unitPrice - matchedEntry.unitPrice) * maxQuantity);
                matchedEntry.progress.add(submitEntry.playerName, matchedEntry.unitPrice, maxQuantity);
                matchedEntry.claims.addProgress(matchedEntry.currency.itemId, maxQuantity * matchedEntry.unitPrice);
                break;
            case SELLING:
                //matchedEntry = buying entry
                matchedEntry.progress.add(submitEntry.playerName, Math.min(submitEntry.unitPrice, submitEntry.unitPrice), maxQuantity);
                matchedEntry.claims.addProgress(submitEntry.itemId, maxQuantity);
                if(matchedEntry.unitPrice > submitEntry.unitPrice)
                    matchedEntry.claims.addReturn(matchedEntry.currency.itemId, (matchedEntry.unitPrice - submitEntry.unitPrice) * maxQuantity);
                submitEntry.progress.add(matchedEntry.playerName, submitEntry.unitPrice, maxQuantity);
                submitEntry.claims.addProgress(submitEntry.currency.itemId, maxQuantity * submitEntry.unitPrice);
                break;
        }
        if(!updateProgressAndClaims(submitEntry) || !updateProgressAndClaims(matchedEntry)){
            submitEntry.progress = submitProgress;
            submitEntry.claims = submitClaims;
            matchedEntry.progress = matchedProgress;
            matchedEntry.claims = matchedClaims;
            System.err.printf("ERROR UPDATING GE BETWEEN %s and %s%n", submitEntry.playerName, matchedEntry.playerName);
            return;
        }
        submitEntry.ifPlayer(p -> {
            p.getGrandExchangeTracker().notifyChanges(false);
            p.getLogManager().add(LogEntry.geProgress(submitEntry.progress.last()));
//            p.sendf("[GE Update] %s %s %s x %,d %s you @ %,d %s!",
//                    matchedEntry.playerName, matchedEntry.type.pastTense,
//                    submitEntry.item().getDefinition().getName(), maxQuantity,
//                    submitEntry.type == Entry.Type.BUYING ? "to" : "from",
//                    submitEntry.unitPrice, submitEntry.currency.shortName);
            if (p.getGrandExchangeTracker().activeSlot == submitEntry.slot)
                JGrandExchangeInterface.ViewingEntry.set(p, submitEntry);
            else
                JGrandExchangeInterface.Entries.setAll(p, p.getGrandExchangeTracker().entries);
        });
        matchedEntry.ifPlayer(p -> {
            p.getGrandExchangeTracker().notifyChanges(false);
            p.getLogManager().add(LogEntry.geProgress(matchedEntry.progress.last()));
//            p.sendf("[GE Update] %s %s %s x %,d %s you @ %,d %s!",
//                    submitEntry.playerName, submitEntry.type.pastTense,
//                    submitEntry.item().getDefinition().getName(), maxQuantity,
//                    matchedEntry.type == Entry.Type.BUYING ? "to" : "from",
//                    submitEntry.unitPrice, submitEntry.currency.shortName);
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

    public IntSummaryStatistics itemUnitPriceStats(final int itemId, final Entry.Type type, final Entry.Currency currency){
        return stream(itemId)
                .filter(e -> !e.cancelled && e.type == type && e.currency == currency)
                .mapToInt(e -> e.unitPrice)
                .summaryStatistics();
    }

    public int defaultItemUnitPrice(final int itemId, final Entry.Type type, final Entry.Currency currency){
        try(final PreparedStatement stmt = sql.prepare("SELECT AVG(unitPrice) FROM ge_history WHERE itemId = ? AND type = ? AND currency = ?")){
            stmt.setShort(1, (short)itemId);
            stmt.setString(2, type.name());
            stmt.setString(3, currency.name());
            try(final ResultSet rs = stmt.executeQuery()){
                if(!rs.next())
                    return DEFAULT_UNIT_PRICE;
                final int avg = (int)Math.round(rs.getDouble(1));
                return avg < 1 ? DEFAULT_UNIT_PRICE : avg;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return contains(itemId) ?
                    (int)itemUnitPriceStats(itemId, type, currency).getAverage()
                    : DEFAULT_UNIT_PRICE;
        }
    }

    public static JGrandExchange getInstance(){
        return instance;
    }

    public static boolean init(final MySQLConnection sql){
        ItemInfo.geBlacklist.load();
        instance = new JGrandExchange(sql);
        return instance.load();
    }
}
