package org.hyperion.rs2.model;

import java.io.BufferedReader;

import org.hyperion.Server;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.util.Misc;

/**
 * @author Arsen Maxyutov.
 */
public class QuestTab {
	
	public static BufferedReader WORLD_CUP;
	/**
	 * Grab world cup scores from http://www.livescore.com/worldcup/ and
	 * @return Time, team1 t1score-t2score team2
	 */
	public static String getWorldCupScores() {
		/*
		BufferedReader getCup = null;
		StringBuilder finished = new StringBuilder();
		String raw = null;
		try {
		getCup = new BufferedReader(new InputStreamReader(new URL("http://www.livescore.com/worldcup/").openConnection().getInputStream()));
		while((raw = getCup.readLine()) != null) {
			if(raw.contains("<td class=\"tl\">")) {
				String time = findVar(raw,"<td class=\"fd\">");
				String teamOne = findVar(raw,"<td class=\"tl\">");
				String teamTwo = findVar(raw, "<td class=\"tr\">");
				String matchScore = findVar(raw, "<td class=\"fs\">");
				finished.append(time = filterTime(time));
				finished.append(teamOne).append(matchScore).append(teamTwo);
			}
		}
		}catch(IOException e) {
			return "Disabled, get the scores at @blu@http://livescore.com/worldcup/";
		} finally {
			if(getCup != null)
				try {
					getCup.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
		}
		*/
		return "Disabled";
	}
	/**
	 * Interpret the meaning of passed in "time"
	 */
	private static String filterTime(String time) {
		StringBuilder finished = new StringBuilder();
		if(time != null) {
			if(time.contains("'"))
				finished.append(time).append(":");
			else if(time.contains(":"))
				finished.append("Starts at: ").append(time);
			else if(time.equalsIgnoreCase("FT"))
				finished.append("Full-time: ");
		}
		return finished.toString();
	}
	
	/**
	 * Filter string inside from given and constant key
	 */
	private static String findVar(String raw, String key) {
		String start = raw.substring(raw.indexOf(key));
		return start.substring(key.length(), start.indexOf("<"));
	}
	
	/**
	 * The Ids used to clear the Quest tab.
	 */
	public static int[] QUEST_TAB_IDS = {
			7332, 7333, 7334, 7336, 7383, 7339, 7338, 7340, 7346, 7341, 7342, 7337, //Used ones
			7343, 7335, 7344,
			7345, 7347, 7348, 682, 12772, 673, 673, 7352, 17510, 7353, 12129,
			8438, 12852, 15841, 7354, 7355, 7356, 8679, 7459, 16149, 6987,
			7357, 12836, 7358, 7359, 14169, 10115, 14604, 7360, 12282, 13577,
			12839, 7361, 16128, 11857, 7362, 7363, 7364, 10125, 4508, 18517,
			11907, 7365, 7366, 7367, 13389, 15487, 7368, 11132, 7369, 12389,
			13974, 6027, 7370, 8137, 7371, 12345, 7372, 8115, 10135, 18684,
			15499, 18306, 668, 8576, 12139, 14912, 7374, 7373, 8969, 15352,
			7375, 7376, 15098, 15592, 249, 1740, 15235, 3278, 664, 7378, 6518,
			7379, 7380, 7381, 11858, 191, 9927, 6024, 7349, 7350, 7351, 13356};

	private static int max_index = 0;

	private static int getId(int index) {
		if(index > max_index)
			max_index = index;
		return QUEST_TAB_IDS[index];
	}

	private Player player;

	public QuestTab(Player player) {
		this.player = player;
	}

	public void sendPlayercount() {
		int players = World.getWorld().getPlayers().size();
		player.getActionSender().sendString("@gre@Players Online : " + players, 640);
	}

	public void sendUptime() {
		if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
			player.getActionSender().sendString("@or2@Uptime: @gre@" + Server.getUptime(), getId(0));

	}

	public void sendRank() {
		player.getActionSender().sendString("@or2@Rank: @gre@" + player.getQuestTabRank(), getId(1));
	}

	public void sendBonusSkill() {
		player.getActionSender().sendString("@or2@Bonus Skill: @gre@" + Misc.getSkillName(Skills.BONUS_SKILL), getId(2));
	}

	public void sendElo() {
		String eloString = "@or2@Elo: @gre@" + player.getPoints().getEloRating();
		eloString = eloString.replaceAll("Elo", "PvP Rating");
		player.getActionSender().sendString(eloString, getId(3));
	}

	public void sendKDR() {
		player.getActionSender().sendString("@or2@Kdr: @gre@" + player.getKDR(), getId(4));
	}

	public void sendEmptyString() {
		player.getActionSender().sendString("@or2@Bounty Hunter: @gre@" + (player.getBountyHunter().isEnabled() ? "On" : "Off"), getId(15));
	}

	public void sendPkPoints() {
		player.getActionSender().sendString("@or2@" + Server.NAME + " Points: @gre@" + player.getPoints().getPkPoints(), getId(6));
	}

	public void sendDonatePoints() {
		player.getActionSender().sendString("@or2@Donator Points: @gre@" + player.getPoints().getDonatorPoints(), getId(7));
	}

	public void sendVotePoints() {
		player.getActionSender().sendString("@or2@Voting Points: @gre@" + player.getPoints().getVotingPoints(), getId(8));
	}

	public void sendHonorPoints() {
		player.getActionSender().sendString("@or2@Honor Points: @gre@" + player.getPoints().getHonorPoints(), getId(9));
	}

	public void sendExplock() {
		player.getActionSender().sendString("@or2@Exp Lock: @gre@" + player.xpLock, getId(10));
	}

	public void sendYellEnabled() {
		player.getActionSender().sendString("@or2@Yell enabled: @gre@" + player.getYelling().isYellEnabled(), getId(11));
	}

	public void sendTrivia() {
		player.getActionSender().sendString("@or2@Trivia enabled: @gre@" + player.getTrivia().isEnabled(), getId(12));
	}

	public void sendYellColoursEnabled() {
		player.getActionSender().sendString("@or2@Yell Colours: @gre@" + player.getYelling().isYellColoursEnabled(), getId(13));
	}

	public void sendItemsKept() {
		player.getActionSender().sendString("@or2@Items Kept on Death", getId(14));
	}
	
	public void sendBHTarget() {
		if(player.getBountyHunter().getTarget() != null) {
			player.getActionSender().sendString("Target: @gre@"+player.getBountyHunter().getTarget().getSafeDisplayName(), 36502);
		} else {
			player.getActionSender().sendString("Target: @gre@None", 36502);
		}
        player.getActionSender().sendString("", getId(5));
    }
	
	public void sendBHKills() {
		player.getActionSender().sendString("@or2@BH Points: @gre@"+player.getBountyHunter().getKills(), getId(16));
	}
	
	public void sendBHPerks() {
		player.getActionSender().sendString("@whi@"+player.getBHPerks().toString(), getId(17));
	}

	public void sendRankInfo() {
		int i = 18;
		for(int r = i; r < i + 30; r++)
			player.getActionSender().sendString("", getId(r));

		player.getActionSender().sendString("@gre@Current Rank Info.", getId(i++));
		++i;
		++i;
		player.getActionSender().sendString("@gre@Primary Rank:", getId(i++));
		player.getActionSender().sendString("@whi@"+Rank.getPrimaryRank(player).toString(), getId(i++));
		++i;
		player.getActionSender().sendString("@gre@Abilities:", getId(i++));
		for(Rank rank : Rank.values()) {
			if(Rank.hasAbility(player, rank)){
				player.getActionSender().sendString("@whi@"+rank.toString()+(Rank.isAbilityToggled(player, rank) ? "" : " [I]"), getId(i++));
			}
		}
	}

	public void sendAllInfo() {
		player.getActionSender().sendString("Settings & Information", 663);
		player.getQuestTab().sendPlayercount();
		player.getQuestTab().sendUptime();
		player.getQuestTab().sendRank();
		player.getQuestTab().sendBonusSkill();
		player.getQuestTab().sendElo();
		player.getQuestTab().sendKDR();
		player.getQuestTab().sendEmptyString();
		player.getQuestTab().sendPkPoints();
		player.getQuestTab().sendDonatePoints();
		player.getQuestTab().sendVotePoints();
		player.getQuestTab().sendHonorPoints();
		player.getQuestTab().sendYellEnabled();
		player.getQuestTab().sendExplock();
		player.getQuestTab().sendTrivia();
		player.getQuestTab().sendYellColoursEnabled();
		player.getQuestTab().sendItemsKept();
		player.getQuestTab().sendRankInfo();
		player.getQuestTab().sendBHKills();
		player.getQuestTab().sendBHTarget();
		player.getQuestTab().sendBHPerks();
		sendEmptyQuestTabStrings();
	}

	public void sendEmptyQuestTabStrings() {
		for(int i = max_index + 1; i < QUEST_TAB_IDS.length; i++) {
			player.getActionSender().sendString("", QUEST_TAB_IDS[i]);
		}
	}


	static {

		for(int i = 25; i < 25 + Rank.values().length; i++) {
			final int i2 = i;
			ActionsManager.getManager().submit(getId(i), new ButtonAction() {
				@Override
				public void handle(Player player, int id) {
					int index = 25;
					for(Rank rank : Rank.values()) {
						if(Rank.hasAbility(player, rank)){
							System.out.println(index+"_"+rank);
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

		ActionsManager.getManager().submit(getId(22), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getQuestTab().sendRank();
				player.getQuestTab().sendRankInfo();
			}
		});
		
		ActionsManager.getManager().submit(getId(15), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.sendMessage("You just set your bounty hunter to @blu@"+(player.getBountyHunter().switchEnabled() ? "On" : "Off"));
				player.getQuestTab().sendEmptyString();
			}
		});
		
		/*
		 * Telet to bounty hunter target!
		 */
		
		ActionsManager.getManager().submit(getId(5), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {

			}
		});
		
		/**
		 * Check perks!!
		 */
		
		ActionsManager.getManager().submit(getId(17), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.sendMessage("Special Perk: Increase special attack after a kill", "Veng Reduction: Reduce time for next veng", "Prayer Leech: Leech opponents player on hit (stacks w/ pray)");
			}
		});
		
		ActionsManager.getManager().submit(getId(14), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getActionSender().openItemsKeptOnDeathInterface(player);
			}
		});
		ActionsManager.getManager().submit(getId(0), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				if(player.lastScoreCheck > System.currentTimeMillis()) {
					player.sendMessage("@red@You can only check the score every 30 sconds!");
					return;
				}
				player.lastScoreCheck = System.currentTimeMillis() + 30000;
				player.getActionSender().sendMessage("@blu@Featured Score: @bla@"+getWorldCupScores()+"");
			}

		});
		ActionsManager.getManager().submit(getId(3), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				String message = "My elo is : " + player.getPoints().getEloRating()+", Peak rating: "+player.getPoints().getEloPeak();
				message = message.replaceAll("elo", "PvP Rating");
				player.forceMessage(message);
			}

		});

		ActionsManager.getManager().submit(getId(4), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				String message = "My kdr is : " + player.getKDR() + ", " + player.getKillCount() + "/" + player.getDeathCount();
				player.forceMessage(message);
			}

		});

		ActionsManager.getManager().submit(getId(6), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.forceMessage(String.format("I have %,d Pk Points", player.getPoints().getPkPoints()));
			}

		});

		ActionsManager.getManager().submit(getId(7), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
                player.forceMessage(String.format("I have %,d Donator Points. Bought %,d (Roughly $%,d)", player.getPoints().getDonatorPoints(), player.getPoints().getDonatorPointsBought(), player.getPoints().getDonatorPointsBought() / 100));
			}

		});

		ActionsManager.getManager().submit(getId(8), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
                player.forceMessage(String.format("I have %,d Voting Points", player.getPoints().getVotingPoints()));
			}

		});

		ActionsManager.getManager().submit(getId(9), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
                player.forceMessage(String.format("I have %,d Honor Points", player.getPoints().getHonorPoints()));
			}

		});

		ActionsManager.getManager().submit(getId(10), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.xpLock = ! player.xpLock;
				player.getQuestTab().sendExplock();
				player.getActionSender().sendMessage(
						"@blu@Your Exp lock is now: @red@" + player.xpLock);
			}

		});


		ActionsManager.getManager().submit(getId(11), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getYelling().setYellEnabled(! player.getYelling().isYellEnabled());
				player.getQuestTab().sendYellEnabled();
				player.getActionSender().sendMessage(
						"@blu@Your yell messages are now: @red@"
								+ (player.getYelling().isYellEnabled() ? "Enabled" : "Disabled"));
			}

		});
		ActionsManager.getManager().submit(getId(12), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getTrivia().change();
				player.getQuestTab().sendTrivia();
				player.getActionSender().sendMessage(
						"@blu@Your trivia is now: @red@"
								+ (player.getTrivia().isEnabled() ? "Enabled"
								: "Disabled"));
			}

		});

		ActionsManager.getManager().submit(getId(13), new ButtonAction() {
			@Override
			public void handle(Player player, int id) {
				player.getYelling().setYellColoursEnabled(! player.getYelling().isYellColoursEnabled());
				player.getActionSender().sendMessage(
						"@blu@Yell colours are now: @red@"
								+ (player.getYelling().isYellColoursEnabled() ? "Enabled" : "Disabled"));
				player.getQuestTab().sendYellColoursEnabled();
			}

		});
	}
}
