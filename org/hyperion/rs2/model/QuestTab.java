package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.event.impl.ServerMinigame;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.bounty.BountyPerks;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.util.Calendar;
import java.util.List;

/**
 * @author Arsen Maxyutov.
 * @author Glis				29/07/2015
 */
public class QuestTab {
	/**
	 * The Ids used to clear the Quest tab.
	 */
	public static int[] QUEST_TAB_IDS = {	//Stored the usable ID's
			/*663,*/ 7332, 7333, 7334, 7336, 7383, 7339, 7338, 7340, 7346, 7341, 7342, 7337, 7343, 7335, 7344,
			7345, 7347, 7348, /*682,*/ 12772, 673, 7352, 17510, 7353, 12129,
			8438, 12852, 15841, 7354, 7355, 7356, 8679, 7459, 16149, 6987,
			7357, 12836, 7358, 7359, 14169, 10115, 14604, 7360, 12282, 13577,
			12839, 7361, 16128, 11857, 7362, 7363, 7364, 10125, /*4508,*/10135, 18517,
			11907, 7365, 7366, 7367, 13389, 15487, 7368, 11132, 7369, 12389,
			13974, 6027, 7370, 8137, 7371, 12345, 7372, 8115, 18684,
			15499, 18306, 668, 8576, 12139, 14912, 7374, 7373, 8969, 15352,
			7375, 7376, 15098, 15592, 249, 1740, 15235, 3278, 664, 7378, 6518,
			7379, 7380, 7381, 11858, 191, 9927, 6024, 7349, 7350, 7351, 13356
	};
	public static int[] QUEST_TAB_TO_CLEAR = {	//Stored the ID's to clear
			663, 7332, 7333, 7334, 7336, 7383, 7339, 7338, 7340, 7346, 7341, 7342, 7337, 7343, 7335, 7344,
			7345, 7347, 7348, 682, 12772, 673, 7352, 17510, 7353, 12129,
			8438, 12852, 15841, 7354, 7355, 7356, 8679, 7459, 16149, 6987,
			7357, 12836, 7358, 7359, 14169, 10115, 14604, 7360, 12282, 13577,
			12839, 7361, 16128, 11857, 7362, 7363, 7364, 10135, 10125, /*4508,*/ 18517,
			11907, 7365, 7366, 7367, 13389, 15487, 7368, 11132, 7369, 12389,
			13974, 6027, 7370, 8137, 7371, 12345, 7372, 8115, 18684,
			15499, 18306, 668, 8576, 12139, 14912, 7374, 7373, 8969, 15352,
			7375, 7376, 15098, 15592, 249, 1740, 15235, 3278, 664, 7378, 6518,
			7379, 7380, 7381, 11858, 191, 9927, 6024, 7349, 7350, 7351, 13356
	};

	private static int max_index = 0;

	private static int getId(int index) {
		if(index > max_index)
			max_index = index;
		return QUEST_TAB_IDS[index];
	}

	private static int getNextIndex() {
		if(max_index > QUEST_TAB_IDS.length)
			return QUEST_TAB_IDS.length;
		int i = max_index++;
		return i;
	}

	private Player player;

	public QuestTab(Player player) {
		this.player = player;
	}

	public void createQuestTab() {
		resetQuestTab();
		updateQuestTab();
	}

	public void updateQuestTab() {
		sendPlayerCount();
		sendStaffCount();
		sendUptime();
		sendBonusSkill();
		sendKills();
		sendDeaths();
		sendKdr();
		sendPvpRating();
		sendItemsKept();
		sendPkPoints();
		sendVotePoints();
		sendDonatePoints();
		sendHonorPoints();
		sendBHPoints();
		sendBHTarget();
		sendBHEnabled();
		sendBHPerks();
		sendYellEnabled();
		sendTriviaEnabled();
		sendPkMessagesEnabled();
		sendStaffMessagesEnabled();
		sendParticlesEnabled();
		sendTitlesEnabled();
		sendExpLockEnabled();
		sendRankInfo();
		fillQuestTab();
	}

	public void resetQuestTab() {
		for(int i = 0; i < QUEST_TAB_TO_CLEAR.length; i++) {
			player.getActionSender().sendString("", QUEST_TAB_TO_CLEAR[i]);
			player.getActionSender().sendTooltip(QUEST_TAB_TO_CLEAR[i], "");
		}
		player.getActionSender().sendString("@yel@     ArteroPk", 640);
		player.getActionSender().sendString("@yel@" + Misc.centerQuestTab("- Server Information -"), getId(0));
		player.getActionSender().sendFont(getId(0), 2);
		player.getActionSender().sendString("", getId(5));
		player.getActionSender().sendString("@yel@" + Misc.centerQuestTab("- Pk Information -"), getId(6));
		player.getActionSender().sendFont(getId(6), 2);
		player.getActionSender().sendString("", getId(16));
		player.getActionSender().sendString("@yel@" + Misc.centerQuestTab("- Ingame Points -"), getId(13));
		player.getActionSender().sendFont(getId(13), 2);
		player.getActionSender().sendString("", getId(17));
		player.getActionSender().sendString("@yel@" + Misc.centerQuestTab("- Bounty hunter -"), getId(18));
		player.getActionSender().sendFont(getId(18), 2);
		player.getActionSender().sendString("", getId(23));
		player.getActionSender().sendString("@yel@" + Misc.centerQuestTab("- Locks -"), getId(24));
		player.getActionSender().sendFont(getId(24), 2);
		player.getActionSender().sendString("", getId(32));
	}

	public void sendPlayerCount() {
		int players = (int)(World.getWorld().getPlayers().size() * World.PLAYER_MULTI);
		int id = getId(1);	//Easier to adjust later

		player.getActionSender().sendString("@or1@Players online: @gre@" + players, id);
		player.getActionSender().sendTooltip(id, "Players online");
	}
	public void sendStaffCount() {
		int id = getId(2);
		int staffOnline = World.getWorld().getStaffManager().getOnlineStaff().size();
		player.getActionSender().sendString("@or1@Staff online: " + (staffOnline == 0 ? "@red@" : "@gre@") + staffOnline, id);
		player.getActionSender().sendTooltip(id, "Staff online");
	}

	public void sendUptime() {
		int id = getId(3);
		player.getActionSender().sendString((Rank.hasAbility(player, Rank.ADMINISTRATOR) && Events.eventName == "") ? "@or1@Uptime: @gre@" + Server.getUptime() : (Events.eventName == "" ? "" : "@or1@Event: @gre@" + Events.eventName), id);
		player.getActionSender().sendTooltip(id, (Events.eventName == "" ? "" : "Teleport to event"));
	}

	public void sendBonusSkill() {
		int id = getId(4);
		player.getActionSender().sendString("@or1@Bonus skill: @gre@"+ Misc.getSkillName(Skills.BONUS_SKILL), id);
		//player.getActionSender().sendString("@or1@Bonus skill: @gre@All skills", id);
		player.getActionSender().sendTooltip(id, "Bonus skill");
	}

	public void sendKills() {
		int id = getId(7);
		player.getActionSender().sendString("@or1@Kills: @gre@" + player.getKillCount(), id);
		player.getActionSender().sendTooltip(id, "Yell kills");
	}

	public void sendDeaths() {
		int id = getId(8);
		player.getActionSender().sendString("@or1@Deaths: @gre@" + player.getDeathCount(), id);
		player.getActionSender().sendTooltip(id, "Yell deaths");
	}

	public void sendKdr() {
		int id = getId(9);
		player.getActionSender().sendString("@or1@Kill/Death: @gre@" + player.getKDR(), id);
		player.getActionSender().sendTooltip(id, "Yell kdr");
	}

	public void sendPvpRating() {
		int id = getId(10);
		player.getActionSender().sendString("@or1@PvP rating: @gre@" + player.getPoints().getEloRating(), id);
		player.getActionSender().sendTooltip(id, "Yell PvP rating");
	}

	public void sendItemsKept() {
		int id = getId(11);
		player.getActionSender().sendString("@or1@Items kept on death", id);
		player.getActionSender().sendTooltip(id, "Open items kept on death");
	}

	public void sendPkPoints() {
		int id = getId(14);
		player.getActionSender().sendString("@or1@ArteroPK points: @gre@" + Misc.shortNumber(player.getPoints().getPkPoints()), id);
		player.getActionSender().sendTooltip(id, "Yell ArteroPK points");
	}

	public void sendVotePoints() {
		int id = getId(15);
		player.getActionSender().sendString("@or1@Voting points: @gre@" + Misc.shortNumber(player.getPoints().getVotingPoints()), id);
		player.getActionSender().sendTooltip(id, "Yell voting points");
	}

	public void sendDonatePoints() {
		int id = getId(16);
		player.getActionSender().sendString("@or1@Donator points: @gre@" + Misc.shortNumber(player.getPoints().getDonatorPoints()), id);
		player.getActionSender().sendTooltip(id, "Yell donator points");
	}

	public void sendHonorPoints() {
		int id = getId(17);
		player.getActionSender().sendString("@or1@Honor points: @gre@" + Misc.shortNumber(player.getPoints().getHonorPoints()), id);
		player.getActionSender().sendTooltip(id, "Yell honor points");
	}

	public void sendBHPoints() {
		int id = getId(19);
		player.getActionSender().sendString("@or1@BH points: @gre@" + player.getBountyHunter().getKills(), id);
		player.getActionSender().sendTooltip(id, "Yell BH points");
	}

	public void sendBHTarget() {
		int id = getId(20);
		player.getActionSender().sendString("@or1@Target: @gre@" + (player.getBountyHunter().getTarget() != null ? player.getBountyHunter().getTarget().getSafeDisplayName() : "None"), id);
		player.getActionSender().sendString("@or1@Target: @gre@" + (player.getBountyHunter().getTarget() != null ? player.getBountyHunter().getTarget().getSafeDisplayName() : "None"), 36502);
	}

	public void sendBHEnabled() {
		int id = getId(21);
		player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("bhon") ? "Disable" : "Enable") + " bounty hunter", id);
		player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("bhon") ? "Disable" : "Enable") + " bounty hunter");
	}

	public void sendBHPerks() {
		int id = getId(22);
		player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Click to see the BH perks"), id);
		player.getActionSender().sendTooltip(id, "Check BH perks");
	}

	public void sendYellEnabled() {
		int id = getId(25);
		player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledYell") ? "Enable" : "Disable") + " yelling", id);
		player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledYell") ? "Enable" : "Disable") + " yelling");
	}

	public void sendTriviaEnabled() {
		int id = getId(26);
		player.getActionSender().sendString("@or1@" + (player.getTrivia().isEnabled() ? "Disable" : "Enable") + " trivia", id);
		player.getActionSender().sendTooltip(id, (player.getTrivia().isEnabled() ? "Disable" : "Enable") + " trivia");
	}

	public void sendPkMessagesEnabled() {
		int id = getId(27);
		player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledPkMessages") ? "Enable" : "Disable") + " PK messages", id);
		player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledPkMessages") ? "Enable" : "Disable") + " PK messages");
	}

	public void sendStaffMessagesEnabled() {
		int id = getId(28);
		player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "Enable" : "Disable") + " staff login", id);
		player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "Enable" : "Disable") + " staff login");
	}

	public void sendParticlesEnabled() {
		int id = getId(29);
		player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledParticles") ? "Enable" : "Disable") + " particles", id);
		player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledParticles") ? "Enable" : "Disable") + " particles");
	}

	public void sendTitlesEnabled() {
		int id = getId(30);
		player.getActionSender().sendString("@or1@Toggle right-click options", id);
		player.getActionSender().sendTooltip(id, "Toggle right-click options");
	}

	public void sendExpLockEnabled() {
		int id = getId(31);
		player.getActionSender().sendString("@or1@" + (player.xpLock ? "Disable" : "Enable") + " exp lock", id);
		player.getActionSender().sendTooltip(id, (player.xpLock ? "Disable" : "Enable") + " exp lock");
	}

	public void sendRankInfo() {
		max_index = 34;
		boolean hasRank = false;
		for(Rank rank : Rank.values()) {
			if(Rank.hasAbility(player, rank)) {
				int id = getId(getNextIndex());
				player.getActionSender().sendString((Rank.getPrimaryRank(player).equals(rank) ? "@gre@" : "@or1@") + rank.toString(), id);
				player.getActionSender().sendTooltip(id, (Rank.getPrimaryRank(player).equals(rank) ? "" : "Set rank to " + rank.toString()));
				if(!hasRank && rank != Rank.PLAYER)
					hasRank = true;
			}
		}
		player.getActionSender().sendString(!hasRank ? "" : "@or1@     Available ranks", getId(33));
		player.getActionSender().sendFont(getId(33), 2);
		if(!hasRank)
			max_index = 33;
	}

	public void fillQuestTab() {
		for(int i = getNextIndex(); i < QUEST_TAB_IDS.length; i++) {
			player.getActionSender().sendString("", QUEST_TAB_IDS[i]);
		}
	}

	static {
		for(int i = 34; i < 34 + Rank.values().length; i++) {
			final int i2 = i;
			ActionsManager.getManager().submit(getId(i), new ButtonAction() {
				@Override
				public void handle(Player player, int id) {
					int index = 34;
					for(Rank rank : Rank.values()) {
						if(Rank.hasAbility(player, rank)){
							if(i2 == index) {
								player.setPlayerRank(Rank.setPrimaryRank(player, rank));
								player.getQuestTab().sendRankInfo();
							}
							index++;
						}
					}
				}
			});
		}

		ActionsManager.getManager().submit(getId(1), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getActionSender().openPlayersInterface();
			}
		});

		ActionsManager.getManager().submit(getId(2), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				List<Player> onlineStaff = World.getWorld().getStaffManager().getOnlineStaff();
				player.getActionSender().sendMessage("Staff online: @dre@" + onlineStaff.size());
				for(Player staffMember : onlineStaff) {
					final Rank rank = Rank.getPrimaryRank(staffMember);
					player.getActionSender().sendMessage(String.format(
							"[%s%s@bla@] - %s%s",
							rank.getYellColor(), staffMember.display == null || staffMember.display.isEmpty() ? staffMember.getName() : staffMember.display,
							rank.getYellColor(), rank));
				}
			}
		});

		ActionsManager.getManager().submit(getId(3), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				if(Events.eventName != "") {
					Events.joinEvent(player);
				}
			}
		});

		ActionsManager.getManager().submit(getId(4), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				Calendar c = Calendar.getInstance();
				player.sendMessage("@dre@The next bonus skills will be; ");
				int dayOfYear = (c.get(Calendar.DAY_OF_YEAR) + 4);
				for (int i = 1; i <= 5; i++) {
					int bonusSkill = ((dayOfYear + i) % (Skills.SKILL_COUNT - 8)) + 7;
					if (bonusSkill == 21) {
						player.sendMessage("@dre@" + i + ". @bla@Random");
					} else {
						player.sendMessage("@dre@" + i + ". @bla@" + Misc.getSkillName(bonusSkill));
					}
				}
			}
		});

		ActionsManager.getManager().submit(getId(7), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getKillCount() == 0 ? "no" : player.getKillCount()) + " " + (player.getKillCount() == 1 ? "kill" : "kills") + " so far.");
			}
		});

		ActionsManager.getManager().submit(getId(8), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getDeathCount() == 0 ? "no" : player.getDeathCount()) + " " + (player.getDeathCount() == 1 ? "death" : "deaths") + " so far.");
			}
		});

		ActionsManager.getManager().submit(getId(9), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				double kdr = player.getKDR();
				player.forceMessage(String.format("My kill/deathratio is %.2f.", kdr));
			}
		});

		ActionsManager.getManager().submit(getId(11), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getActionSender().openItemsKeptOnDeathInterface(player);
			}
		});

		ActionsManager.getManager().submit(getId(10), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("My PvP rating is " + player.getPoints().getEloRating() + ". " + (player.getPoints().getEloRating() == player.getPoints().getEloPeak() ? "This is also my best PvP rating ever." : "My best PvP rating ever was " + player.getPoints().getEloPeak() + "."));
			}
		});

		ActionsManager.getManager().submit(getId(14), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getPoints().getPkPoints() == 0 ? "no" : player.getPoints().getPkPoints()) + " " + (player.getPoints().getPkPoints() == 1 ? "ArteroPK point" : "ArteroPK points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(15), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getPoints().getVotingPoints() == 0 ? "no" : player.getPoints().getVotingPoints()) + " " + (player.getPoints().getVotingPoints() == 1 ? "voting point" : "voting points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(16), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage(player.getPoints().getDonatorPointsBought() == 0 ? "I have never bought any donator points." : "I bought " + player.getPoints().getDonatorPointsBought() + " donator points for $" + player.getPoints().getDonatorPointsBought() / 100 + " and " + (player.getPoints().getDonatorPointsBought() == player.getPoints().getDonatorPoints() ? "still have them all." : "still have " + player.getPoints().getDonatorPoints() + " of them left."));
			}
		});

		ActionsManager.getManager().submit(getId(17), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				DialogueManager.openDialogue(player, 530);
				}
		});

		ActionsManager.getManager().submit(getId(19), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getBountyHunter().getKills() == 0 ? "no" : player.getBountyHunter().getKills()) + " " + (player.getBountyHunter().getKills() == 1 ? "BH point" : "BH points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(21), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getPermExtraData().put("bhon", player.getBountyHunter().switchEnabled());
				player.getQuestTab().sendBHEnabled();
				player.sendMessage("Bounty hunter is now " + (player.getPermExtraData().getBoolean("bhon") ? "enabled" : "disabled") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(22), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				int perk1 = 0;
				int perk2 = 0;
				int perk3 = 0;
				for(int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.SPEC_RESTORE); i++) {
					perk1++;
				}
				for(int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.VENG_REDUCTION); i++) {
					perk2++;
				}
				for(int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.PRAY_LEECH); i++) {
					perk3++;
				}
				player.sendMessage("@dre@" + (perk1 == 0 ? "" : "(Level " + perk1 + ") ") + "Special perk:@bla@ Increase special attack after a kill.");
				player.sendMessage("@dre@" + (perk2 == 0 ? "" : "(Level " + perk2 + ") ") + "Veng reduction:@bla@ Reduce cooldown on vengeance.");
				player.sendMessage("@dre@" + (perk3 == 0 ? "" : "(Level " + perk3 + ") ") + "Prayer leech:@bla@ Leech opponent's prayer on hit.");
			}
		});

		ActionsManager.getManager().submit(getId(25), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getPermExtraData().put("disabledYell", !player.getPermExtraData().getBoolean("disabledYell"));
				player.getQuestTab().sendYellEnabled();
				player.sendMessage("Yell is now " + (player.getPermExtraData().getBoolean("disabledYell") ? "disabled" : "enabled") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(26), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getTrivia().setEnabled(!player.getTrivia().isEnabled());
				player.getQuestTab().sendTriviaEnabled();
				player.sendMessage("Trivia is now " + (player.getTrivia().isEnabled() ? "enabled" : "disabled") + ".");
				if(player.getTrivia().isEnabled())
					player.sendMessage("To answer, simply use ::answer ANSWER.");
			}
		});

		ActionsManager.getManager().submit(getId(27), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getPermExtraData().put("disabledPkMessages", !player.getPermExtraData().getBoolean("disabledPkMessages"));
				player.getQuestTab().sendPkMessagesEnabled();
				player.sendMessage("Pk messages are now " + (player.getPermExtraData().getBoolean("disabledPkMessages") ? "disabled" : "enabled") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(28), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getPermExtraData().put("disabledStaffMessages", !player.getPermExtraData().getBoolean("disabledStaffMessages"));
				player.getQuestTab().sendStaffMessagesEnabled();
				player.sendMessage("Staff messages are now " + (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "disabled" : "enabled") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(29), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				/*
				player.getPermExtraData().put("disabledParticles", !player.getPermExtraData().getBoolean("disabledParticles"));
				player.getQuestTab().sendParticlesEnabled();
				player.sendMessage("Particles are now " + (player.getPermExtraData().getBoolean("disabledParticles") ? "disabled" : "enabled") + ".");
				*/
				player.sendMessage("Do ::particles to change particles on/off.");
			}
		});

		ActionsManager.getManager().submit(getId(30), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				/*
				player.getPermExtraData().put("disabledTitles", !player.getPermExtraData().getBoolean("disabledTitles"));
				player.getQuestTab().sendTitlesEnabled();
				player.sendMessage("Player titles are now " + (player.getPermExtraData().getBoolean("disabledTitles") ? "disabled" : "enabled") + ".");
				*/
				player.sendMessage("Do ::switchoption trade/profile/follow to change options (do ::commands for more)");
			}
		});

		ActionsManager.getManager().submit(getId(31), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.xpLock = !player.xpLock;
				player.getQuestTab().sendExpLockEnabled();
				player.sendMessage("Experience lock is now " + (player.xpLock ? "on" : "off") + ".");
			}
		});
	}
}
