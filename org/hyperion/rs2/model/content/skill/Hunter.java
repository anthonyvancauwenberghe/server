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
import java.util.List;


/**
 * @author DrHales
 */
public class Hunter implements ContentTemplate {

    public static final List<Integer> IMP_IDS = Arrays.asList(1028, 6055, 1029, 6056, 1030, 6057, 1031, 6058, 1032, 6059, 1033, 6060, 1034, 6061, 1035, 6062, 6053, 6063, 7845, 7846, 6054, 6064, 7903, 7906);

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

    private boolean hasEquipment(final Player player) {
        return ((player.getEquipment().get(Equipment.SLOT_WEAPON) != null && (player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11259
                || player.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 10010)));
    }

    private void process(final Player player, final int id, final int x, final int y) {
        if ((System.currentTimeMillis() - player.contentTimer) > 1499) {
            final Impling npc = getImpling(id);
            if (npc != null) {
                if (player.getSkills().getLevel(Skills.HUNTER) < npc.getLevel()) {
                    player.sendf("You need a hunter level of %d to catch this impling.", npc.getLevel());
                    return;
                }
                if (!hasEquipment(player) && player.getSkills().getLevel(Skills.HUNTER) < (npc.getLevel() + 10)) {
                    player.sendMessage("You need a net to catch this imp!");
                    return;
                }
                if (player.getInventory().freeSlots() < 1) {
                    player.sendMessage("You need some free inventory slots to catch imps!");
                    return;
                }
                player.contentTimer = System.currentTimeMillis();
                ContentEntity.startAnimation(player, 5209);
                player.cE.face(x, y);
                TaskManager.submit(new Task(500L, "Impling Catching Task") {
                    @Override
                    public void execute() {
                        if (HunterNpcs.remove(id, x, y)) {
                            final int count = player.getExtraData().getInt("impscaught") + 1;
                            player.getExtraData().put("impscaught", count);
                            player.getInventory().add(Item.create(npc.getItem()));
                            player.sendMessage("You catch the impling!");
                            player.getAchievementTracker().itemSkilled(Skills.HUNTER, npc.getItem(), 1);
                            player.getSkills().addExperience(Skills.HUNTER, npc.getExperience());
                            player.sendf("You have now caught @red@%,d@bla@ impling%s.", count, count > 1 ? "s" : "");
                            HunterNpcs.spawn();
                        }
                        stop();
                    }
                });
            }
        }
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int id, final int x, final int y, int d) {
        if (type == 10) {
            process(player, id, x, y);
        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        HunterNpcs.startup();
    }

    @Override
    public int[] getValues(final int value) {
        return value != 10 ? null : ArrayUtils.fromList(IMP_IDS);
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
        private final int level;
        private final int item;
        private final int experience;

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

}