package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLUtils;

public class UnbanCommand extends Command {

	private int type;

	public UnbanCommand(String startsWith, int type) {
		super(startsWith, Rank.FORUM_MODERATOR);
		this.type = type;
	}

	public UnbanCommand(String startsWith, int type, Rank... rights) {
		super(startsWith, rights);
		this.type = type;
	}

	@Override
	public boolean execute(Player player, String input) {
		try {
			String parts[] = filterInput(input).split(",");
			String name = parts[0];
			if(name.length() > Player.MAX_NAME_LENGTH)
				throw new Exception("Invalid name");
			String reason = parts[1];
			if(reason.length() < 1)
				throw new Exception("Reason too short");
			reason = SQLUtils.checkInput(reason);
			String mes = World.getWorld().getBanManager().unmoderate(player.getName(), name, type, reason);
			player.getActionSender().sendMessage(mes);
		} catch(Exception e) {
			player.getActionSender().sendMessage("Use the command as ::unban name,reason");
			player.getActionSender().sendMessage("For instance ::unban goodguy13,accidental ban");
		}
		return true;
	}

}
