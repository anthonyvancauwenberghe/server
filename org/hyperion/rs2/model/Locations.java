package org.hyperion.rs2.model;

import org.hyperion.engine.task.impl.OverloadStatsTask;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.Edgeville;
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
        player.getLocation().enterArea(player);
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
     * AND IT WILL TAKE THE FIRST INITIALIZED AS PRIORITY.
     */
    public enum Location {
        BORK(new int[]{3490, 3585}, new int[]{9915, 9970}, true, true, false, false, false, false) {
            @Override
            public boolean onDeath(Player player) {
                return Bork.doDeath(player);
            }
        },
        FUN_PK_AREA(new int[]{2586, 2602, 2603, 2606, 2581, 2585}, new int[]{3151, 3172, 3151, 3172, 3151, 3172}, true, true, true, false, false, false, Rank.PLAYER) {
            @Override
            public void enter(Player player) {
                if(!player.attackOption) {
                    player.getActionSender().sendPlayerOption("Attack", 2, 0);
                    player.attackOption = true;
                    if (player.getNpcState()) {
                        player.setPNpc(-1);
                    }
                }
            }

            @Override
            public boolean canAttack(Player player, Player target) {
                return true;
            }

            @Override
            public void leave(Player player) {
                player.getActionSender().sendPlayerOption("null", 2, 1);
                player.attackOption = false;
            }

            @Override
            public boolean onDeath(Player player) {
                player.setTeleportTarget(Position.create(2594, 3157, 0));
                return false;
            }
        },

        EDGEVILLE_BANK_BANKER_AREA(new int[]{3095, 3098}, new int[]{3488, 3493}, false, false, false, false, false, false, Rank.OWNER),
        AFK_AREA(new int[]{2138, 2164}, new int[]{5091, 5106}, false, true, true, true, true, true, Rank.PLAYER),
        DONATOR_PLACE_AREA(new int[]{2344, 2389}, new int[]{4938, 4987}, false, true, true, false, true, true, Rank.DONATOR),
        SUPER_DONATOR_AREA(new int[]{2028, 2045}, new int[]{4517, 4541}, false, true, true, false, true, true, Rank.SUPER_DONATOR),
        SUPER_DONATOR_PVM_AREA(new int[]{3464, 3511}, new int[]{9478, 9524}, false, true, true, false, true, true, Rank.SUPER_DONATOR),
        GRAARDOR_ROOM(new int[]{2864, 2876, 2869, 2871}, new int[]{5351, 5369, 5370, 5372}, true, true, false, false, false, false, Rank.PLAYER),
        KREE_ARRA_ROOM(new int[]{2824, 2842, 2821, 2823}, new int[]{5296, 5308, 5301, 5303}, true, true, false, false, false, false, Rank.PLAYER),
        TSUTSAROTH_ROOM(new int[]{2918, 2936, 2937, 2940}, new int[]{5318, 5331, 5322, 5326}, true, false, false, false, false, false, Rank.PLAYER),
        ZILYANA_ROOM(new int[]{2889, 2907, 2885, 2888}, new int[]{5258, 5276, 5267, 5270}, true, true, false, false, false, false, Rank.PLAYER),
        WILDERNESS_MULTI(new int[]{3004, 3063, 3134, 3325, 3196, 3325, 3149, 3325, 3149, 3215, 3215, 3400, 3014, 3215, 2989, 3008}, new int[]{3601, 3716, 3523, 3648, 3646, 3781, 3781, 3845, 3845, 3903, 3845, 4000, 3856, 3903, 3914, 3930}, true, true, true, true, false, false, Rank.PLAYER) {
            @Override
            public void enter(Player player) {
                if(!player.attackOption) {
                    player.getActionSender().sendPlayerOption("Attack", 2, 0);
                    player.setCanSpawnSet(false);
                    player.attackOption = true;
                    if (player.getNpcState()) {
                        player.setPNpc(-1);
                    }
                    if (player.isOverloaded())
                        OverloadStatsTask.OverloadFactory.applyBoosts(player);
                }
            }

            @Override
            public void leave(Player player) {
                player.setCanSpawnSet(true);
                player.cE.getDamageDealt().clear();
                player.getActionSender().sendPlayerOption("null", 2, 1);
                player.attackOption = false;
                player.getActionSender().sendWildLevel(-1);
                player.wildernessLevel = -1;
            }

            @Override
            public boolean canAttack(Player player, Player target) {
                int difference = Math.min(player.wildernessLevel, target.wildernessLevel);
                int combatDifference = player.getSkills().getCombatLevel() - target.getSkills().getCombatLevel();
                if(combatDifference < 0)
                    combatDifference = target.getSkills().getCombatLevel() - player.getSkills().getCombatLevel();

                if (combatDifference <= difference && difference > 0)
                    return true;
                if(difference <= 0) {
                    player.sendMessage("Your opponent is not in the wilderness.");
                } else {
                    player.sendMessage("You need to go deeper into the wilderness to attack this player.");
                }
                return false;
            }

            @Override
            public boolean canTeleport(Player player) {
                if(player.wildernessLevel > 20 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                    player.sendMessage("You cannot teleport above level 20 wilderness.");
                    return false;
                }
                if(player.cE.getOpponent() != null && player.wildernessLevel > 0) {
                    player.sendMessage("@blu@You have lost EP because you have teleported during combat.");
                    player.removeEP();
                }
                return true;
            }

            @Override
            public void process(Player player) {
                int wildLevel = -1;
                int absX = player.getPosition().getX();
                int absY = player.getPosition().getY();
                if ((absY >= 10340 && absY <= 10364 && absX <= 3008 && absX >= 2992))
                    wildLevel = (((absY - 10340) / 8) + 3);
                else if ((absY >= 3520 && absY <= 3967 && absX <= 3392 && absX >= 2942))
                    wildLevel = (((absY - 3520) / 8) + 3);
                else if (absY <= 10349 && absX >= 3010 && absX <= 3058 && absY >= 10306)
                    wildLevel = 57;
                else if (absX >= 3064 && absX <= 3070 && absY >= 10252 && absY <= 10260)
                    wildLevel = 53;

                if(player.wildernessLevel != wildLevel) {
                    player.wildernessLevel = wildLevel;
                    if (wildLevel != -1)
                        player.getActionSender().sendWildLevel(player.wildernessLevel);
                }
            }
        },
        WILDERNESS(new int[]{2941, 3392, 2986, 3012, 3653, 3706, 3650, 3653}, new int[]{3520, 3968, 10338, 10366, 3441, 3538, 3457, 3472}, false, true, true, true, false, false, Rank.PLAYER) {
            @Override
            public void enter(Player player) {
                if(!player.attackOption) {
                    player.getActionSender().sendPlayerOption("Attack", 2, 0);
                    player.setCanSpawnSet(false);
                    player.attackOption = true;
                    if (player.getNpcState()) {
                        player.setPNpc(-1);
                    }
                    if (player.isOverloaded())
                        OverloadStatsTask.OverloadFactory.applyBoosts(player);
                }
            }

            @Override
            public void leave(Player player) {
                player.setCanSpawnSet(true);
                player.cE.getDamageDealt().clear();
                player.getActionSender().sendPlayerOption("null", 2, 1);
                player.attackOption = false;
                player.getActionSender().sendWildLevel(-1);
                player.wildernessLevel = -1;
            }

            @Override
            public boolean canAttack(Player player, Player target) {
                int difference = Math.min(player.wildernessLevel, target.wildernessLevel);
                int combatDifference = player.getSkills().getCombatLevel() - target.getSkills().getCombatLevel();
                if(combatDifference < 0)
                    combatDifference = target.getSkills().getCombatLevel() - player.getSkills().getCombatLevel();

                if (combatDifference <= difference && difference > 0)
                    return true;
                if(difference <= 0) {
                    player.sendMessage("Your opponent is not in the wilderness.");
                } else {
                    player.sendMessage("You need to go deeper into the wilderness to attack this player.");
                }
                return false;
            }

            @Override
            public boolean canTeleport(Player player) {
                if(player.wildernessLevel > 20 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                    player.sendMessage("You cannot teleport above level 20 wilderness.");
                    return false;
                }
                if(player.cE.getOpponent() != null && player.wildernessLevel > 0) {
                    player.sendMessage("@blu@You have lost EP because you have teleported during combat.");
                    player.removeEP();
                }
                return true;
            }

            @Override
            public void process(Player player) {
                int wildLevel = -1;
                int absX = player.getPosition().getX();
                int absY = player.getPosition().getY();
                if ((absY >= 10340 && absY <= 10364 && absX <= 3008 && absX >= 2992))
                    wildLevel = (((absY - 10340) / 8) + 3);
                else if ((absY >= 3520 && absY <= 3967 && absX <= 3392 && absX >= 2942))
                    wildLevel = (((absY - 3520) / 8) + 3);
                else if (absY <= 10349 && absX >= 3010 && absX <= 3058 && absY >= 10306)
                    wildLevel = 57;
                else if (absX >= 3064 && absX <= 3070 && absY >= 10252 && absY <= 10260)
                    wildLevel = 53;

                if(player.wildernessLevel != wildLevel) {
                    player.wildernessLevel = wildLevel;
                    if (wildLevel != -1)
                        player.getActionSender().sendWildLevel(player.wildernessLevel);
                }
            }
        },
        FIGHT_CAVES(new int[]{2360, 2445}, new int[]{5045, 5125}, true, true, false, false, false, false, Rank.PLAYER) {
            @Override
            public boolean onDeath(Player player) {
                player.getActionSender().sendMessage("Too bad, you didn't complete fight caves!");
                player.setTeleportTarget(Position.create(2439, 5171, 0), false);
                return true;
            }

            @Override
            public void leave(Player player) {
                player.fightCavesWave = 0;
                player.getActionSender().showInterfaceWalkable(- 1);
            }

            @Override
            public boolean canTeleport(Player player) {
                player.sendMessage("You cannot teleport from the Fight Caves.");
                return false;
            }
        },
        FIGHT_PITS(new int[]{2370, 2425}, new int[]{5133, 5167}, true, true, true, false, false, false, Rank.PLAYER) {
            @Override
            public boolean onDeath(Player player) {
                return FightPits.pitsDeath(player);
            }

            @Override
            public void enter(Player player) {
                if(!FightPits.inGame(player)) {
                    player.setTeleportTarget(Position.create(2399, 5178, 0), false);
                }
                if(!player.attackOption) {
                    player.getActionSender().sendPlayerOption("Attack", 2, 0);
                    player.attackOption = true;
                }
            }

            @Override
            public boolean canTeleport(Player player) {
                player.sendMessage("You cannot teleport from the Fight Pits.");
                return false;
            }

            @Override
            public void leave(Player player) {
                FightPits.fightPitsCheck(player);
                if(player.attackOption) {
                    player.getActionSender().sendPlayerOption("null", 2, 1);
                    player.attackOption = false;
                }
            }

            @Override
            public boolean canAttack(Player player, Player target) {
                return FightPits.inGame(player) && !FightPits.isSameTeam(player, target);
            }
        },
        FIGHT_PITS_WAIT_ROOM(new int[]{2393, 2404}, new int[]{5168, 5176}, false, false, false, false, false, false, Rank.PLAYER),
        DUEL_ARENA(new int[]{3332, 3358, 3333, 3357, 3334, 3356, 3335, 3355, 3336, 3354, 3337, 3353, 3338, 3352, 3339, 3351, 3363, 3389, 3364, 3388, 3365, 3387, 3366, 3386, 3367, 3385, 3368, 3384, 3369, 3383, 3370, 3382, 3332, 3358, 3333, 3357, 3334, 3356, 3335, 3355, 3336, 3354, 3337, 3353, 3338, 3352, 3339, 3351, 3363, 3389, 3364, 3388, 3365, 3387, 3366, 3386, 3367, 3385, 3368, 3384, 3369, 3383, 3370, 3382, 3332, 3358, 3333, 3357, 3334, 3356, 3335, 3355, 3336, 3354, 3337, 3353, 3338, 3352, 3339, 3351, 3363, 3389, 3364, 3388, 3365, 3387, 3366, 3386, 3367, 3385, 3368, 3384, 3369, 3383, 3370, 3382}, new int[]{3250, 3252, 3249, 3253, 3248, 3255, 3246, 3256, 3246, 3256, 3245, 3257, 3245, 3257, 3244, 3258, 3250, 3252, 3249, 3253, 3247, 3255, 3246, 3256, 3246, 3256, 3245, 3257, 3245, 3257, 3244, 3258, 3231, 3233, 3230, 3234, 3228, 3236, 3227, 3237, 3227, 3237, 3226, 3238, 3226, 3238, 3225, 3239, 3231, 3233, 3230, 3234, 3228, 3236, 3227, 3237, 3227, 3237, 3226, 3238, 3226, 3238, 3225, 3239, 3212, 3214, 3211, 3215, 3209, 3217, 3208, 3218, 3208, 3218, 3207, 3219, 3207, 3219, 3206, 3220, 3212, 3214, 3211, 3215, 3209, 3217, 3208, 3218, 3208, 3218, 3207, 3219, 3207, 3219, 3206, 3220}, false, false, false, false, false, false, Rank.PLAYER) {
            @Override
            public void enter(Player player) {
                if(player.duelAttackable <= 0) {
                    player.setTeleportTarget(Position.create(3360 + Combat.random(17), 3274 + Combat.random(3), 0), false);
                    return;
                }
                if(!player.attackOption) {
                    player.getActionSender().sendPlayerOption("Attack", 2, 0);
                    player.attackOption = true;
                }
            }

            @Override
            public void leave(Player player) {
                if(player.attackOption) {
                    player.getActionSender().sendPlayerOption("null", 2, 0);
                    player.attackOption = false;
                }
            }

            @Override
            public boolean canTeleport(Player player) {
                player.sendMessage("You cannot teleport while being in a duel!");
                return false;
            }

            @Override
            public boolean onDeath(Player player) {
                if (player.duelAttackable > 0) {
                    Duel.finishFullyDuel(player);
                    return true;
                }
                return false;
            }

            @Override
            public boolean canAttack(Player player, Player target) {
                if (player.duelAttackable > 0) {
                    if (target.getIndex() == player.duelAttackable) {
                        return true;
                    }
                }
                player.sendMessage("This is not your opponent!");
                return false;
            }
        },
        DUEL_ARENA_LOBBY(new int[]{3322, 3394, 3311, 3323, 3331, 3391}, new int[]{3195, 3291, 3223, 3248, 3242, 3260}, false, false, false, false, false, false, Rank.PLAYER) {

            @Override
            public void enter(Player player) {
                if(!player.duelOption) {
                    player.getActionSender().sendPlayerOption("Challenge", 5, 1);
                    if (player.getNpcState()) {
                        player.setPNpc(-1);
                    }
                    player.duelOption = true;
                }
            }

            @Override
            public void leave(Player player) {
                if(player.duelOption) {
                    if ((Rank.hasAbility(player, Rank.MODERATOR)))
                        player.getActionSender().sendPlayerOption("Moderate", 5, 0);
                    else
                        player.getActionSender().sendPlayerOption("null", 5, 0);
                    player.duelOption = false;
                }
            }
        },
        BARROWS(new int[] {3520, 3598, 3543, 3584, 3543, 3560}, new int[] {9653, 9750, 3265, 3314, 9685, 9702}, false, false, false, false, false, false, Rank.PLAYER),
        JAIL(new int[]{2090, 2105, 2105, 2108, 2106, 2106, 2095, 2100, 2087, 2090, 2086, 2088, 2087, 2090}, new int[]{4422, 4436, 4419, 4422, 4427, 4431, 4420, 4421, 4419, 4422, 4428, 4429, 4436, 4439}, false, false, false, false, false, false, Rank.PLAYER) {
            @Override
            public boolean canTeleport(Player player) {
                player.sendMessage("You cannot teleport out of jail.");
                return false;
            }
        },
        JAIL_FULL_AREA(new int[]{2065, 2111}, new int[]{4416, 4455}, false, false, false, false, true, true, Rank.HELPER),
        DEFAULT(null, null, false, true, true, true, true, true);

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
         * This is the default call when a player enters an area. This code
         * checks if they have the rank, and then pass the player on
         * to the area-specific code.
         * @param player The player
         */
        public final void enterArea(Player player) {
            if(!Rank.hasAbility(player, getMinimumRank())) {
                player.setTeleportTarget(Edgeville.POSITION);
                return;
            }
            //This is a temp block of code for TESTING
            player.sendMessage("Now entering: @dre@" + Misc.ucFirst(name()));
            enter(player);
        }

        /**
         * This determines what happens when the player enters an area.
         * This can be left empty, and only needs to be overwritten if it does something
         * @param player The player
         */
        public void enter(Player player) {}

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

        public boolean canTeleport(Player player) {
            return true;
        }

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
                gc.getLocation().enterArea(((Player)gc));
            }
        }
    }
}
