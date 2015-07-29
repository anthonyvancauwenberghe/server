package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.rs2.event.impl.ServerMinigame;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.util.Misc;

/**
 * @author Arsen Maxyutov.
 * @author Glis				29/07/2015
 */
public class QuestTab {
    /**
     * The Ids used to clear the Quest tab.
     */
    public static int[] QUEST_TAB_IDS = {	//Stored the usable ID's
            663, 7332, 7333, 7334, 7336, 7383, 7339, 7338, 7340, 7346, 7341, 7342, 7337, 7343, 7335, 7344,
            7345, 7347, 7348, 682, 12772, 673, 673, 7352, 17510, 7353, 12129,
            8438, 12852, 15841, 7354, 7355, 7356, 8679, 7459, 16149, 6987,
            7357, 12836, 7358, 7359, 14169, 10115, 14604, 7360, 12282, 13577,
            12839, 7361, 16128, 11857, 7362, 7363, 7364, 10125, 4508, 18517,
            11907, 7365, 7366, 7367, 13389, 15487, 7368, 11132, 7369, 12389,
            13974, 6027, 7370, 8137, 7371, 12345, 7372, 8115, 10135, 18684,
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

    public void updateQuestTab() {
        resetQuestTab();
		sendPlayerCount();
		sendStaffCount();
		sendUptime();
		sendBonusSkill();
		sendName();
		sendRank();
		sendItemsKept();
		sendKills();
		sendDeaths();
		sendKdr();
		sendPvpRating();
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
		sendYellColoursEnabled();
		sendExpLockEnabled();
		sendRankInfo();
		fillQuestTab();
    }

    public void resetQuestTab() {
        for(int i = 0; i < 10; i++) {
            player.getActionSender().sendString("i", QUEST_TAB_IDS[i]);
        }
        player.getActionSender().sendString(Misc.formatPlayerName(player.getName()) + "'s log", 640);
        player.getActionSender().sendString("", getId(3));
        player.getActionSender().sendString("", getId(5));
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("General Information"), getId(6));
        player.getActionSender().sendString("", getId(10));
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("PK"), getId(11));
        player.getActionSender().sendString("", getId(16));
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Points"), getId(17));
        player.getActionSender().sendString("", getId(23));
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Bounty Hunter"), getId(24));
        player.getActionSender().sendString("", getId(28));
		player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Locks"), getId(29));
		player.getActionSender().sendString("", getId(34));
    }

    public void sendPlayerCount() {
        int players = (int)(World.getWorld().getPlayers().size() * World.PLAYER_MULTI);
        player.getActionSender().sendString("@or1@Players online: @gre@" + players, getId(0));
    }
    public void sendStaffCount() {
        int staffOnline = 0;
        for(Player i : World.getWorld().getPlayers()) {
            if (i != null && Rank.isStaffMember(i)) {
                staffOnline++;
            }
        }
        player.getActionSender().sendString("@or1@Staff online: " + (staffOnline == 0 ? "@red@" : "@gre@") + staffOnline, getId(1));
    }

    public void sendUptime() {
        player.getActionSender().sendString(Rank.hasAbility(player, Rank.ADMINISTRATOR) ? "@or1@Uptime: @gre@" + Server.getUptime() : (ServerMinigame.name == null ? "" : "@or1@Event: @gre@" + ServerMinigame.name), getId(2));
    }

    public void sendBonusSkill() {
        player.getActionSender().sendString("@or1@Bonus skill: @gre@"+ Misc.getSkillName(Skills.BONUS_SKILL), getId(4));
    }

    public void sendName() {
        player.getActionSender().sendString("@or1@Name: @gre@"+ Misc.formatPlayerName(player.getName()), getId(7));
    }

    public void sendRank() {
        player.getActionSender().sendString("@or1@Rank: @gre@" + player.getQuestTabRank(), getId(8));
    }

    public void sendItemsKept() {
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Items kept on death"), getId(9));
    }

    public void sendKills() {
        player.getActionSender().sendString("@or1@Kills: @gre@" + player.getKillCount(), getId(12));
    }

    public void sendDeaths() {
        player.getActionSender().sendString("@or1@Deaths: @gre@" + player.getDeathCount(), getId(13));
    }

    public void sendKdr() {
        player.getActionSender().sendString("@or1@Kill/Death: @gre@" + player.getKDR(), getId(14));
    }

    public void sendPvpRating() {
        player.getActionSender().sendString("@or1@PvP rating: @gre@" + player.getPoints().getEloRating(), getId(15));
    }

    public void sendPkPoints() {
        player.getActionSender().sendString("@or1@PK points: @gre@" + player.getPoints().getPkPoints(), getId(18));
    }

    public void sendVotePoints() {
        player.getActionSender().sendString("@or1@Voting points: @gre@" + player.getPoints().getVotingPoints(), getId(19));
    }

    public void sendDonatePoints() {
        player.getActionSender().sendString("@or1@Donator points: @gre@" + player.getPoints().getDonatorPoints() + "@or1@/@gre@" + player.getPoints().getDonatorPointsBought(), getId(20));
    }

    public void sendHonorPoints() {
        player.getActionSender().sendString("@or1@Honor points: @gre@" + player.getPoints().getHonorPoints(), getId(21));
    }

    public void sendBHPoints() {
        player.getActionSender().sendString("@or1@BH points: @gre@" + player.getBountyHunter().getKills(), getId(22));
    }

    public void sendBHTarget() {
        player.getActionSender().sendString("@or1@Target: @gre@" + (player.getBountyHunter().getTarget() != null ? player.getBountyHunter().getTarget().getSafeDisplayName() : "None"), getId(25));
    }

    public void sendBHEnabled() {
        player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("bhon") ? "Disable" : "Enable") + " bounty hunter", getId(26));
    }

    public void sendBHPerks() {
        player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Click to see your BH perks"), getId(27));
    }

    public void sendYellEnabled() {
        player.getActionSender().sendString("@or1@" + (player.getYelling().isYellEnabled() ? "Disable" : "Enable") + " yelling", getId(30));
    }

    public void sendTriviaEnabled() {
        player.getActionSender().sendString("@or1@" + (player.getTrivia().isEnabled() ? "Disable" : "Enable") + " trivia", getId(31));
    }

    public void sendYellColoursEnabled() {
        player.getActionSender().sendString("@or1@" + (player.getYelling().isYellColoursEnabled() ? "Disable" : "Enable") + " yell colors", getId(32));
    }

    public void sendExpLockEnabled() {
        player.getActionSender().sendString("@or1@" + (player.xpLock ? "Disable" : "Enable") + " exp lock", getId(33));
    }

	public void sendRankInfo() {
		max_index = 36;
		boolean hasRank = false;
			for(Rank rank : Rank.values()) {
				if(Rank.hasAbility(player, rank)) {
					player.getActionSender().sendString("@or1@" + rank.toString(), getId(getNextIndex()));
					if(!hasRank)
						hasRank = true;
				}
			}
        player.getActionSender().sendString(!hasRank ? "" : "@or1@" + Misc.centerQuestTab("Available ranks"), getId(35));
	}

    public void fillQuestTab() {
        for(int i = getNextIndex(); i < QUEST_TAB_IDS.length; i++) {
            player.getActionSender().sendString("", QUEST_TAB_IDS[i]);
        }
    }

    static {
		for(int i = 36; i < 36 + Rank.values().length; i++) {
			final int i2 = i;
			ActionsManager.getManager().submit(getId(i), new ButtonAction() {
				@Override
				public void handle(Player player, int id) {
					int index = 36;
					for(Rank rank : Rank.values()) {
						if(Rank.hasAbility(player, rank)){
							if(i2 == index) {
								player.setPlayerRank(Rank.setPrimaryRank(player, rank));
								player.getQuestTab().sendRank();
								player.getQuestTab().sendRankInfo();
							}
							index++;
						}
					}
				}
			});
		}

        ActionsManager.getManager().submit(getId(0), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getActionSender().openPlayersInterface();
            }
        });

		ActionsManager.getManager().submit(getId(1), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getActionSender().openStaffInterface();
			}
		});

		ActionsManager.getManager().submit(getId(9), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getActionSender().openItemsKeptOnDeathInterface(player);
			}
		});

		ActionsManager.getManager().submit(getId(12), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getKillCount() == 0 ? "no" : player.getKillCount()) + " " + (player.getKillCount() == 1 ? "kill" : "kills") + " so far.");
			}
		});

		ActionsManager.getManager().submit(getId(13), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getDeathCount() == 0 ? "no" : player.getDeathCount()) + " " + (player.getDeathCount() == 1 ? "death" : "deaths") + " so far.");
			}
		});

		ActionsManager.getManager().submit(getId(14), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				String kdr = "" + player.getKDR();
				kdr = kdr.replace(".", ",");
				player.forceMessage("My kill/deathrating is " + kdr + ".");
			}
		});

		ActionsManager.getManager().submit(getId(15), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("My PvP rating is " + player.getPoints().getEloRating() + ". " + (player.getPoints().getEloRating() == player.getPoints().getEloPeak() ? "This is also my best PvP rating ever." : "My best PvP rating ever was " + player.getPoints().getEloPeak() + "."));
			}
		});

		ActionsManager.getManager().submit(getId(18), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getPoints().getPkPoints() == 0 ? "no" : player.getPoints().getPkPoints()) + " " + (player.getPoints().getPkPoints() == 1 ? "PK point" : "PK points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(19), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getPoints().getVotingPoints() == 0 ? "no" : player.getPoints().getVotingPoints()) + " " + (player.getPoints().getVotingPoints() == 1 ? "voting point" : "voting points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(20), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage(player.getPoints().getDonatorPointsBought() == 0 ? "I have never bought any donator points." : "I bought " + player.getPoints().getDonatorPointsBought() + " donator points and " + (player.getPoints().getDonatorPointsBought() == player.getPoints().getDonatorPoints() ? "still have them all." : "still have " + player.getPoints().getDonatorPoints() + "of them left."));
			}
		});

		ActionsManager.getManager().submit(getId(21), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage("I have " + (player.getBountyHunter().getKills() == 0 ? "no" : player.getBountyHunter().getKills()) + " " + (player.getBountyHunter().getKills() == 1 ? "BH point" : "BH points") + ".");
			}
		});

		ActionsManager.getManager().submit(getId(26), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getPermExtraData().put("bhon", player.getBountyHunter().switchEnabled());
				player.getQuestTab().sendBHEnabled();
			}
		});

		ActionsManager.getManager().submit(getId(27), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.sendMessage("Not implemented yet.");
			}
		});

		ActionsManager.getManager().submit(getId(30), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getYelling().setYellEnabled(!player.getYelling().isYellEnabled());
				player.getQuestTab().sendYellEnabled();
			}
		});

		ActionsManager.getManager().submit(getId(31), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getTrivia().setEnabled(!player.getTrivia().isEnabled());
				player.getQuestTab().sendTriviaEnabled();
			}
		});

		ActionsManager.getManager().submit(getId(32), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getYelling().setYellColoursEnabled(!player.getYelling().isYellColoursEnabled());
				player.getQuestTab().sendYellColoursEnabled();
			}
		});

		ActionsManager.getManager().submit(getId(33), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.xpLock = !player.xpLock;
				player.getQuestTab().sendExpLockEnabled();
			}
		});
    }
}
