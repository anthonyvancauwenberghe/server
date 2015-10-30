package org.hyperion.rs2.model;

import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Gilles on 29/09/2015.
 */
public class AchievementTab {

    private Player player;

    private long lastClick;
    private int clickId;
    private final static int START_INDEX = 32011;

    public enum difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    static List<Achievement> easy = new LinkedList<>();
    static List<Achievement> medium = new LinkedList<>();
    static List<Achievement> hard = new LinkedList<>();
    static Map<Integer, List> achievements = new HashMap<>();

    public AchievementTab(Player player) {
        this.player = player;
    }

    public void createAchievementTab() {
        resetAchievementTab();
    }

    public void resetAchievementTab() {
        updateAchievementTab();
    }

    public void updateAchievementTab() {
        sendEasyAchievements();
        sendMediumAchievements();
        sendHardAchievements();
        player.getActionSender().sendScrollbarLength(32010, (Achievements.get().size() + difficulty.values().length + 1) * 14 );
        sendAchievementCompleted();
    }

    public void sendAchievements(String difficulty) {
        List<Achievement> currentAchievements = achievements.get(AchievementTab.difficulty.valueOf(difficulty.toUpperCase()).ordinal());
        int otherAchievements = 0;
        for(int i = 0; i < AchievementTab.difficulty.valueOf(difficulty.toUpperCase()).ordinal(); i++)
            if(achievements.get(i) != currentAchievements)
                otherAchievements += achievements.get(i).size() + 2;

        int startIndex = otherAchievements;
        if(startIndex > 0) {
            player.getActionSender().sendString(START_INDEX + startIndex - 1, "");
            player.getActionSender().sendTooltip(START_INDEX + startIndex - 1, "");
        }
        player.getActionSender().sendString(START_INDEX + startIndex, "@or1@" + Misc.ucFirst(difficulty));
        player.getActionSender().sendFont(START_INDEX + startIndex++, 2);
        for(Achievement achievement : currentAchievements) {
            player.getActionSender().sendString(player.getAchievementTracker().progress(achievement).getTabString(), START_INDEX + startIndex);
            player.getActionSender().sendTooltip(START_INDEX + startIndex++, achievement.title);
        }
    }

    public void sendEasyAchievements() {
        sendAchievements("easy");
    }

    public void sendMediumAchievements() {
        sendAchievements("medium");
    }

    public void sendHardAchievements() {
        sendAchievements("hard");
    }

    public void sendAchievementCompleted() {
        int finished = 0;
        for(Achievement achievement : Achievements.get().values())
            if(player.getAchievementTracker().progress(achievement.id).finished())
                finished++;
        player.getActionSender().sendString("Achievement completed: " + finished + "/" + Achievements.get().size(), 32004);
    }

    public static String buildPercentBar(double percentage) {
        int rounded = (int)percentage;
        int bars = 0;
        StringBuilder sb = new StringBuilder("@gre@");
        for(; rounded > 0; rounded -= 1) {
            sb.append("|");
            bars++;
        }
        sb.append("@red@");
        for(; bars <= 99; bars++) {
            sb.append("|");
        }
        return sb.toString() + "@bla@  " + percentage + "%";
    }

    static {
        try {
            for (Achievement achievement : Achievements.get().values()) {
                if (achievement.difficulty == Achievement.Difficulty.EASY) {
                    easy.add(achievement);
                }
                if (achievement.difficulty == Achievement.Difficulty.MEDIUM) {
                    medium.add(achievement);
                }
                if (achievement.difficulty == Achievement.Difficulty.HARD) {
                    hard.add(achievement);
                }
                achievements.put(0, easy);
                achievements.put(1, medium);
                achievements.put(2, hard);
            }

            int i = 1;
            for (difficulty difficulty : AchievementTab.difficulty.values()) {
                List<Achievement> list = achievements.get(difficulty.ordinal());
                for (Achievement achievement : list) {
                    int i2 = i;
                    ActionsManager.getManager().submit(START_INDEX + i, new ButtonAction() {
                        @Override
                        public void handle(Player player, int id) {
                            if (player.getAchievementTab().lastClick + Time.ONE_SECOND * 2 > System.currentTimeMillis() && player.getAchievementTab().clickId == START_INDEX + i2) {
                                player.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements:ID" + achievement.id);
                            } else {
                                double average = player.getAchievementTracker().progress(achievement).progressPercent();
                                player.getActionSender().sendDialogue("@dre@" + achievement.title, ActionSender.DialogueType.MESSAGE, 1,
                                        Animation.FacialAnimation.HAPPY,
                                        achievement.tasks.size() < 1 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(0)).progress + "/" + achievement.tasks.get(0).threshold + (achievement.tasks.get(0).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(0).desc : ""),
                                        achievement.tasks.size() < 2 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(1)).progress + "/" + achievement.tasks.get(1).threshold + (achievement.tasks.get(1).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(1).desc : ""),
                                        achievement.tasks.size() < 3 ? "" : "@dre@" + player.getAchievementTracker().taskProgress(achievement.tasks.get(2)).progress + "/" + achievement.tasks.get(2).threshold + (achievement.tasks.get(2).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(2).desc : ""),
                                        "" + buildPercentBar(average)
                                );
                            }
                            player.getAchievementTab().clickId = START_INDEX + i2;
                            player.getAchievementTab().lastClick = System.currentTimeMillis();
                        }
                    });
                    i++;
                }
                i += 2;
            }

            ActionsManager.getManager().submit(32002, new ButtonAction() {
                public void handle(Player player, int id) {
                    player.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements");
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
