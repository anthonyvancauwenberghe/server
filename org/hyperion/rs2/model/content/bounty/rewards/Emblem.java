package org.hyperion.rs2.model.content.bounty.rewards;

import static org.hyperion.rs2.model.content.bounty.BountyHunter.BASE_POINTS;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/23/14
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Emblem {
    TIER_1(BASE_POINTS),
    TIER_2(BASE_POINTS * 2),
    TIER_3(BASE_POINTS * 4),
    TIER_4(BASE_POINTS * 8),
    TIER_5(BASE_POINTS * 15),
    TIER_6(BASE_POINTS * 25),
    TIER_7(BASE_POINTS * 35),
    TIER_8(BASE_POINTS * 50),
    TIER_9(BASE_POINTS * 70),
    TIER_10(BASE_POINTS * 100);


    private static final int BASE_ID = 13195;

    private final int reward;
    private final int id;
    private Emblem(final int reward) {
        this.reward = reward;
        this.id = ordinal() + BASE_ID;
    }


}
