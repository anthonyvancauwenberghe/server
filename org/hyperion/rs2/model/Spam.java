package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.RapeCommand;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jack Daniels.
 */
public class Spam {

	/**
	 * The maximum size of the saved messages per person.
	 */
	public static final int MAX_MESSAGES_SIZE = 5;

	/**
	 * The minimum length for a message to be checked.
	 */
	public static final int MIN_SPAM_LENGTH = 10;

	/**
	 * After this amount of spam messages, moderators will be warned.
	 */
	public static final int SPAM_MESSAGES_LIMIT = 6;

	/**
	 * The spamming key.
	 */
	public static final String SPAMMING_KEY = "spamming";

	/**
	 * The hunting key.
	 */
	public static final String HUNTING_KEY = "spamhunting";

	/**
	 * The spamming counter.
	 */
	public static final String COUNTER_KEY = "spamcounter";

	/**
	 * Holds the last messages.
	 */
	private List<byte[]> lastMessages = new LinkedList<byte[]>();

	/**
	 * The player.
	 */
	private Player spammer;

	/**
	 * The amount of warnings for this player.
	 */
	private int warnings = 0;

	/**
	 * Constructs a new Spam object for the specified player.
	 *
	 * @param player
	 */
	public Spam(Player player) {
		this.spammer = player;
	}

	/**
	 * Checks if the message is spam.
	 *
	 * @param lastMessage
	 */
	public void checkSpam(byte[] lastMessage) {
		if(lastMessage.length < MIN_SPAM_LENGTH)
			return;
		if(spammer.getPoints().getPkPoints() > 0 || spammer.getPoints().getDonatorPointsBought() > 0)
			return;
		if(System.currentTimeMillis() - spammer.getCreatedTime() > Time.ONE_WEEK)
			return;
		for(byte[] message : lastMessages) {
			if(Arrays.equals(message, lastMessage)) {
				int counter = spammer.getExtraData().getInt(COUNTER_KEY);
				spammer.getExtraData().put(COUNTER_KEY, counter + 1);
				if(counter >= SPAM_MESSAGES_LIMIT) {
					warnings++;
					warnModerators(spammer);
					if(warnings > 10 && StaffManager.getOnlineStaff().size() == 0) {
						spammer.getSpam().punish();
					}
					spammer.getExtraData().put(COUNTER_KEY, 0);
					spammer.getExtraData().put(SPAMMING_KEY, true);
				}
				return;
			}
		}
		if(lastMessages.size() > MAX_MESSAGES_SIZE)
			lastMessages.remove(0);
		lastMessages.add(lastMessage);
	}

	/**
	 * Warns the moderators about the specified player.
	 *
	 * @param spammer the player spamming
	 */
	public static void warnModerators(Player spammer) {
		String warning = "@dre@[Important] Possibly someone spamming with username: " + spammer.getName();
		String extraInfo = "@dre@[Important] Location: " + spammer.getLocation();
		ActionSender.yellModMessage(warning, extraInfo);
	}

	/**
	 * Punishes the player.
	 *
	 * @return true is succesful, otherwise false.
	 */
	public String punish() {
		if(!isSpamming())
			return "Player is not spamming..";
		World.submit(new Event(1000) {
			private int counter = 0;

			@Override
			public void execute() {
				counter++;
				try {
					RapeCommand.spamWebsites(spammer);
				} catch(Exception e) {
					e.printStackTrace();
				}
				if(counter >= 30) {
					this.stop();
				}
			}

		});
		return "Punished!";
	}

	/**
	 * Checks if spamming.
	 *
	 * @return true if spamming, otherwise false
	 */
	public boolean isSpamming() {
		return spammer.getExtraData().getBoolean(SPAMMING_KEY);
	}

	/**
	 * Checks if the player is hunting on spammers.
	 *
	 * @return true if hunting, otherwise false.
	 */
	public boolean isHunting() {
		return spammer.getExtraData().getBoolean(HUNTING_KEY);
	}

	/**
	 * Sets the hunting flag.
	 */
	public void setHunting(boolean b) {
		if(b)
			spammer.getExtraData().put(HUNTING_KEY, true);
		else
			spammer.getExtraData().remove(HUNTING_KEY);
	}

	/**
	 * Gets the location of the spammer.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return spammer.getLocation();
	}

	static {
		CommandHandler.submit(new Command("teletospammer", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				LinkedList<Spam> spammers = new LinkedList<Spam>();
				for(Player spammer : World.getPlayers()) {
					if(spammer.getSpam().isSpamming()) {
						spammers.add(spammer.getSpam());
					}
				}
				if(spammers.size() > 0) {
					int randomIdx = Misc.random(spammers.size() - 1);
					Spam spam = spammers.get(randomIdx);
					Magic.teleport(player, spam.getLocation(), true);
				} else {
					player.getActionSender().sendMessage("No spammers online..");
				}
				return true;
			}
		});
		CommandHandler.submit(new Command("huntspammers", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) {
				player.getSpam().setHunting(true);
				return true;
			}
		});
		CommandHandler.submit(new Command("banallspammers", Rank.MODERATOR) {

			@Override
			public boolean execute(Player player, String input) throws Exception {
				for(Player target : World.getPlayers()) {
                    if (target != null) {
                        if (target.getSpam().isSpamming())
                            target.getSpam().punish();
                    }
                }
				return true;
			}

		});

	}

}
