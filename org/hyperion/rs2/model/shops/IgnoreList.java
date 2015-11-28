package org.hyperion.rs2.model.shops;

import java.util.LinkedList;

/**
 * Created by Gilles on 28/11/2015.
 */
public class IgnoreList {

    public static final int SIZE = 200;

    public static final int EMPTY_IGNORE_SPOT = 0;

    private long[] ignores = new long[SIZE];

    private long[] previousIgnores = new long[SIZE];

    public void clear() {
        ignores = new long[SIZE];
    }

    public long[] toArray() {
        return ignores;
    }

    private boolean loaded = false;

    /**
     * @param loaded
     */
    public void setLoaded(boolean loaded) {
        if(loaded) {
            if(this.loaded) {
                System.out.println("Was already loaded!");
            } else {
                this.loaded = true;
                updatePreviousIgnores();
            }
        } else {
            System.out.println("Invalid input");
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void updatePreviousIgnores() {
        for(int i = 0; i < SIZE; i++) {
            previousIgnores[i] = ignores[i];
        }
    }


    public LinkedList<Integer> getChangedSlots() {
        LinkedList<Integer> changedSlots = new LinkedList<>();
        for(int i = 0; i < SIZE; i++) {
            long ignore = ignores[i];
            long previousignore = previousIgnores[i];
            if(ignore != previousignore)
                changedSlots.add(i);
        }
        return changedSlots;
    }

    public boolean add(long ignore) {
        for(int i = 0; i < ignores.length; i++) {
            if(ignores[i] == EMPTY_IGNORE_SPOT) {
                ignores[i] = ignore;
                return true;
            }
        }
        return false;
    }

    public void set(long friend, int slot) {
        ignores[slot] = friend;
    }

    public boolean remove(long ignore) {
        for(int i = 0; i < ignores.length; i++) {
            if(ignores[i] == ignore) {
                ignores[i] = EMPTY_IGNORE_SPOT;
                return true;
            }
        }
        return false;
    }

    public boolean contains(long ignore) {
        for(long f : ignores) {
            if(f == ignore)
                return true;
        }
        return false;
    }
}
