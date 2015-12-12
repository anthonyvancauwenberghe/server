package org.hyperion.rs2.model.content.minigame.poker;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 8/3/15
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PokerHolder {

    private int chips;

    public PokerHolder(final int chips) {
        this.chips = chips;
    }

    public void addChips(final int chips) {
        this.chips += chips;
    }

    public void removeChips(final int chips) {
        this.chips -= chips;
    }

    public int getChips() {
        return chips;
    }


}
