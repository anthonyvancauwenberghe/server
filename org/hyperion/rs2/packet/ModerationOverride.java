package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.misc2.Jail;

/**
 * @author Wasay
 */
public class ModerationOverride {
	/**
	 * Sends moderation dialogue allowing for easy punishment
	 */
	public static final void sendModerationOptions(final Player player, final Player other) {
		player.setModeration(other);
		DialogueManager.openDialogue(player, 136);
	}

	public static final boolean canModerate(final Player p) {
		return Rank.hasAbility(p, Rank.MODERATOR);
	}

	/**
	 * close() was deprecated recently yet it'll serve the purposes we need
	 * So @SuppressWarnings("deprecation") should be safe in this case
	 */
	@SuppressWarnings("deprecation")
	public static final void kickPlayer(Player p) {
		p.getSession().close();
	}

	public static final void jailPlayer(Player p) {
		p.setTeleportTarget(Jail.LOCATION);
	}

	public static final void mutePlayer(Player p) {
		p.isMuted = true;
		p.getActionSender().sendMessage("You have been muted!");
	}
}
