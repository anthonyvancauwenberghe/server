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
    private int page = 0;

    public AchievementTab(Player player) {
        this.player = player;
    }

    public void createAchievementTab() {
        resetAchievementTab();
    }

    public void resetAchievementTab() {
        for(int i = 0; i < 33; i++)
            player.getActionSender().sendString("", 28026 + i);
        updateAchievementTab();
    }

    public void updateAchievementTab() {
        int j = 0;
        for(int i = 0 + (page * 33); i < ((page + 1) * 33); i++) {
            if(Achievements.get().size() > i) {
                player.getActionSender().sendString(player.getAchievementTracker().progress(Achievements.get().get(i)).getTabString(), 28026 + j);
            } else {
                player.getActionSender().sendString("", 28026 + j);
            }
            j++;
        }
        if(page != 0)
            player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Previous page"), 28060);
        else
            player.getActionSender().sendString("", 28060);
        if(page != Achievements.get().size() / 33)
            player.getActionSender().sendString("@or1@" + Misc.centerQuestTab("Next page"), 28061);
        else
            player.getActionSender().sendString("", 28061);
        player.getActionSender().sendString("Page " + (page + 1), 28023);
    }

    public void nextPage() {
        if(page == Achievements.get().size() / 33)
            return;
        page++;
        updateAchievementTab();
    }

    public void previousPage() {
        if(page == 0)
            return;
        page--;
        updateAchievementTab();
    }

    static {
        for (int i = 0; i < 36; i++) {
            int i2 = i;
            ActionsManager.getManager().submit(28026 + i, new ButtonAction() {
                @Override
                public void handle(Player player, int id) {
                    if(i2 < 33) {
                        if (player.getAchievementTab().lastClick + Time.ONE_SECOND * 2 > System.currentTimeMillis() && player.getAchievementTab().clickId == 28026 + i2) {
                            player.sendMessage("Sent to wiki");
                        } else {
                            player.getActionSender().sendDialogue(Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).title, ActionSender.DialogueType.MESSAGE, 1,
                                    Animation.FacialAnimation.HAPPY,
                                    "" + (Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.size() > 0 ? Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.get(0).desc : ""), //Line 1
                                    "" + (Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.size() > 1 ? Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.get(1).desc : ""), //Line 2
                                    "" + (Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.size() > 2 ? Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.get(2).desc : ""), //Line 3
                                    "" + (Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.size() > 3 ? Achievements.get().get(i2 + (player.getAchievementTab().page * 33)).tasks.get(3).desc : "") //Line 4
                            );
                        }
                        player.getAchievementTab().clickId = 28026 + i2;
                        player.getAchievementTab().lastClick = System.currentTimeMillis();
                    } else if(i2 == 34) {
                        player.getAchievementTab().previousPage();
                    } else if(i2 == 35) {
                        player.getAchievementTab().nextPage();
                    }
                }
            });
        }
    }

}
