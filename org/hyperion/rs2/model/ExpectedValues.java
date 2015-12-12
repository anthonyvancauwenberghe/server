package org.hyperion.rs2.model;

import org.hyperion.rs2.util.AccountValue;

/**
 * Created by Gilles on 20/10/2015.
 */
public class ExpectedValues {
    private final Player player;
    private int deltaTrade = 0, deltaStake = 0, deltaDrop = 0, deltaPickup = 0, deltaGamble = 0, deltaOther = 0;

    public ExpectedValues(final Player player) {
        this.player = player;
    }

    private static int getValue(final Item item) {
        return AccountValue.getItemValue(item);
    }

    public int getExpectedValue() {
        return player.getStartValue() + deltaTrade + deltaStake + deltaDrop + deltaPickup + deltaGamble + deltaOther;
    }

    public void removeItemFromInventory(final String reason, final Item item) {
        if(reason.equalsIgnoreCase("Gambling")){
            changeDeltaGamble(-getValue(item));
            return;
        }else if(reason.equalsIgnoreCase("Trade")){
            changeDeltaTrade(-getValue(item));
            return;
        }else if(reason.equalsIgnoreCase("Stake")){
            changeDeltaStake(-getValue(item));
            return;
        }
        changeDeltaOther(reason, -getValue(item));
    }

    public void addItemtoInventory(final String reason, final Item item) {
        if(reason.equalsIgnoreCase("Gambling")){
            changeDeltaGamble(getValue(item));
            return;
        }else if(reason.equalsIgnoreCase("Trade")){
            changeDeltaTrade(getValue(item));
            return;
        }else if(reason.equalsIgnoreCase("Stake")){
            changeDeltaStake(getValue(item));
            return;
        }
        changeDeltaOther(reason, getValue(item));
    }

    public void buyFromStore(final Item item) {
        changeDeltaOther("Bought " + item.getCount() + " " + item.getDefinition().getProperName() + " from store", getValue(item));
    }

    public void sellToStore(final Item item) {
        changeDeltaOther("Sold " + item.getCount() + " " + item.getDefinition().getProperName() + " to store", -getValue(item));
    }

    public void pickupItem(final Item item) {
        changeDeltaPickup(getValue(item));
    }

    public void deathDrop(final Item... items) {
        for(final Item item : items)
            dropItem(item);
    }

    public void trade(final Item[] itemsToAdd, final Item[] itemsToRemove) {
        for(final Item item : itemsToAdd)
            if(item != null)
                addItemtoInventory("Trade", item);
        for(final Item item : itemsToRemove)
            if(item != null)
                removeItemFromInventory("Trade", item);
    }

    public void stake(final Item[] items, final boolean won) {
        for(final Item item : items)
            if(item != null)
                if(won)
                    addItemtoInventory("Stake", item);
                else
                    removeItemFromInventory("Stake", item);

    }

    public void stakeItem(final Item item) {
        player.getExpectedValues().changeDeltaStake(getValue(item));
    }

    public void dropItem(final Item item) {
        player.getExpectedValues().changeDeltaDrop(-getValue(item));
    }

    public void changeDeltaTrade(final int change) {
        player.debugMessage("Changed trade value for player " + player.getSafeDisplayName() + " from '" + deltaTrade + "' to '" + (deltaTrade + change) + "'");
        deltaTrade += change;
    }

    public void changeDeltaStake(final int change) {
        player.debugMessage("Changed stake value for player " + player.getSafeDisplayName() + " from '" + deltaStake + "' to '" + (deltaStake + change) + "'");
        deltaStake += change;
    }

    public void changeDeltaDrop(final int change) {
        player.debugMessage("Changed drop value for player " + player.getSafeDisplayName() + " from '" + deltaDrop + "' to '" + (deltaDrop + change) + "'");
        deltaDrop += change;
    }

    public void changeDeltaPickup(final int change) {
        player.debugMessage("Changed pickup value for player " + player.getSafeDisplayName() + " from '" + deltaPickup + "' to '" + (deltaPickup + change) + "'");
        deltaPickup += change;
    }

    public void changeDeltaGamble(final int change) {
        player.debugMessage("Changed gamble value for player " + player.getSafeDisplayName() + " from '" + deltaGamble + "' to '" + (deltaGamble + change) + "'");
        deltaGamble += change;
    }

    public void changeDeltaOther(final String reason, final int change) {
        player.debugMessage("Changed value for player " + player.getSafeDisplayName() + " from '" + deltaOther + "' to '" + (deltaOther + change) + "' for reason '" + reason + "'.");
        deltaOther += change;
    }
}
