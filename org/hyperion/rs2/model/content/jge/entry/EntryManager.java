package org.hyperion.rs2.model.content.jge.entry;

import org.hyperion.rs2.model.Player;

import java.util.Optional;

/**
 * Created by Administrator on 9/24/2015.
 */
public class EntryManager {

    public static final int NUMBER_OF_SLOTS = 6;

    public final Player player;

    private final Entry[] entries;

    public EntryManager(final Player player){
        this.player = player;

        entries = new Entry[NUMBER_OF_SLOTS];
    }

    public boolean used(final int slot){
        return get(slot) != null;
    }

    public boolean empty(final int slot){
        return !used(slot);
    }

    public Entry get(final int slot){
        return entries[slot];
    }

    public Optional<Entry> opt(final int slot){
        return Optional.ofNullable(get(slot));
    }

    public void add(final Entry entry){
        entries[entry.slot] = entry;
    }

    public void remove(final Entry entry){
        entries[entry.slot] = null;
    }
}
