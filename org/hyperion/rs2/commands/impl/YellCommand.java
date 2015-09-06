package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.rs2.util.TextUtils;

import java.util.LinkedList;

public class YellCommand extends Command {

	public static final int NORMAL_YELL_DELAY = 240000;
	public static final int DONATOR_YELL_DELAY = 180000;
	public static final int SUPER_YELL_DELAY = 120000;

	public static int minYellRank = 0;

	private LinkedList<Player> lastYells = new LinkedList<Player>();

	public YellCommand() {
		super("yell", Rank.PLAYER);
	}

	private int getYellDelay(Player player) {
		if(Rank.hasAbility(player, Rank.SUPER_DONATOR))
			return SUPER_YELL_DELAY;
		else if (Rank.hasAbility(player, Rank.DONATOR))
			return DONATOR_YELL_DELAY;
		return NORMAL_YELL_DELAY;
	}

	@Override
	public boolean execute(Player player, String input) {

		if(Rank.getPrimaryRank(player).ordinal() < minYellRank) {
			player.getActionSender().sendMessage("An administrator has set the minimum yell rank higher temporarily");
			return false;
		}

		if(player.isMuted || player.yellMuted) {
			player.getActionSender().sendMessage("Muted players cannot use the yell command.");
			return false;
		}

		if(!player.getPermExtraData().getBoolean("yellAccepted")) {
			player.getActionSender().yellRules();
			return false;
		}

		if(!Rank.hasAbility(player, Rank.MODERATOR) && input.contains("@"))
			return false;

		input = PushMessage.filteredString(input);
		input = input.replaceAll("tradereq", "").replaceAll("duelreq", "");

		long yellMilliseconds = (long)(System.currentTimeMillis() - player.getPermExtraData().getLong("yelltimur"));

		if(!Rank.isStaffMember(player)) {
			if((player.getSkills().getTotalLevel() >= 1800 || player.getPoints().getEloPeak() >= 1800) || Rank.hasAbility(player, Rank.SUPER_DONATOR) || Rank.hasAbility(player, Rank.DONATOR)) {
				if(yellMilliseconds < getYellDelay(player)) {
					player.sendMessage("Please wait " + (int) ((getYellDelay(player) - yellMilliseconds) / 1000) + " seconds before yelling.");
					if(player.getClanName().equalsIgnoreCase(""))
						ClanManager.joinClanChat(player, "chatting", false);
					return false;
				}
				player.getYelling().updateYellTimer();
				player.getPermExtraData().put("yelltimur", player.getYelling().getYellTimer());
			} else {
				player.sendMessage("You need at least 1,800 PvP Rating peak, 1800 total level or purchase donator", "to start yelling");
				return false;
			}
		}

		final String colors = Rank.getPrimaryRank(player).getYellColor();

		final boolean hasTag = (!player.getYelling().getTag().isEmpty() && !Rank.isStaffMember(player)) || player.getName().equalsIgnoreCase("nab");

		final String tag = hasTag ? player.getYelling().getTag() : Rank.getPrimaryRank(player).toString();

		final String suffix = (player.hardMode() ? "[I]" : "") + "[" + colors + tag + "@bla@] " + player.getSafeDisplayName() + "@bla@: " + (Rank.getPrimaryRank(player) == Rank.OWNER ? colors : "@bla@");
		input = input.replaceFirst("yell ", "");
		input = TextUtils.ucFirst(input);
		if(!Rank.isStaffMember(player)) {
			World.getWorld().submit(
					new Event(getYellDelay(player)) {
						public void execute() {
							player.sendMessage("[B] Nab: Hey " + player.getSafeDisplayName() + ", you can yell again!");
							stop();
						}
					}
			);
		}

		/**
		 * {@link org.hyperion.rs2.util.PushMessage}
		 */
		PushMessage.pushYell(suffix, input, player);

		return true;
	}
}
