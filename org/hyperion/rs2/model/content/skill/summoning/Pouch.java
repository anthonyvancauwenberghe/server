package org.hyperion.rs2.model.content.skill.summoning;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * Created by Gilles on 8/03/2016.
 */
public enum Pouch {
    ;
    private final static int SHARD_ID = 18016;
    private final static int POUCH_ID = 12155;

    private final int shardsAmount, experience, levelRequirement;
    private final Item requiredItem, pouchItem;
    private final Charm requiredCharm;

    Pouch(int shardsAmount, int experience, int levelRequirement, Item pouchItem, Charm requiredCharm, Item requiredItem) {
        this.shardsAmount = shardsAmount;
        this.experience = experience;
        this.levelRequirement = levelRequirement;
        this.requiredItem = requiredItem;
        this.pouchItem = pouchItem;
        this.requiredCharm = requiredCharm;
    }

    public int getShardsAmount() {
        return shardsAmount;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public Item getRequiredItem() {
        return requiredItem;
    }

    public Item getPouchItem() {
        return pouchItem;
    }

    public Charm getRequiredCharm() {
        return requiredCharm;
    }

    public void createPouch(Player player) {
        if(player.getSkills().getLevel(Skills.SUMMONING) < getLevelRequirement()) {
            player.sendMessage("You need a Summoning level of " + getLevelRequirement() + " to create this pouch.");
            return;
        }

        if(player.getInventory().getCount(getRequiredCharm().getId()) >= getRequiredCharm().getCount()) {
            player.sendMessage("You need at least " + getRequiredCharm().getCount() + " " + getRequiredCharm().getDefinition().getProperName() + (getRequiredCharm().getCount() == 1 ? "" : "s") + " to create this pouch.");
            return;
        }

        if(player.getInventory().getCount(SHARD_ID) >= getShardsAmount()) {
            player.sendMessage("You need at least " + getShardsAmount() + " shards to create this pouch.");
            return;
        }

        if(player.getInventory().getCount(SHARD_ID) >= getShardsAmount()) {
            player.sendMessage("You need at least " + getShardsAmount() + " shards to create this pouch.");
            return;
        }

        if(player.getInventory().getCount(POUCH_ID) >= getShardsAmount()) {
            player.sendMessage("You need a pouch to create this pouch.");
            return;
        }

        if(player.getInventory().getCount(getRequiredItem().getId()) < getRequiredItem().getCount()) {
            player.sendMessage("You need " + getRequiredItem().getCount() + " " + getRequiredItem().getDefinition().getProperName() + (getRequiredItem().getCount() == 1 ? "" : "s") + " to make this pouch.");
            return;
        }

        if(player.getInventory().remove(getRequiredItem()) < getRequiredItem().getCount() || player.getInventory().remove(new Item(SHARD_ID, getShardsAmount())) < getShardsAmount() || player.getInventory().remove(new Item(POUCH_ID)) < 1 || player.getInventory().remove(getRequiredCharm()) < getRequiredCharm().getCount())
            return;

        if(!player.getInventory().hasRoomFor(getPouchItem())) {
            player.getInventory().add(getRequiredItem());
            player.getInventory().add(new Item(SHARD_ID, getShardsAmount()));
            player.getInventory().add(new Item(POUCH_ID));
            player.getInventory().add(getRequiredCharm());
            return;
        }

        player.getInventory().add(getPouchItem());
    }
}
