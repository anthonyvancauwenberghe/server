package org.hyperion.rs2.model.content.skill;

import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.ArrayUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author DrHales
 */
public class Hunter implements ContentTemplate {

    public static final List<Integer> NPC_IDS = Arrays.asList(1028, 6055, 1029, 6056, 1030, 6057, 1031, 6058, 1032, 6059, 1033, 6060, 1034, 6061, 1035, 6062, 6053, 6063, 7845, 7846, 6054, 6064, 7903, 7906, 5085, 5084, 5083, 5082);
    private static final List<Integer> JAR_IDS = Arrays.asList(11238, 11240, 11242, 11244, 11246, 11248, 11250, 11252, 13337, 11254, 11256, 15517);
    private static final List<Integer> BUTTERFLY_JARS = Arrays.asList(10020, 10018, 10016, 10014);

    private Impling getImpling(final int value) {
        return Impling.BABY.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.BABY : Impling.YOUNG.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.YOUNG : Impling.GOURMET.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.GOURMET : Impling.EARTH.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.EARTH : Impling.ESSENCE.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.ESSENCE : Impling.ELECTRIC.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.ELECTRIC : Impling.NATURE.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.NATURE : Impling.MAGPIE.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.MAGPIE : Impling.NINJA.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.NINJA : Impling.PIRATE.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.PIRATE : Impling.DRAGON.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.DRAGON : Impling.KINGLY.getImps().stream().anyMatch((array) -> array.equals(value))
                ? Impling.KINGLY : null;
    }

    private Butterfly getButterfly(final int value) {
        return Butterfly.RUBY_HARVEST.getId() == value
                ? Butterfly.RUBY_HARVEST : Butterfly.SAPPHIRE_GLACIALIS.getId() == value
                ? Butterfly.SAPPHIRE_GLACIALIS : Butterfly.SNOWY_KNIGHT.getId() == value
                ? Butterfly.SNOWY_KNIGHT : Butterfly.BLACK_WARLOCK.getId() == value
                ? Butterfly.BLACK_WARLOCK : null;
    }

    private Net getNet(final int value) {
        return Net.REGULAR.getId() == value
                ? Net.REGULAR : Net.MAGIC.getId() == value
                ? Net.MAGIC : Net.VOLATILE.getId() == value
                ? Net.VOLATILE : Net.SACRED.getId() == value
                ? Net.SACRED : null;
    }

    private Jar getJar(final int value) {
        return Jar.LOW.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.LOW : Jar.MEDIUM.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.MEDIUM : Jar.HIGH.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.HIGH : Jar.DRAGON.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.DRAGON : Jar.KINGLY.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.KINGLY : Jar.RUBY_HARVEST.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.RUBY_HARVEST : Jar.SAPPHIRE_GLACIALIS.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.SAPPHIRE_GLACIALIS : Jar.SNOWY_KNIGHT.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.SNOWY_KNIGHT : Jar.BLACK_WARLOCK.getJars().stream().anyMatch(array -> array.equals(value))
                ? Jar.BLACK_WARLOCK : null;
    }

    private void process(final Player player, final int id, final int x, final int y) {
        if ((System.currentTimeMillis() - player.contentTimer) > 1499) {
            final Impling imp = getImpling(id);
            final Net net = player.getEquipment().get(Equipment.SLOT_WEAPON) != null ? getNet(player.getEquipment().get(Equipment.SLOT_WEAPON).getId()) : null;
            if (imp != null) {
                catchImp(player, imp, net, id, x, y);
            } else {
                final Butterfly butterfly = getButterfly(id);
                if (butterfly != null) {
                    catchButterfly(player, butterfly, net, id, x, y);
                }
            }
        }
    }

    public void loot(final Player player, final int id) {
        final Item item = Item.create(id);
        if (player.getInventory().hasItem(item)) {
            final Jar jar = getJar(id);
            if (jar != null) {
                if (player.getInventory().freeSlots() < 1) {
                    player.sendMessage("You need at least 1 inventory slot to loot jars!");
                    return;
                }
                player.getInventory().remove(item);
                final int reward = jar.getRewards().get(new Random().nextInt(jar.getRewards().size()));
                player.getInventory().add(Item.create(reward, jar.getAmount(reward)));
            }
        }
    }

    private void catchImp(final Player player, final Impling imp, final Net net, final int id, final int x, final int y) {
        player.getSkills().stopSkilling();
        if (player.getSkills().getLevel(Skills.HUNTER) < imp.getLevel()) {
            player.sendf("You need a hunter level of %d to catch this impling.", imp.getLevel());
            return;
        }
        if (net == null && player.getSkills().getLevel(Skills.HUNTER) < (imp.getLevel() + 10)) {
            player.sendMessage("You need a net to catch this imp!");
            return;
        }
        if (player.getInventory().freeSlots() < 1) {
            player.sendMessage("You need some free inventory slots to catch imps!");
            return;
        }
        player.contentTimer = System.currentTimeMillis();
        ContentEntity.startAnimation(player, 5209);
        player.face(Position.create(x, y, 0));
        player.setCurrentTask(new Task(500L, "Impling Catching Task") {
            @Override
            public void execute() {
                if (HunterNpcs.remove(id, x, y)) {
                    final int count = player.getExtraData().getInt("impscaught") + 1;
                    player.getExtraData().put("impscaught", count);
                    player.getInventory().add(Item.create(imp.getItem()));
                    player.getAchievementTracker().itemSkilled(Skills.HUNTER, imp.getItem(), 1);
                    player.sendMessage("You catch the impling!");
                    player.getSkills().addExperience(Skills.HUNTER, getBonus(imp.getExperience(), net));
                    player.sendf("You have now caught @red@%,d@bla@ impling%s.", count, count > 1 ? "s" : "");
                    HunterNpcs.spawn(HunterNpcs.Spawn.getRandomLocation());
                }
                stop();
            }
        });
        TaskManager.submit(player.getCurrentTask());
    }

    private void catchButterfly(final Player player, final Butterfly butterfly, final Net net, final int id, final int x, final int y) {
        if (player.getSkills().getLevel(Skills.HUNTER) < butterfly.getLevel()) {
            player.sendf("You need a hunter level of %d to catch this butterfly.", butterfly.getLevel());
            return;
        }
        if (net == null) {
            player.sendMessage("You need a net to catch this butterfly!");
            return;
        }
        if (player.getInventory().freeSlots() < 1) {
            player.sendMessage("You need some free inventory slots to catch butterflies!");
            return;
        }
        player.contentTimer = System.currentTimeMillis();
        ContentEntity.startAnimation(player, 6999);
        player.face(Position.create(x, y, 0));
        player.setCurrentTask(new Task(500L, "Butterfly Catching Task") {
            @Override
            public void execute() {
                if (HunterNpcs.remove(id, x, y)) {
                    player.getInventory().add(Item.create(butterfly.getItem()));
                    player.sendMessage("You catch the butterfly!");
                    player.getSkills().addExperience(Skills.HUNTER, getBonus(butterfly.getExperience(), net));
                    HunterNpcs.spawn(HunterNpcs.Spawn.getRandomLocation());
                }
                stop();
            }
        });
        TaskManager.submit(player.getCurrentTask());
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int id, final int x, final int y, int d) {
        if (type == 10) {
            process(player, id, x, y);
        } else if (type == 17 || type == 22) {
            loot(player, id);
        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        HunterNpcs.startup();
    }

    @Override
    public int[] getValues(final int value) {
        return value == 10 ? ArrayUtils.fromList(NPC_IDS) : value == 22 ? ArrayUtils.fromList(JAR_IDS) : value == 17 ? ArrayUtils.fromList(BUTTERFLY_JARS) : null;
    }

    private int getBonus(final int value, final Net net) {
        return net != null ? net.getBonus() > 0 ? value + (value / net.getBonus()) : value : value;
    }

    private enum Net {
        REGULAR(10010, 0),
        MAGIC(11259, 0),
        VOLATILE(14102, 20),
        SACRED(14110, 10);

        final int id, bonus;

        Net(int id, int bonus) {
            this.id = id;
            this.bonus = bonus;
        }

        public int getId() {
            return id;
        }

        public int getBonus() {
            return bonus;
        }
    }

    private enum Butterfly {
        RUBY_HARVEST(5085, 15, 10020, 1250),
        SAPPHIRE_GLACIALIS(5084, 25, 10018, 1750),
        SNOWY_KNIGHT(5083, 35, 10016, 2450),
        BLACK_WARLOCK(5082, 45, 10014, 7500);

        private final int id, level, item, experience;

        Butterfly(int id, int level, int item, int experience) {
            this.id = id;
            this.level = level;
            this.item = item;
            this.experience = experience;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public int getItem() {
            return item;
        }

        public int getExperience() {
            return experience;
        }

    }

    private enum Impling {
        BABY(Arrays.asList(1028, 6055), 1, 11238, 1000),
        YOUNG(Arrays.asList(1029, 6056), 22, 11240, 1500),
        GOURMET(Arrays.asList(1030, 6057), 28, 11242, 2100),
        EARTH(Arrays.asList(1031, 6058), 36, 11244, 2500),
        ESSENCE(Arrays.asList(1032, 6059), 42, 11246, 3600),
        ELECTRIC(Arrays.asList(1033, 6060), 50, 11248, 7503),
        NATURE(Arrays.asList(1034, 6061), 58, 11250, 15500),
        MAGPIE(Arrays.asList(1035, 6062), 65, 11252, 26000),
        NINJA(Arrays.asList(6053, 6063), 74, 11254, 56000),
        PIRATE(Arrays.asList(7845, 7846), 76, 13337, 61000),
        DRAGON(Arrays.asList(6054, 6064), 83, 11256, 85400),
        KINGLY(Arrays.asList(7903, 7906), 91, 15517, 104000);

        private final List<Integer> imps;
        private final int level, item, experience;

        Impling(List<Integer> imps, int level, int item, int experience) {
            this.imps = imps;
            this.level = level;
            this.item = item;
            this.experience = experience;
        }

        public List<Integer> getImps() {
            return imps;
        }

        public int getLevel() {
            return level;
        }

        public int getItem() {
            return item;
        }

        public int getExperience() {
            return experience;
        }
    }

    private enum Jar {
        LOW(Arrays.asList(11238, 11240, 11242), Arrays.asList(995, 1079, 1093, 1113, 1275, 4131, 2491, 2497, 2503, 1333, 1319, 450)) {
            @Override
            public int getAmount(final int value) {
                return value == 995 ? 50000 : value == 450 ? 50 : 1;
            }
        },
        MEDIUM(Arrays.asList(11244, 11246, 11248, 11250), Arrays.asList(995, 9185, 560, 565, 561, 811, 1079, 1093, 1113, 1275, 4131, 4089, 4091, 4095, 4093, 4097)) {
            @Override
            public int getAmount(final int value) {
                return value == 995 ? 200000 : (value == 560 || value == 565 || value == 561) ? 1000 : value == 811 ? 50 : 1;
            }
        },
        HIGH(Arrays.asList(11252, 13337, 11254), Arrays.asList(4151, 4153, 995, 6524, 6329, 1231, 892, 1079, 1093, 3385, 3387, 3389, 3391, 868, 4225)) {
            @Override
            public int getAmount(final int value) {
                return value == 995 ? 500000 : value == 6329 ? 3 : value == 892 ? 100 : value == 868 ? 50 : 1;
            }
        },
        DRAGON(Collections.singletonList(11256), Arrays.asList(537, 535, 4087, 4585, 9244, 9144, 11212, 1713, 11732, 9245, 995, 3140, 10564, 4214)) {
            @Override
            public int getAmount(final int value) {
                return value == 537 ? 15 : (value == 535 || value == 9244) ? 50 : value == 9144 ? 100 : (value == 11212 || value == 9245) ? 10 : value == 1713 ? 5 : value == 995 ? 2500000 : 1;
            }
        },
        KINGLY(Collections.singletonList(15517), Arrays.asList(15509, 15503, 15505, 15507, 15511, 7158, 2364, 995, 3140, 9245, 6738, 1215, 11235, 892)) {
            @Override
            public int getAmount(final int value) {
                return value == 2364 ? 100 : value == 995 ? 10000000 : value == 9245 ? 50 : value == 6738 ? 2 : value == 892 ? 500 : 1;
            }
        },
        RUBY_HARVEST(Collections.singletonList(10020), Collections.singletonList(995)) {
            @Override
            public int getAmount(final int value) {
                return 75000;
            }
        },
        SAPPHIRE_GLACIALIS(Collections.singletonList(10018), Collections.singletonList(995)) {
            @Override
            public int getAmount(final int value) {
                return 100000;
            }
        },
        SNOWY_KNIGHT(Collections.singletonList(10016), Collections.singletonList(995)) {
            @Override
            public int getAmount(final int value) {
                return 150000;
            }
        },
        BLACK_WARLOCK(Collections.singletonList(10014), Collections.singletonList(995)) {
            @Override
            public int getAmount(final int value) {
                return 200000;
            }
        };

        private final List<Integer> jars, rewards;

        Jar(List<Integer> jars, List<Integer> rewards) {
            this.jars = jars;
            this.rewards = rewards;
        }

        public List<Integer> getJars() {
            return jars;
        }

        public List<Integer> getRewards() {
            return rewards;
        }

        public abstract int getAmount(int value);
    }

}