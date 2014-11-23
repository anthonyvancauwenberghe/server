package org.hyperion.rs2.model.content.bounty.rewards;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/23/14
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Emblem {
    TIER_1(1),
    TIER_2(1);



    private final int reward;
    private final int id;
    private Emblem(final int reward) {
        this.reward = reward;
        this.id = ordinal() + 13195;
    }
}
