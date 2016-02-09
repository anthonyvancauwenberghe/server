package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.saving.SaveString;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/15/15
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveBonusXP extends SaveString {

    public SaveBonusXP() {
        super("bonusxp");
    }


    @Override
    public void setValue(Player player, String value) {
        try {
            player.getSkills().setBonusXP(Skills.CurrentBonusXP.load(value));
            player.getSkills().getBonusXP().ifPresent(bonus -> {
                    if(bonus.running())
                        player.sendf("You have @red@%s@bla@ remaining for your bonus in @red@%s", bonus.timeRemaining(), Skills.SKILL_NAME[bonus.getSkill()]);
            });
        }catch(final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getValue(Player player) {
        if(player.getSkills().getBonusXP().isPresent())
            return player.getSkills().getBonusXP().get().toString();
        return "";
    }
}
