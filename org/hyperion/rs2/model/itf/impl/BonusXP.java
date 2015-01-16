package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/14/15
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class BonusXP extends Interface {

    private static final int ID = 7;

    public BonusXP() {
        super(ID);
    }

    @Override
    public void handle(final Player player, final Packet pkt) {
        final int skill = pkt.getByte();

        if(skill > Skills.SKILL_COUNT)
            return;

        player.getSkills().getBonusXP().ifPresent(
                s -> player.sendf("Your current bonus skill of @red@%s@bla@ has ended", Skills.SKILL_NAME[skill]));

        player.getSkills().setBonusXP(new Skills.CurrentBonusXP(skill));

        player.sendf("You have just started your bonus skill of: @red@%s", Skills.SKILL_NAME[skill]);

    }


}
