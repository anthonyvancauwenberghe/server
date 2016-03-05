package org.hyperion.rs2.model;

import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 3/03/2016.
 */
public class Locations {

    /**
     * Gets called on login to do the required actions, and assign a player his original location.
     * @param player The player
     */
    public static void login(Player player) {
        player.setLocation(Location.getLocation(player));
        player.getLocation().login(player);
        player.getLocation().enter(player);
    }

    /**
     * Gets called on logout to clean up after a player, to make sure he is not in instanced
     * places for example.
     * @param player The player
     */
    public static void logout(Player player) {
        player.getLocation().logout(player);
    }

    /**
     * To add a new location;
     * Take the BOTTOM LEFT and the TOP RIGHT corner
     *
     * ADD Coordinates bottom left corner
     * ADD Coordinates top right corner
     *
     * THIS CAN INCLUDE MORE SQUARES THAN JUST ONE. SIMPLY ADD ANOTHER ONE
     * AFTER THE FIRST 2 NUMBERS. SQUARES CAN OVERLAP
     * AND IT WILL TAKE THE LATER INITIALIZED AS PRIORITY.
     */
    public enum Location {
        BORK(new int[]{3490, 3585}, new int[]{9915, 9970}, true, true, false, false, false, false) {
            @Override
            public void login(Player player) {
                Bork.doDeath(player);
            }

            @Override
            public void logout(Player player) {
                Bork.doDeath(player);
            }

            @Override
            public boolean onDeath(Player player) {
                return Bork.doDeath(player);
            }
        },
        DUEL_ARENA_CHALLENGING_AREA(new int[]{3355, 3360, 3361, 3373, 3374, 3379}, new int[]{3267, 3279, 3272, 3279, 3267, 3286}, false, false, true, false, true, true, Rank.PLAYER),

        EDGEVILLE_BANK_AREA(new int[]{3091, 3094, 3090, 3090, 3095, 3098}, new int[]{3488, 3499, 3494, 3496, 3494, 3499}, false, false, true, false, true, true, Rank.PLAYER),
        EDGEVILLE_BANK_BANKER_AREA(new int[]{3095, 3098}, new int[]{3488, 3493}, false, false, false, false, false, false, Rank.PLAYER),
        AFK_AREA(new int[]{2138, 2164}, new int[]{5091, 5106}, false, true, true, true, true, true, Rank.PLAYER),
        EASTS_BANK_AREA(new int[]{2971, 2983}, new int[]{3605, 3616}, false, true, false, false, true, true, Rank.PLAYER),
        SUPER_DONATOR_AREA(new int[]{2028, 2045}, new int[]{4517, 4541}, false, true, true, false, true, true, Rank.SUPER_DONATOR),
        /*Godwars*/
        GRAARDOR_ROOM(new int[]{2864, 2876, 2869, 2871}, new int[]{5351, 5369, 5370, 5372}, true, true, false, false, false, false, Rank.PLAYER),
        KREE_ARRA_ROOM(new int[]{2824, 2842, 2821, 2823}, new int[]{5296, 5308, 5301, 5303}, true, true, false, false, false, false, Rank.PLAYER),
        TSUTSAROTH_ROOM(new int[]{2918, 2936, 2937, 2940}, new int[]{5318, 5331, 5322, 5326}, false, false, false, false, false, false, Rank.PLAYER),
        ZILYANA_ROOM(new int[]{2889, 2907, 2885, 2888}, new int[]{5258, 5276, 5267, 5270}, true, true, false, false, false, false, Rank.PLAYER),


        //Duel arena (each INSIDE AREA, and VERY ACCURATE, otherwise we'll have dupes)
        //Home maybe
        //Bork (alrdy done)
        //GWD
        //Other bosses
        //Other special areas (funpk, bridding)

        //Main difference in this version is that I sped up pathfinding a buttload
        DEFAULT(null, null);

        /**
         * The corners, from low to high
         */
        private final int[] x, y;
        /**
         * Is the area multi or not
         */
        private final boolean multi;
        /**
         * Can they use summoning or not
         */
        private final boolean summonAllowed;
        /**
         * Can they follow players or not
         */
        private final boolean followingAllowed;
        /**
         * Can they firemake here or not
         */
        private final boolean firemakingAllowed;
        /**
         * Can they use the bank command here or not
         */
        private final boolean bankingAllowed;
        /**
         * Can they use the spawn command here or not
         */
        private final boolean spawningAllowed;
        /**
         * Is there a required rank to be in this area?
         */
        private final Rank minimumRank;

        Location(int[] x, int[] y) {
            this(x, y, false, false, false, false, false, false, Rank.PLAYER);
        }

        Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean firemakingAllowed, boolean bankingAllowed, boolean spawningAllowed) {
            this(x, y, multi, summonAllowed, followingAllowed, firemakingAllowed, bankingAllowed, spawningAllowed, Rank.PLAYER);
        }

        Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean firemakingAllowed, boolean bankingAllowed, boolean spawningAllowed, Rank minimumRank) {
            this.x = x;
            this.y = y;
            this.multi = multi;
            this.summonAllowed = summonAllowed;
            this.followingAllowed = followingAllowed;
            this.firemakingAllowed = firemakingAllowed;
            this.bankingAllowed = bankingAllowed;
            this.spawningAllowed = spawningAllowed;
            this.minimumRank = minimumRank;
        }

        public final int[] getX() {
            return x;
        }

        public final int[] getY() {
            return y;
        }

        public final boolean isMulti() {
            return multi;
        }

        public final boolean isSummonAllowed() {
            return summonAllowed;
        }

        public final boolean isFollowingAllowed() {
            return followingAllowed;
        }

        public final boolean isFiremakingAllowed() {
            return firemakingAllowed;
        }

        public final boolean isBankingAllowed() {
            return bankingAllowed;
        }

        public final boolean isSpawningAllowed() {
            return spawningAllowed;
        }

        public final Rank getMinimumRank() {
            return minimumRank;
        }

        /**
         * This determines what happens on login.
         * This can be left empty, and only needs to be overwritten if it does something
         * @param player The player
         */
        public void login(Player player) {}

        /**
         * This determines what happens when the player enters an area.
         * This can be left empty, and only needs to be overwritten if it does something
         * @param player The player
         */
        public void enter(Player player) {
            //This is a temp block of code for TESTING
            player.sendMessage("Now entering: @dre@" + Misc.ucFirst(name()));
        }

        /**
         * This determines what happens when the player leaves an area.
         * This can be left empty, and only needs to be overwritten if it does something
         * @param player The player
         */
        public void leave(Player player) {}

        /**
         * This determines what happens when the player logs out in an area.
         * This can be left empty, and only needs to be overwritten if it does something
         * @param player The player
         */
        public void logout(Player player) {}

        /**
         * Gets called every time the player moves. This can be used to for
         * example show an interface.
         * @param player The player
         */
        public void process(Player player) {}

        /**
         * Gets called when the player dies. This can be overwritten to make the
         * player do special actions.
         * @param player The player
         * @return {@link true} when it can ignore the other death events. This will
         * cancel out item loss, and all other normal death sequencing, including teleporting away.
         * {@link false} when it still has to do the other death event.
         */
        public boolean onDeath(Player player) {
            return false;
        }

        /**
         * Gets called when an npc is killed in this area.
         * @param killer The player
         * @param npc The npc that got killed
         * @return {@link true} when it can stop executing the other npc death code. Usefull for example in bork.
         */
        public boolean handleKilledNPC(Player killer, NPC npc) {
            return false;
        }

        /**
         * @param player The player attacking.
         * @param target The player being attacked.
         * @return Whether the player can attack another player.
         */
        public boolean canAttack(Player player, Player target) {
            return false;
        }


        /**
         * STATIC
         */

        //TODO MAKE THIS MORE EFFICIENT
        public static boolean inLocation(Entity gc, Location location) {
            if(location == Location.DEFAULT)
                return getLocation(gc) == Location.DEFAULT;
            return inLocation(gc.getPosition().getX(), gc.getPosition().getY(), location);
        }

        public static Location getLocation(Entity gc) {
            for(Location location : Location.values()) {
                if(location != Location.DEFAULT)
                    if(inLocation(gc, location))
                        return location;
            }
            return Location.DEFAULT;
        }

        public static boolean inLocation(int absX, int absY, Location location) {
            int checks = location.getX().length - 1;
            for(int i = 0; i <= checks; i+=2) {
                if(absX >= location.getX()[i] && absX <= location.getX()[i + 1]) {
                    if(absY >= location.getY()[i] && absY <= location.getY()[i + 1]) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static void process(Entity gc) {
        Location newLocation = Location.getLocation(gc);
        if(gc.getLocation() == newLocation) {
            if(gc instanceof Player) {
                Player player = (Player) gc;
                gc.getLocation().process(player);
            }
        } else {
            Location prev = gc.getLocation();
            gc.setLocation(newLocation);
            if(gc instanceof Player) {
                Player player = (Player)gc;
                if(!newLocation.isMulti())
                    player.getActionSender().sendMultiZone(0);
                else
                    player.getActionSender().sendMultiZone(1);
                prev.leave(((Player)gc));
                gc.getLocation().enter(((Player)gc));
            }
        }
    }
}
