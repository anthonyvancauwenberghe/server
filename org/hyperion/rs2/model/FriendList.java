package org.hyperion.rs2.model;

import java.util.LinkedList;

public class FriendList {

    public static final int SIZE = 200;

    public static final int EMPTY_FRIEND_SPOT = 0;
    private final long[] previousFriends = new long[SIZE];
    private long[] friends = new long[SIZE];
    private boolean loaded = false;

    public void clear() {
        friends = new long[SIZE];
    }

    public long[] toArray() {
        return friends;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded
     */
    public void setLoaded(final boolean loaded) {
        if(loaded){
            if(this.loaded){
                System.out.println("Was already loaded!");
            }else{
                this.loaded = true;
                updatePreviousFriends();
            }
        }else{
            System.out.println("Invalid input");
        }
    }

    public void updatePreviousFriends() {
        System.arraycopy(friends, 0, previousFriends, 0, SIZE);
    }


    public LinkedList<Integer> getChangedSlots() {
        final LinkedList<Integer> changedSlots = new LinkedList<Integer>();
        for(int i = 0; i < SIZE; i++){
            final long friend = friends[i];
            final long previousFriend = previousFriends[i];
            if(friend != previousFriend)
                changedSlots.add(i);
        }
        return changedSlots;
    }

    public boolean add(final long friend) {
        for(int i = 0; i < friends.length; i++){
            if(friends[i] == EMPTY_FRIEND_SPOT){
                friends[i] = friend;
                return true;
            }
        }
        return false;
    }

    public void set(final long friend, final int slot) {
        friends[slot] = friend;
    }

    public boolean remove(final long friend) {
        for(int i = 0; i < friends.length; i++){
            if(friends[i] == friend){
                friends[i] = EMPTY_FRIEND_SPOT;
                return true;
            }
        }
        return false;
    }

    public boolean contains(final long friend) {
        for(final long f : friends){
            if(f == friend)
                return true;
        }
        return false;
    }
}
