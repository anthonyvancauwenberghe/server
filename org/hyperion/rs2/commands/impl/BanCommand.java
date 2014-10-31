package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLUtils;
import org.hyperion.util.Time;

/**
 * @author Arsen Maxyutov.
 */
public class BanCommand extends Command {

	/**
	 * The type of ban
	 * e.g. MUTE
	 */
	private int type;

	/**
	 * The 'ban by ip' flag.
	 */
	private boolean byIp;

	/**
	 * Constructs a new ban command.
	 *
	 * @param startsWith the name of the command
	 * @param type       the type of ban, e.g. MUTE
	 * @param byIp       whether the ban is by ip.
	 */
	public BanCommand(String startsWith, int type, boolean byIp, Rank... ranks) {
		super(startsWith, ranks);
		this.type = type;
		this.byIp = byIp;
	}

	/**
	 * Constructs a new ban command.
	 *
	 * @param startsWith the name of the command
	 * @param type       the type of ban, e.g. MUTE
	 * @param byIp       whether the ban is by ip.
	 */
	public BanCommand(String startsWith, int type, boolean byIp) {
		super(startsWith, Rank.FORUM_MODERATOR);
		this.type = type;
		this.byIp = byIp;
	}


	/**
	 * Bans the scum if the scum is online.
	 */
	@Override
	public boolean execute(Player player, String input) {
		try {
			input = filterInput(input);
			String[] parts = input.split(",");
			String modName = player.getName().toLowerCase();
			String scumName = parts[0];
			if(scumName.length() > Player.MAX_NAME_LENGTH)
				throw new Exception("Invalid name");
			Player scum = World.getWorld().getPlayer(scumName);
			String duration = parts[1];
			String reason = parts[2];
			if(reason.length() < 1)
				throw new Exception("Reason is too short");
			reason = SQLUtils.checkInput(reason);
			int hours_duration = Integer.parseInt(duration);
			long expiration_time = System.currentTimeMillis() + Time.ONE_HOUR * hours_duration;
			if(scum != null) {
				if(Rank.getPrimaryRank(scum).ordinal() > Rank.getPrimaryRankIndex(player)) {
					player.getActionSender().sendMessage("This person's rank is too great for you to punish!");
				}
				World.getWorld().getBanManager().moderate(modName, scum, type, byIp, expiration_time, reason);
				player.getActionSender().sendMessage("Player was punished!");
			} else {
				World.getWorld().getBanManager().moderate(modName, scumName, null, hours_duration, byIp, expiration_time, reason);
				player.getActionSender().sendMessage("Player is offline but has still been punished.");
			}
		} catch(Exception e) {
			player.getActionSender().sendMessage("Use the command as ::ban name,duration(in hours),reason");
			player.getActionSender().sendMessage("For instance ::ban spammer123,10,spamming edge");
		}
		return true;
	}

}
