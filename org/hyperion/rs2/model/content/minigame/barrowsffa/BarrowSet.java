package org.hyperion.rs2.model.content.minigame.barrowsffa;


import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;
import static org.hyperion.rs2.model.content.minigame.barrowsffa.BarrowsFFA.DIALOGUE_ID;

import java.util.stream.Stream;

public enum BarrowSet {
    DHAROK(DIALOGUE_ID + 1, new Integer(4716), new Integer(4718), new Integer(4720), new Integer(4722)),
    KARIL(DIALOGUE_ID + 2, new Integer(4732), new Integer(4734), new Integer(4736), new Integer(4738)),
    AHRIM(DIALOGUE_ID + 3, new Integer(4714), new Integer(4712), new Integer(4710), new Integer(4708)),
    GUTHAN(DIALOGUE_ID + 5, new Integer(4724), new Integer(4726), new Integer(4728), new Integer(4730)),
    TORAGS(DIALOGUE_ID + 6, new Integer(4745), new Integer(4747), new Integer(4749), new Integer(4751)),
    VERACS(DIALOGUE_ID + 7, new Integer(4753), new Integer(4755), new Integer(4757), new Integer(4759));

    public static final BarrowSet[] SETS = values().clone();

    private final Item[] items;
    private final int dialogueId; //dialogue id for picking the set

    private BarrowSet(int dialogueAction ,final Integer... ids) {
        this.items = Stream.of(ids).map(Item::create).toArray(Item[]::new);
        this.dialogueId = dialogueAction;
    }

    public void equip(final Player player) {
        int i = 0;
        for(; i < 4; i++) {
            player.getEquipment().set(Equipment.getType(items[i]).getSlot(), Item.create(items[i].getId()));
        }
        for(; i < items.length; i++)
            player.getInventory().add(Item.create(items[i].getId()));
    }

    public static BarrowSet forDialogue(final int id) {
        for(final BarrowSet set : SETS)
            if(set.dialogueId == id)
                return set;
        return null;
    }


}