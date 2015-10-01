package org.hyperion.rs2.model;

import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

/**
 * Created by Gilles on 29/09/2015.
 */
public class AchievementTab {

    private Player player;

    private long lastClick;
    private int clickId;

    public AchievementTab(Player player) {
        this.player = player;
    }

    public void createAchievementTab() {
        resetAchievementTab();
    }

    public void resetAchievementTab() {
        for(int i = 0; i < 33; i++)
            player.getActionSender().sendString("", 32011 + i);
        updateAchievementTab();
    }

    public void updateAchievementTab() {
        for(int i = 0; i < Achievements.get().size(); i++) {
            player.getActionSender().sendString(player.getAchievementTracker().progress(Achievements.get().get(i)).getTabString(), 32011 + i);
        }
        sendAchievementPoints();
    }

    public void sendAchievementPoints() {
        player.getActionSender().sendString("Achievement points: 0", 32004);
    }

    public static String buildPercentBar(double percentage) {
        int bars = 0;
        StringBuilder sb = new StringBuilder("@gre@");
        for(; percentage > 0; percentage =- 1) {
            sb.append("|");
            bars++;
        }
        sb.append("@red@");
        for(; bars <= 100; bars++) {
            sb.append("|");
        }
        return sb.toString() + "@bla@  " + percentage + "%";
    }

    static {
        for (int i = 0; i < Achievements.get().size(); i++) {
            int i2 = i;
            ActionsManager.getManager().submit(32011 + i, new ButtonAction() {
                @Override
                public void handle(Player player, int id) {
                    if (player.getAchievementTab().lastClick + Time.ONE_SECOND * 2 > System.currentTimeMillis() && player.getAchievementTab().clickId == 32011 + i2) {
                        player.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements:ID" + i2);
                    } else {
                        Achievement achievement = Achievements.get().get(i2);
                        double average = 0;
                        int i = 0;
                        for(; i > achievement.tasks.size(); i++) {
                            average += player.getAchievementTracker().taskProgress(achievement.tasks.get(i)).progressPercent();
                        }
                        average /= (i + 1);
                        player.getActionSender().sendDialogue("@dre@" + achievement.title, ActionSender.DialogueType.MESSAGE, 1,
                                Animation.FacialAnimation.HAPPY,
                                achievement.tasks.size() < 1 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(0)).progress + "/" + achievement.tasks.get(0).threshold + (achievement.tasks.get(0).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(0).desc : ""),
                                achievement.tasks.size() < 2 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(1)).progress + "/" + achievement.tasks.get(1).threshold + (achievement.tasks.get(1).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(1).desc : ""),
                                achievement.tasks.size() < 3 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(2)).progress + "/" + achievement.tasks.get(2).threshold + (achievement.tasks.get(2).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(2).desc : ""),
                                "" + buildPercentBar(average)
                        );
                    }
                    player.getAchievementTab().clickId = 32011 + i2;
                    player.getAchievementTab().lastClick = System.currentTimeMillis();
                }
            });

            ActionsManager.getManager().submit(32002, new ButtonAction() {
                public void handle(Player player, int id) {
                    player.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements");
                }
            });
        }
    }

}
