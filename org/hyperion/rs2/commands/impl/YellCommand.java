package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.rs2.util.TextUtils;

import java.util.LinkedList;

public class YellCommand extends Command {
	
	private static enum YellRanks {
		NEWBIE(0),
		NOVICE(10),
		JUNIOR(40),
		PKER(80),
		KILLER(150),
		HUNTER(250),
		MURDERER(400),
		SERJEANT(600),
		SLAYER(800),
		CAPTAIN(1000),
		ELITE(1500),
		COMMANDER(2000),
		WAR_CHIEF(3000),
		REAPER(4000),
		OVERLORD(6000),
		GOD(10000);
		
		private YellRanks(final int minimum) {
			this.minimum = minimum;
			this.title = TextUtils.titleCase(super.toString().replaceAll("_", ""));
		}
		
		private final int minimum;
		private final String title;
		
		public final String toString() {
			return title;
		}
	}

	public static final int NORMAL_YELL_DELAY = 30000;
	public static final int DONATOR_YELL_DELAY = 10000;
	public static final int SKILLER_YELL_DELAY = 15000;
	
	public static int minYellRank = 0;

	public static final int MAX_SAME_PERSON_YELLS = 3;

	private LinkedList<Player> lastYells = new LinkedList<Player>();

	public YellCommand() {
		super("yell", Rank.PLAYER);
	}
	
	private int getYellDelay(Player player) {
		int timer = 60000;
		int deltaElo = player.getPoints().getEloRating() - 1200;
		if(deltaElo <= 0)
			deltaElo = 1;
		int reduction = (deltaElo/20) * 1000;
		if(reduction > 60000)
			reduction = 60000;
		timer -= reduction;
		return NORMAL_YELL_DELAY + timer;
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

		if(!Rank.hasAbility(player, Rank.MODERATOR) && input.contains("@"))
			return false;

		if(lastYells.size() >= MAX_SAME_PERSON_YELLS && !Rank.isStaffMember(player)) {
			boolean spamming = true;
			for(Player lastYeller : lastYells) {
				if(! lastYeller.equals(player)) {
					spamming = false;
					break;
				}
			}
			/*if(spamming) {
				player.getActionSender().sendMessage("You've yelled too many times recently.");
				return false;
			}*/
		}
		if(Rank.getPrimaryRank(player).ordinal() < Rank.HELPER.ordinal()) {
			input = input.replaceAll("req", "reqs");
			input = PushMessage.filteredString(input);
		} else {
			input = input.replaceAll("tradereq", "").replaceAll("duelreq", "");
		}
		input = input.replaceAll("arsen", "graham");
		input = input.replaceAll("Arsen", "Graham");

		long yellMilliseconds = (long) (System.currentTimeMillis() - player.getYelling().getYellTimer());
		if(!Rank.isStaffMember(player) && !Rank.hasAbility(player, Rank.SUPER_DONATOR)) {
			if(Rank.hasAbility(player, Rank.DONATOR)) {
				if(yellMilliseconds < DONATOR_YELL_DELAY) {
					player.getActionSender().sendMessage("Please wait " + (int) ((DONATOR_YELL_DELAY - yellMilliseconds) / 1000) + " seconds before yelling.");
					return false;
				}
				player.getYelling().updateYellTimer();
			} else if((player.getSkills().getTotalLevel() >= 1800 || player.getPoints().getEloPeak() >= 2000)) {
				if(yellMilliseconds < getYellDelay(player)) {
					player.getActionSender().sendMessage("Please wait " + (int) ((getYellDelay(player) - yellMilliseconds) / 1000) + " seconds before yelling.");
					return false;
				}
				player.getYelling().updateYellTimer();
			} else {
               player.sendMessage("Use the @red@help@bla@ clan chat for help", "Use the @red@::market@bla@ or the @red@market@bla@ cc for selling/buying", "Use the @red@duel@bla@ cc for staking");
               return false;
			}
		}
		final String colors = Rank.getPrimaryRank(player).getYellColor();
		final boolean hasTag = !player.getYelling().getTag().isEmpty();
		final boolean aboveDonator = Rank.getPrimaryRank(player).ordinal() > Rank.DONATOR.ordinal();
		final String tag = hasTag ? player.getYelling().getTag() : Rank.getPrimaryRank(player).toString();
		final String suffix = "["+colors+tag +"@bla@]" +player.getSafeDisplayName() + "@bla@: ";
		input = input.replaceFirst("yell ", "");
		/**
		 * {@link org.hyperion.rs2.util.PushMessage}
		 */
		PushMessage.pushYell(suffix, input, player);
		//Makes sure one player can't yell 10 messages in a row.
		lastYells.add(player);
		if(lastYells.size() > 3) {
			lastYells.poll();
		}
		return true;
	}
	
	public static String getTitle(final Player player) {
		final int kills = player.getKillCount();
		final YellRanks ranks[] = YellRanks.values();
		for(int i = ranks.length - 1; i >= 0; i--) {
			if(kills > ranks[i].minimum)
				return ranks[i].toString();
		}
		return "Newbie";
	}
	
	public static void main(String args[]) {
		System.out.println(Rank.SUPER_DONATOR.toString()+"hello");
	}
}
