package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;

public class LvlCommand extends Command {

    public LvlCommand() {
        super("lvl", Rank.OWNER);
    }

    @Override
    public boolean execute(final Player player, String input) {
        input = filterInput(input);
        final String[] parts = input.split(" ");
        try{
            final int skillId = Integer.parseInt(parts[0]);
            final int lvl = Integer.parseInt(parts[1]);
            if(lvl > 99 || skillId > 24)
                return false;
            player.getSkills().setLevel(skillId, lvl);
            player.getSkills().setExperience(skillId, player.getSkills().getXPForLevel(lvl) + 1);
            final String message = Skills.SKILL_NAME[skillId] + " level is now " + lvl;
            player.getActionSender().sendMessage(message);
        }catch(final Exception e){
            player.getActionSender().sendMessage("Syntax is ::lvl [skill] [lvl].");
        }
        return true;
    }

}
