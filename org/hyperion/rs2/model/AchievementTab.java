package org.hyperion.rs2.model;

import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievement.Difficulty;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 29/09/2015.
 */
public class AchievementTab {

    private Player player;

    private long lastClick;
    private int clickId;
    private final static int START_INDEX = 32011;

    private static Map<Difficulty, List<Achievement>> achievements = new TreeMap<>();

    public AchievementTab(Player player) {
        this.player = player;
    }

    public void createAchievementTab() {
        resetAchievementTab();
        updateAchievementTab();
    }

    public void resetAchievementTab() {
        for(int i = START_INDEX; i < START_INDEX + 100; i++) {
            player.getActionSender().sendString(i, "");
            player.getActionSender().sendFont(i, 1);
        }
    }

    public void updateAchievementTab() {
        Arrays.stream(Difficulty.values()).forEach(diff -> sendAchievements(diff));
        player.getActionSender().sendScrollbarLength(32010, (achievements.values().stream().mapToInt(item -> item.size()).sum() + (achievements.keySet().size() * 2)) * 16) ;
        sendAchievementCompleted();
    }

    public void sendAchievements(Difficulty difficulty) {
        List<Achievement> currentAchievements = achievements.get(difficulty);
        int startIndex = achievements.entrySet().stream().filter(entry -> !entry.getKey().equals(difficulty) && entry.getKey().ordinal() < difficulty.ordinal()).mapToInt(entry -> entry.getValue().size()).sum() + (difficulty.ordinal() * 2);
        
        if(startIndex > 0) {
            player.getActionSender().sendString(START_INDEX + startIndex - 1, "").sendTooltip(START_INDEX + startIndex - 1, "");
        }
        player.getActionSender().sendString(START_INDEX + startIndex, "@or1@" + Misc.ucFirst(difficulty.toString())).sendFont(START_INDEX + startIndex++, 2);
        for(Achievement achievement : currentAchievements) {
            player.getActionSender().sendString(player.getAchievementTracker().progress(achievement).getTabString(), START_INDEX + startIndex).sendTooltip(START_INDEX + startIndex++, achievement.title);
        }
    }

    public void sendAchievementCompleted() {
        int finished = 0;
        for(Achievement achievement : Achievements.get().values())
            if(player.getAchievementTracker().progress(achievement.id).finished())
                finished++;
        player.getActionSender().sendString("Achievement completed: " + finished + "/" + Achievements.get().size(), 32004);
    }

    public static String buildPercentBar(double percentage) {
        int bars = 0;
        StringBuilder sb = new StringBuilder("@gre@");
        for(;  bars < percentage; bars++) {
            sb.append("|");
        }
        sb.append("@red@");
        for(; bars < 100; bars++) {
            sb.append("|");
        }
        return sb.toString() + "@bla@  " + percentage + "%";
    }

    static {
        Arrays.stream(Difficulty.values()).forEach(i -> achievements.put(i, Achievements.get().stream().filter(ach -> ach.difficulty == i).collect(Collectors.toList())));
        int i = 1;
        for(Difficulty difficulty : Difficulty.values()) {
            List<Achievement> list = achievements.get(difficulty);
            for (Achievement achievement : list) {
                int i2 = i;
                ActionsManager.getManager().submit(START_INDEX + i, (player1, id) -> {
                    if (player1.getAchievementTab().lastClick + Time.ONE_SECOND * 2 > System.currentTimeMillis() && player1.getAchievementTab().clickId == START_INDEX + i2) {
                        player1.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements:ID" + achievement.id);
                    } else {
                        double average = 0;
                        int i1 = 0;
                        for (; i1 < achievement.tasks.size(); i1++) {
                            average += player1.getAchievementTracker().taskProgress(achievement.tasks.get(i1)).progressPercent();
                        }
                        average /= (i1 == 0 ? 1 : i1);
                        player1.getActionSender().sendDialogue("@dre@" + achievement.title, ActionSender.DialogueType.MESSAGE, 1,
                                Animation.FacialAnimation.HAPPY,
                                achievement.tasks.size() < 1 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(0)).progress + "/" + achievement.tasks.get(0).threshold + (achievement.tasks.get(0).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(0).desc : ""),
                                achievement.tasks.size() < 2 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(1)).progress + "/" + achievement.tasks.get(1).threshold + (achievement.tasks.get(1).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(1).desc : ""),
                                achievement.tasks.size() < 3 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(2)).progress + "/" + achievement.tasks.get(2).threshold + (achievement.tasks.get(2).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(2).desc : ""),
                                "" + buildPercentBar(average)
                        );
                    }
                    player1.getAchievementTab().clickId = START_INDEX + i2;
                    player1.getAchievementTab().lastClick = System.currentTimeMillis();
                });
                i++;
            }
            i += 2;
        }

        ActionsManager.getManager().submit(32002, (player1, id) -> player1.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements"));
    }

}
