package org.hyperion.rs2.model;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.util.Misc;

/**
 * @author Arsen Maxyutov.
 * @author Glis                29/07/2015
 */
public class QuestTab {

    private int max_index = 0;

    private int getId(int index) {
        index += 33011;
        if (index > max_index)
            max_index = index;
        return index;
    }

    private static int getClickId(int index) {
        index -= 32525;
        return index;
    }

    private int getNextIndex() {
        max_index++;
        return max_index;
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
        sendBonusSkill();
        sendUptime();
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
        sendBHEnabled();
        sendBHPerks();
        sendYellEnabled();
        sendYellTitlesEnabled();
        sendPkMessagesEnabled();
        sendStaffMessagesEnabled();
        sendLootMessagesEnabled();
        sendParticlesEnabled();
        sendTitlesEnabled();
        sendExpLockEnabled();
        sendRankInfo();
        fillQuestTab();
    }

    public void resetQuestTab() {
        for (int i = 0; i < 200; i++) {
            player.getActionSender().sendString("", 33011 + i);
            player.getActionSender().sendTooltip(33011 + i, "");
        }
        player.getActionSender().sendString("@yel@" + Configuration.getString(Configuration.ConfigurationObject.NAME), 640);
        player.getActionSender().sendString("@yel@Server Information ", getId(0));
        player.getActionSender().sendFont(getId(0), 2);
        player.getActionSender().sendString("", getId(5));
        player.getActionSender().sendString("@yel@Pk Information", getId(6));
        player.getActionSender().sendFont(getId(6), 2);
        player.getActionSender().sendString("", getId(16));
        player.getActionSender().sendString("@yel@Ingame Points", getId(13));
        player.getActionSender().sendFont(getId(13), 2);
        player.getActionSender().sendString("", getId(18));
        player.getActionSender().sendString("@yel@Bounty hunter", getId(19));
        player.getActionSender().sendFont(getId(19), 2);
        player.getActionSender().sendString("", getId(23));
        player.getActionSender().sendString("@yel@Locks", getId(24));
        player.getActionSender().sendFont(getId(24), 2);
        player.getActionSender().sendString("", getId(32));
    }

    public void sendPlayerCount() {
        int players = (int) (World.getPlayers().size() * Configuration.getDouble(Configuration.ConfigurationObject.PLAYER_MULTIPLIER));
        int id = getId(1);    //Easier to adjust later

        player.getActionSender().sendString("@or1@Players online: @gre@" + players, id);
        player.getActionSender().sendTooltip(id, "Players online");
    }

    public void sendStaffCount() {
        int id = getId(2);
        int staffOnline = StaffManager.getOnlineStaff().size();
        player.getActionSender().sendString("@or1@Staff online: " + (staffOnline == 0 ? "@red@" : "@gre@") + staffOnline, id);
        player.getActionSender().sendTooltip(id, "Staff online");
    }

    public void sendBonusSkill() {
        int id = getId(3);
        player.getActionSender().sendString("@or1@Bonus skill: @gre@"+ Misc.getSkillName(Skills.BONUS_SKILL), id);
        //player.getActionSender().sendString("@or1@Bonus skill: @gre@All skills", id);
        player.getActionSender().sendTooltip(id, "Bonus skill");
    }

    public void sendUptime() {
        int id = getId(4);
        player.getActionSender().sendString((Rank.hasAbility(player, Rank.ADMINISTRATOR) && Events.eventName == "") ? "@or1@Uptime: @gre@" + Server.getUptime() : (Events.eventName == "" ? "" : "@or1@Event: @gre@" + (Events.eventName.length() > 20 ? Events.eventName.substring(0, 20) + "." : Events.eventName)), id);
        player.getActionSender().sendTooltip(id, (Events.eventName == "" ? "" : "Teleport to event"));
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
        int id = getId(20);
        player.getActionSender().sendString("@or1@BH points: @gre@" + player.getBountyHunter().getKills(), id);
        player.getActionSender().sendTooltip(id, "Yell BH points");
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

    public void sendYellTitlesEnabled() {
        int id = getId(26);
        player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledYellTitles") ? "Enable" : "Disable") + " yell titles", id);
        player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledYellTitles") ? "Enable" : "Disable") + " yell titles");
    }

    public void sendPkMessagesEnabled() {
        int id = getId(28);
        player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledPkMessages") ? "Enable" : "Disable") + " PK messages", id);
        player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledPkMessages") ? "Enable" : "Disable") + " PK messages");
    }

    public void sendStaffMessagesEnabled() {
        int id = getId(29);
        player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "Enable" : "Disable") + " staff login", id);
        player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "Enable" : "Disable") + " staff login");
    }

    public void sendLootMessagesEnabled() {
        int id = getId(30);
        player.getActionSender().sendString("@or1@" + (player.getPermExtraData().getBoolean("disabledLootMessages") ? "Enable" : "Disable") + " loot messages", id);
        player.getActionSender().sendTooltip(id, (player.getPermExtraData().getBoolean("disabledLootMessages") ? "Enable" : "Disable") + " loot messages");
    }

    public void sendParticlesEnabled() {
        int id = getId(31);
        player.getActionSender().sendString("@or1@Toggle particles", id);
        player.getActionSender().sendTooltip(id, "Toggle particles");
    }

    public void sendTitlesEnabled() {
        int id = getId(32);
        player.getActionSender().sendString("@or1@Toggle player titles", id);
        player.getActionSender().sendTooltip(id, "Toggle player titles");
    }

    public void sendExpLockEnabled() {
        int id = getId(33);
    }

    public void sendRankInfo() {
        boolean hasRank = false;
        int i = 36;
        for (Rank rank : Rank.values()) {
            if (Rank.hasAbility(player, rank)) {
                player.getActionSender().sendString((Rank.getPrimaryRank(player).equals(rank) ? "@gre@" : "@or1@") + rank.toString(), getId(i));
                player.getActionSender().sendTooltip(getId(i), (Rank.getPrimaryRank(player).equals(rank) ? "" : "Set rank to " + rank.toString()));
                i++;
                if (!hasRank && rank != Rank.PLAYER)
                    hasRank = true;
            } else {
                player.getActionSender().sendString("", getId(i));
                player.getActionSender().sendTooltip(getId(i), "");
            }
        }
        if(!hasRank)
            player.getActionSender().sendString("", getId(36));
        player.getActionSender().sendString(!hasRank ? "" : "@yel@Available ranks", getId(35));
        player.getActionSender().sendFont(getId(35), 2);
        player.getActionSender().sendScrollbarLength(33010, (((5 + (hasRank ? 1 : 0)) * 16) + ((i - 5) * 12)) + 8);
    }

    public void fillQuestTab() {
        for (int i = getNextIndex(); i < 33211; i++) {
            player.getActionSender().sendString("", i);
        }
    }

    static {
        /*
        for(int i = 0; i < Rank.values().length; i++) {
            final int i2 = i;
            ActionsManager.getManager().submit(getClickId(36 + i), new ButtonAction() {
                @Override
                public void handle(Player player, int id) {
                    List<Rank> playerRanks = new ArrayList<>();
                    for(Rank rank : Rank.values()) {
                        if(Rank.hasAbility(player, rank))
                            playerRanks.add(rank);
                    }
                    if(i2 >= playerRanks.size())
                        return;
                    if(Rank.hasAbility(player, playerRanks.get(i2))) {
                        player.setPlayerRank(Rank.setPrimaryRank(player, playerRanks.get(i2)));
                        player.getQuestTab().sendRankInfo();
                    }
                }
            });
        }

        ActionsManager.getManager().submit(getClickId(1), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getActionSender().openPlayersInterface();
            }
        });

        ActionsManager.getManager().submit(getClickId(2), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                List<Player> onlineStaff = StaffManager.getOnlineStaff();
                player.getActionSender().sendMessage("Staff online: @dre@" + onlineStaff.size());
                for (Player staffMember : onlineStaff) {
                    final Rank rank = Rank.getPrimaryRank(staffMember);
                    player.getActionSender().sendMessage(String.format(
                            "[%s%s@bla@] - %s%s",
                            rank.getYellColor(), staffMember.display == null || staffMember.display.isEmpty() ? staffMember.getName() : staffMember.display,
                            rank.getYellColor(), rank));
                }
            }
        });

        ActionsManager.getManager().submit(getClickId(3), new ButtonAction() {
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

        ActionsManager.getManager().submit(getClickId(4), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                if (Events.eventName != "") {
                    Events.joinEvent(player);
                }
            }
        });

        ActionsManager.getManager().submit(getClickId(7), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage("I have " + (player.getKillCount() == 0 ? "no" : player.getKillCount()) + " " + (player.getKillCount() == 1 ? "kill" : "kills") + " so far.");
            }
        });

        ActionsManager.getManager().submit(getClickId(8), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage("I have " + (player.getDeathCount() == 0 ? "no" : player.getDeathCount()) + " " + (player.getDeathCount() == 1 ? "death" : "deaths") + " so far.");
            }
        });

        ActionsManager.getManager().submit(getClickId(9), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                double kdr = player.getKDR();
                player.forceMessage(String.format("My kill/deathratio is %.2f.", kdr));
            }
        });

        ActionsManager.getManager().submit(getClickId(11), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getActionSender().openItemsKeptOnDeathInterface(player);
            }
        });

        ActionsManager.getManager().submit(getClickId(10), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage("My PvP rating is " + player.getPoints().getEloRating() + ". " + (player.getPoints().getEloRating() == player.getPoints().getEloPeak() ? "This is also my best PvP rating ever." : "My best PvP rating ever was " + player.getPoints().getEloPeak() + "."));
            }
        });

        ActionsManager.getManager().submit(getClickId(14), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage("I have " + (player.getPoints().getPkPoints() == 0 ? "no" : player.getPoints().getPkPoints()) + " " + (player.getPoints().getPkPoints() == 1 ? "ArteroPK point" : "ArteroPK points") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(15), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                DialogueManager.openDialogue(player, 540);
            }
        });

        ActionsManager.getManager().submit(getClickId(16), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage(player.getPoints().getDonatorPointsBought() == 0 ? "I have never bought any donator points." : "I bought " + player.getPoints().getDonatorPointsBought() + " donator points for $" + player.getPoints().getDonatorPointsBought() / 100 + " and " + (player.getPoints().getDonatorPointsBought() == player.getPoints().getDonatorPoints() ? "still have them all." : "still have " + player.getPoints().getDonatorPoints() + " of them left."));
            }
        });

        ActionsManager.getManager().submit(getClickId(17), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                DialogueManager.openDialogue(player, 530);
            }
        });

        ActionsManager.getManager().submit(getClickId(19), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.forceMessage("I have " + (player.getBountyHunter().getKills() == 0 ? "no" : player.getBountyHunter().getKills()) + " " + (player.getBountyHunter().getKills() == 1 ? "BH point" : "BH points") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(21), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("bhon", player.getBountyHunter().switchEnabled());
                player.getQuestTab().sendBHEnabled();
                player.sendMessage("Bounty hunter is now " + (player.getPermExtraData().getBoolean("bhon") ? "enabled" : "disabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(22), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                int perk1 = 0;
                int perk2 = 0;
                int perk3 = 0;
                for (int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.SPEC_RESTORE); i++) {
                    perk1++;
                }
                for (int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.VENG_REDUCTION); i++) {
                    perk2++;
                }
                for (int i = -1; i < player.getBHPerks().hasPerk(BountyPerks.Perk.PRAY_LEECH); i++) {
                    perk3++;
                }
                player.sendMessage("@dre@" + (perk1 == 0 ? "" : "(Level " + perk1 + ") ") + "Special perk:@bla@ Increase special attack after a kill.");
                player.sendMessage("@dre@" + (perk2 == 0 ? "" : "(Level " + perk2 + ") ") + "Veng reduction:@bla@ Reduce cooldown on vengeance.");
                player.sendMessage("@dre@" + (perk3 == 0 ? "" : "(Level " + perk3 + ") ") + "Prayer leech:@bla@ Leech opponent's prayer on hit.");
            }
        });

        ActionsManager.getManager().submit(getClickId(25), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("disabledYell", !player.getPermExtraData().getBoolean("disabledYell"));
                player.getQuestTab().sendYellEnabled();
                player.sendMessage("Yell is now " + (player.getPermExtraData().getBoolean("disabledYell") ? "disabled" : "enabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(26), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("disabledYellTitles", !player.getPermExtraData().getBoolean("disabledYellTitles"));
                player.getQuestTab().sendYellTitlesEnabled();
                player.sendMessage("Yell titles are now " + (player.getPermExtraData().getBoolean("disabledYellTitles") ? "disabled" : "enabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(27), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getTrivia().setEnabled(!player.getTrivia().isEnabled());
                player.getQuestTab().sendTriviaEnabled();
                player.sendMessage("Trivia is now " + (player.getTrivia().isEnabled() ? "enabled" : "disabled") + ".");
                if (player.getTrivia().isEnabled())
                    player.sendMessage("To answer, simply use ::answer ANSWER.");
            }
        });

        ActionsManager.getManager().submit(getClickId(28), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("disabledPkMessages", !player.getPermExtraData().getBoolean("disabledPkMessages"));
                player.getQuestTab().sendPkMessagesEnabled();
                player.sendMessage("Pk messages are now " + (player.getPermExtraData().getBoolean("disabledPkMessages") ? "disabled" : "enabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(29), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("disabledStaffMessages", !player.getPermExtraData().getBoolean("disabledStaffMessages"));
                player.getQuestTab().sendStaffMessagesEnabled();
                player.sendMessage("Staff login messages are now " + (player.getPermExtraData().getBoolean("disabledStaffMessages") ? "disabled" : "enabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(30), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPermExtraData().put("disabledLootMessages", !player.getPermExtraData().getBoolean("disabledLootMessages"));
                player.getQuestTab().sendLootMessagesEnabled();
                player.sendMessage("Loot messages are now " + (player.getPermExtraData().getBoolean("disabledLootMessages") ? "disabled" : "enabled") + ".");
            }
        });

        ActionsManager.getManager().submit(getClickId(31), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.sendMessage("script-particles");
            }
        });

        ActionsManager.getManager().submit(getClickId(32), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.sendMessage("script-titles");
            }
        });

        ActionsManager.getManager().submit(getClickId(33), new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.xpLock = !player.xpLock;
                player.getQuestTab().sendExpLockEnabled();
                player.sendMessage("Experience lock is now " + (player.xpLock ? "on" : "off") + ".");
            }
        });

        ActionsManager.getManager().submit(-32534, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.sendMessage("l4unchur13 https://www.facebook.com/ArteroPk-841836079182865/timeline/");
            }
        });

        ActionsManager.getManager().submit(-32533, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.sendMessage("l4unchur13 https://twitter.com/arteropk1");
            }
        });*/
    }
}
