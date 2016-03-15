package org.hyperion.rs2.model.content.skill.summoning;

import org.hyperion.rs2.model.Item;

/**
 * Created by Gilles on 8/03/2016.
 */
public class Charm extends Item {
    public enum CharmType {
        GOLD(12158),
        GREEN(12159),
        CRIMSON(12160),
        BLUE(12163);

        private final int itemId;

        CharmType(int itemId) {
            this.itemId = itemId;
        }

        public int getItemId() {
            return itemId;
        }
    }

    private final CharmType charmType;

    public Charm(CharmType charmType, int amount) {
        super(charmType.getItemId(), amount);
        this.charmType = charmType;
    }

    public CharmType getCharmType() {
        return charmType;
    }
}
