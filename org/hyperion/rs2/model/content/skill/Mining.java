package org.hyperion.rs2.model.content.skill;

import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.ArrayUtils;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author DrHales
 */
public class Mining implements ContentTemplate {

    private final List<Integer> OBJECTS = Arrays.asList(2491, 2108, 2109, 2094, 2095, 14902, 2090, 2091, 14906, 2092, 2093, 14913, 2100, 2101, 14902, 2096, 2097, 14850, 2098, 2099, 2102, 2103, 14853, 2104, 2105, 14862, 14859, 14860, 1755, 2112, 2113);

    @Override
    public void init() throws FileNotFoundException {
    }

    @Override
    public int[] getValues(int value) {
        return (value == 6 || value == 7) ? ArrayUtils.fromList(OBJECTS) : null;
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int id, final int x, final int y, int d) {
        return handleMiningObjects(player, id) || (type == 6 && mine(player, id, x, y)) || (type == 7 && prospect(player, id, x, y));
    }

    private boolean mine(final Player player, final int id, final int x, final int y) {
        if (id == 450) {
            player.sendMessage("This rock contains no ore.");
            return false;
        }
        final Rock rock = Rock.getRock(id);
        if (player.isBusy()) {
            return true;
        }
        if (rock != null) {
            final Pickaxe pickaxe = Pickaxe.getPickaxe(player);
            if (pickaxe == null) {
                player.sendMessage("You do not have a pickaxe you can use.");
                return false;
            }
            if (player.getSkills().getLevel(Skills.MINING) < rock.getLevel()) {
                player.sendf("You need a mining level of %d to mine this rock.", rock.getLevel());
                return false;
            }
            if (player.getInventory().freeSlots() < 1) {
                player.sendMessage("There is not enough space in your inventory.");
                return false;
            }
            player.setBusy(true);
            player.cE.face(x, y);
            player.playAnimation(Animation.create(pickaxe.getAnimation()));
            final int cycles = getCycles(player, pickaxe, rock) < 1 ? 1 : getCycles(player, pickaxe, rock);
            TaskManager.submit(new Task(600L, "Mining Rocks Task") {
                int cycle = 0;

                @Override
                public void execute() {
                    if (!player.isBusy()) {
                        stop();
                        return;
                    }
                    if (player.getInventory().freeSlots() < 1) {
                        player.playAnimation(Animation.create(65535));
                        player.sendMessage("You do not have any free inventory space left.");
                        stop();
                        player.setBusy(false);
                        return;
                    }
                    if (cycle < cycles) {
                        cycle++;
                        player.playAnimation(Animation.create(pickaxe.getAnimation()));
                    } else if (cycle == cycles) {
                        player.getInventory().add(Item.create(rock.getItem()));
                        player.getAchievementTracker().itemSkilled(Skills.MINING, rock.getItem(), 1);
                        player.getSkills().addExperience(Skills.MINING, rock.getExperience());
                        player.sendMessage("You get some ore.");
                        cycle = 0;
                        if (rock.getRespawn() > 0) {
                            final GameObject expired = new GameObject(GameObjectDefinition.forId(450), Position.create(x, y, player.getPosition().getZ()), 10, 0);
                            ObjectManager.addObject(expired);
                            player.playAnimation(Animation.create(65535));
                            stop();
                            player.setBusy(false);
                            TaskManager.submit(new Task(rock.getRespawn(), String.format("%s Ore Respawn Task", TextUtils.titleCase(String.valueOf(rock)))) {
                                @Override
                                public void execute() {
                                    ObjectManager.replace(expired, new GameObject(GameObjectDefinition.forId(id), Position.create(x, y, player.getPosition().getZ()), 10, 0));
                                    stop();
                                }
                            });
                        } else {
                            player.playAnimation(Animation.create(65535));
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    private int getCycles(final Player player, final Pickaxe pickaxe, final Rock rock) {
        return Misc.inclusiveRandom((int) (rock.getTicks() - (player.getSkills().getLevel(Skills.HUNTER) * 0.03) + pickaxe.getSpeed()), rock.getTicks());
    }

    private boolean prospect(final Player player, final int id, final int x, final int y) {
        if (id == 450) {
            player.sendMessage("This rock contains no ore.");
            return false;
        }
        final Rock rock = Rock.getRock(id);
        if (rock != null) {
            player.cE.face(x, y);
            player.sendMessage("You examine the rock for ores...");
            TaskManager.submit(new Task(1000L, "Mining Prospect Task") {
                @Override
                public void execute() {
                    player.sendf("This rock contains %s ore.", TextUtils.titleCase(String.valueOf(rock).replace("_", " ")));
                    stop();
                }
            });
            return true;
        }
        return false;
    }

    private boolean handleMiningObjects(final Player player, final int value) {
        if (value == 1755) {
            player.setTeleportTarget(Position.create(player.getPosition().getX(), player.getPosition().getY() - 6400, 0));
            return true;
        } else if (value == 2112 || value == 2113) {
            if (player.getSkills().getLevel(Skills.MINING) < 60) {
                player.sendMessage("You need 60 mining to enter the Mining Guild.");
                return false;
            }
            return true;
        }
        return false;
    }

    private enum Pickaxe {
        BRONZE(1265, 1, 625, 1.0),
        IRON(1267, 1, 626, 1.05),
        STEEL(1269, 6, 627, 1.1),
        MITHRIL(1273, 21, 628, 1.2),
        ADAMANT(1271, 31, 629, 1.25),
        RUNITE(1275, 41, 624, 1.3),
        DRAGON(15259, 61, 12188, 1.50),
        ADZE(13661, 80, 10226, 1.60);

        private final static Pickaxe[] VALUES = values();
        private final static List<Pickaxe> ORDINAL = Stream.of(VALUES).sorted((one, two) -> Integer.compare(one.ordinal(), two.ordinal())).collect(Collectors.toCollection(LinkedList::new));
        private final static Map<Integer, Pickaxe> MAPPED = Stream.of(VALUES).collect(Collectors.toMap(Pickaxe::getId, Function.identity()));
        private final int id, level, animation;
        private final double speed;

        Pickaxe(int id, int level, int animation, double speed) {
            this.id = id;
            this.level = level;
            this.animation = animation;
            this.speed = speed;
        }

        public static Pickaxe getPickaxe(final Player player) {
            Item item = player.getEquipment().get(3);
            if (item != null && MAPPED.containsKey(item.getId())) {
                Pickaxe pickaxe = MAPPED.get(item.getId());
                if (pickaxe.usable(player))
                    return pickaxe;
            }
            for (Pickaxe array : ORDINAL) {
                if (player.getInventory().contains(array.getId()) && array.usable(player))
                    return array;
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public int getAnimation() {
            return animation;
        }

        public double getSpeed() {
            return speed;
        }

        private boolean usable(final Player player) {
            return player.getSkills().getLevel(Skills.MINING) >= getLevel();
        }
    }

    private enum Rock {
        RUNE_ESSENCE(Collections.singletonList(2491), 1, 5, 1436, 3, -1),
        CLAY(Arrays.asList(2108, 2109), 1, 5, 434, 5, 2000),
        TIN(Arrays.asList(2094, 2095, 14902), 1, 17, 438, 6, 4000),
        COPPER(Arrays.asList(2090, 2091, 14906), 1, 17, 436, 6, 4000),
        IRON(Arrays.asList(2092, 2093, 14913), 15, 35, 440, 7, 5000),
        SILVER(Arrays.asList(2100, 2101, 14902), 20, 40, 442, 7, 7000),
        COAL(Arrays.asList(2096, 2097, 14850), 30, 50, 453, 7, 7000),
        GOLD(Arrays.asList(2098, 2099), 40, 65, 444, 7, 10000),
        MITHRIL(Arrays.asList(2102, 2103, 14853), 50, 80, 447, 8, 11000),
        ADAMANTITE(Arrays.asList(2104, 2105, 14862), 70, 95, 449, 9, 14000),
        RUNITE(Arrays.asList(14859, 14860), 85, 125, 451, 9, 45000);

        private final List<Integer> rocks;
        private final int level, experience, item, ticks;
        private final long respawn;

        Rock(List<Integer> rocks, int level, int experience, int item, int ticks, long respawn) {
            this.rocks = rocks;
            this.level = level;
            this.experience = experience;
            this.item = item;
            this.ticks = ticks;
            this.respawn = respawn;
        }

        public static Rock getRock(final int value) {
            return RUNE_ESSENCE.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? RUNE_ESSENCE : CLAY.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? CLAY : TIN.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? TIN : COPPER.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? COPPER : IRON.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? IRON : SILVER.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? SILVER : COAL.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? COAL : GOLD.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? GOLD : MITHRIL.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? MITHRIL : ADAMANTITE.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? ADAMANTITE : RUNITE.getRocks().stream().anyMatch((array) -> array.equals(value))
                    ? RUNITE : null;
        }

        public List<Integer> getRocks() {
            return rocks;
        }

        public int getLevel() {
            return level;
        }

        public int getExperience() {
            return experience;
        }

        public int getItem() {
            return item;
        }

        public int getTicks() {
            return ticks;
        }

        public long getRespawn() {
            return respawn;
        }
    }
}
