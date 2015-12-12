package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;

public class SkillCommand extends Command {

    public SkillCommand() {
        super("skill", Rank.OWNER);
    }

    @Override
    public boolean execute(final Player player, String input) {
        input = filterInput(input);
        final String[] parts = input.split(" ");
        try{
            final int skillId = Integer.parseInt(parts[0]);
            final int lvl = Integer.parseInt(parts[1]);
            if(lvl > 20)
                return false;
            player.getSkills().setLevel(skillId, lvl);
            final String message = Skills.SKILL_NAME[skillId] + " level is temp boosted to " + lvl;
            player.getActionSender().sendMessage(message);
        }catch(final Exception exception1){
            player.getActionSender().sendMessage("Syntax is ::skill [skill] [lvl].");
        }
        return true;
    }

}
