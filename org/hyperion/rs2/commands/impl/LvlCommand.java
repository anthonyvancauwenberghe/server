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
	public boolean execute(Player player, String input) {
		input = filterInput(input);
		String[] parts = input.split(" ");
		try {
			int skillId = Integer.parseInt(parts[0]);
			int lvl = Integer.parseInt(parts[1]);
			if(lvl > 99 || skillId > 24)
				return false;
			player.getSkills().setLevel(skillId, lvl);
			player.getSkills().setExperience(skillId,
					player.getSkills().getXPForLevel(lvl) + 1);
			String message = Skills.SKILL_NAME[skillId] + " level is now " + lvl;
			player.getActionSender().sendMessage(message);
		} catch(Exception e) {
			player.getActionSender().sendMessage(
					"Syntax is ::lvl [skill] [lvl].");
		}
		return true;
	}

}
