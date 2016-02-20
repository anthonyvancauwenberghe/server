package org.hyperion.rs2.commands.impl;

import org.hyperion.Configuration;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.Lock;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.rs2.util.TextUtils;

public class YellCommand extends Command {

	public static final int NORMAL_YELL_DELAY = 240000;
	public static final int DONATOR_YELL_DELAY = 180000;
	public static final int SUPER_YELL_DELAY = 120000;

	public static int minYellRank = 0;

	public YellCommand() {
		super("yell", Rank.PLAYER);
	}

	private int getYellDelay(Player player) {
		if(player.getPermExtraData().getLong("loweredYellTimer") >= System.currentTimeMillis() && player.getPermExtraData().getLong("loweredYellTimer") != 0) {
			double yellReducement = 1.0;
			if(player.getPermExtraData().get("yellReduction") != null) {
				try {
					yellReducement = Double.parseDouble((String)player.getPermExtraData().get("yellReduction"));
				} catch(Exception e) {
					yellReducement = (double)player.getPermExtraData().get("yellReduction");
				}
			}
			if(Rank.hasAbility(player, Rank.SUPER_DONATOR))
				return (int)(SUPER_YELL_DELAY * yellReducement);
			else if (Rank.hasAbility(player, Rank.DONATOR))
				return (int)(DONATOR_YELL_DELAY * yellReducement);
			return (int)(NORMAL_YELL_DELAY * yellReducement);
		} else if(player.getPermExtraData().getLong("loweredYellTimer") < System.currentTimeMillis() && player.getPermExtraData().getLong("loweredYellTimer") != 0) {
			player.getPermExtraData().remove("loweredYellTimer");
			player.getPermExtraData().remove("yellReduction");
		}
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
		input = input.replaceAll("tradereq", "").replaceAll("duelreq", "").replaceAll(":clan:", "");

		long yellMilliseconds = System.currentTimeMillis() - player.getPermExtraData().getLong("yelltimur");
		long yellDelay = getYellDelay(player);

		if(!Rank.isStaffMember(player) && !Configuration.getString(Configuration.ConfigurationObject.NAME).equalsIgnoreCase("ArteroBeta")) {
			if((player.getSkills().getTotalLevel() >= 1800 || player.getPoints().getEloPeak() >= 1800) || Rank.hasAbility(player, Rank.SUPER_DONATOR) || Rank.hasAbility(player, Rank.DONATOR)) {
				if(yellMilliseconds < getYellDelay(player)) {
					player.sendMessage("Please wait " + (int) ((yellDelay - yellMilliseconds) / 1000) + " seconds before yelling.");
					if(player.getClanName().equalsIgnoreCase(""))
						ClanManager.joinClanChat(player, "chatting", false);
					return false;
				}
				player.getYelling().updateYellTimer();
				player.getPermExtraData().put("yelltimur", player.getYelling().getYellTimer());
			} else {
				player.sendMessage("You need at least 1,800 PvP Rating peak, 1800 total level or purchase donator", "to start yelling");
                if(player.getClanName().equalsIgnoreCase(""))
                    ClanManager.joinClanChat(player, "chatting", false);
				return false;
			}
		}

		final String colors = Rank.getPrimaryRank(player).getYellColor();

		final String tag = getTag(player);

		final String suffix = (player.hardMode() ? "[I]" : "") + "[" + colors + tag + "@bla@] " + player.getSafeDisplayName() + "@bla@: " + (Rank.getPrimaryRank(player) == Rank.OWNER ? colors : "@bla@");
		final String suffixWithoutTitles = (player.hardMode() ? "[I]" : "") + "[" + colors + Rank.getPrimaryRank(player).toString() + "@bla@] " + player.getSafeDisplayName() + "@bla@: " + (Rank.getPrimaryRank(player) == Rank.OWNER ? colors : "@bla@");
		input = input.replaceFirst("yell ", "");
		input = TextUtils.ucFirst(input);
		if(!Rank.isStaffMember(player) && !Configuration.getString(Configuration.ConfigurationObject.NAME).equalsIgnoreCase("ArteroBeta")) {
			World.submit(
					new Task(yellDelay) {
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
		for(Player other : World.getPlayers()) {
			if(other != null) {
				if(!Lock.isEnabled(other, Lock.YELL)) {
					String message;
					if (!Lock.isEnabled(other, Lock.YELL_TITLES)) {
						message = suffix + input;
					} else {
						message = suffixWithoutTitles + input;
					}
					other.getActionSender().sendMessage(message);
				}
			}
		}

		return true;
	}

	public String getTag(Player player) {
		if(player.getName().equalsIgnoreCase("nab"))
			return "B";
		if(player.getPoints().getDonatorPointsBought() < 25000 || Rank.isStaffMember(player) || player.getYelling().getTag().equals("")) {
			return Rank.getPrimaryRank(player).toString();
		}
		return player.getYelling().getTag();
	}
}
