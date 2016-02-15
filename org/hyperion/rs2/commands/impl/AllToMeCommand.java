package org.hyperion.rs2.commands.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.util.Misc;

public class AllToMeCommand extends Command {

	public AllToMeCommand(String startsWith, Rank... rights) {
		super(startsWith, rights);
	}

	@Override
	public boolean execute(Player player, String input) {
		for(final Player otherPlayer : World.getPlayers()) {
			if(player == otherPlayer)
				continue;
			final int x = player.getLocation().getX() + Misc.random(3);
			final int y = player.getLocation().getY() + Misc.random(3);
			World.submit(new Task(Misc.random(10000)) {
				public void execute() {
					Magic.teleport(otherPlayer, x, y, 0, true);
                    this.stop();
				}
			});
		}
		return true;
	}

}
