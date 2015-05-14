package org.hyperion.rs2.commands.impl;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.skill.Prayer;

/**
 * @author Arsen Maxyutov.
 */
public class SkillSetCommand extends Command {

	/**
	 * Holds all the command names with their row number being equal
	 * to their skill id.
	 */
	private static final String[][] COMMAND_NAMES = {
			{"atk", "attk", "attack"},
			{"def", "defense", "defence"},
			{"str", "strength"},
			{"hp", "hitp", "hitpoints"},
			{"range", "ranging", "ranged"},
			{"pray", "prayer"},
			{"mage", "magic"}
	};

	/**
	 * Constructor
	 *
	 * @param startsWith
	 * @param rights
	 * @param skill
	 */
	private SkillSetCommand(String startsWith, int skill, Rank... rights) {
		super(startsWith, rights);
		this.skill = skill;
	}

	/**
	 * Holds the skill id that should be modified with the command.
	 */
	private int skill = 0;

	/**
	 * The actual skill changing.
	 */
	@Override
	public boolean execute(Player player, String input) {
		if(player.getLocation().cannotMax())
			return false;
        if(!ItemSpawning.canSpawn(player))
            return false;
		if(! Server.SPAWN)
			return false;
		if(! canChangeLevel(player))
			return false;
		input = filterInput(input);
		if(input.length() > 2)
			return false;
		try {
			int level = Integer.parseInt(input);
			if(level > 99 || level < 1) {
				player.getActionSender().sendMessage("Please enter a skill level from 1 to 99");
				return false;
			}
			if(skill == 5 || skill == 1)
				player.resetPrayers();
			player.getSkills().setLevel(skill, level);
			player.getSkills().setExperience(skill, player.getSkills().getXPForLevel(level));
		} catch(Exception e) {
			player.getActionSender().sendMessage("Please enter a skill level from 1 to 99");
		}
		return true;

	}

	/**
	 * Checks if the command can be used.
	 *
	 * @param player
	 * @return
	 */
	public static boolean canChangeLevel(Player player) {
		if(player.getLocation().inPvPArea()) {
			player.getActionSender().sendMessage(
					"You cannot use this command in PvP zones.");
			return false;
		} else if(player.duelAttackable > 0) {
			player.getActionSender().sendMessage(
					"You cannot do that in the duel arena.");
			return false;
		}
		if(FightPits.inGame(player)) {
			return false;
		}
		if(player.getEquipment().size() > 0) {
			player.getActionSender().sendMessage("Please take all your armor off before using this command!");
			return false;
		}
		return true;
	}

	/**
	 * Call this method to load all commands which change skills.
	 */
	public static void init() {
		for(int i = 0; i < COMMAND_NAMES.length; i++) {
			for(String name : COMMAND_NAMES[i]) {
				CommandHandler.submit(new SkillSetCommand(name, i, Rank.PLAYER));
			}
		}
	}
}
