package org.hyperion.rs2.model;

import org.hyperion.rs2.model.joshyachievementsv2.Achievement;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementProgress;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTaskProgress;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Gilles on 29/09/2015.
 */
public class AchievementTab {

    private final static int START_INDEX = 32011;
    static List<Achievement> easy = new LinkedList<>();
    static List<Achievement> medium = new LinkedList<>();
    static List<Achievement> hard = new LinkedList<>();
    static List<Achievement> elite = new LinkedList<>();
    static Map<Integer, List> achievements = new HashMap<>();

    static {
        try{
            for(final Achievement achievement : Achievements.get().values()){
                if(achievement.difficulty == Achievement.Difficulty.EASY){
                    easy.add(achievement);
                }
                if(achievement.difficulty == Achievement.Difficulty.MEDIUM){
                    medium.add(achievement);
                }
                if(achievement.difficulty == Achievement.Difficulty.HARD){
                    hard.add(achievement);
                }
                if(achievement.difficulty == Achievement.Difficulty.ELITE){
                    elite.add(achievement);
                }
                achievements.put(0, easy);
                achievements.put(1, medium);
                achievements.put(2, hard);
                achievements.put(3, elite);
            }

            int i = 1;
            for(final difficulty difficulty : AchievementTab.difficulty.values()){
                final List<Achievement> list = achievements.get(difficulty.ordinal());
                for(final Achievement achievement : list){
                    final int i2 = i;
                    ActionsManager.getManager().submit(START_INDEX + i, (player1, id) -> {
                        if(achievement == null)
                            return;
                        if(player1.getAchievementTab().lastClick + Time.ONE_SECOND * 2 > System.currentTimeMillis() && player1.getAchievementTab().clickId == START_INDEX + i2){
                            player1.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements:ID" + achievement.id);
                        }else{
                            //                            double average = player1.getAchievementTracker().progress(achievement).progressPercent();
                            //                            player1.getActionSender().sendDialogue("@dre@" + achievement.title, ActionSender.DialogueType.MESSAGE, 1,
                            //                                    Animation.FacialAnimation.HAPPY,
                            //                                    achievement.tasks.size() < 1 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(0)).progress + "/" + achievement.tasks.get(0).threshold + (achievement.tasks.get(0).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(0).desc : ""),
                            //                                    achievement.tasks.size() < 2 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(1)).progress + "/" + achievement.tasks.get(1).threshold + (achievement.tasks.get(1).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(1).desc : ""),
                            //                                    achievement.tasks.size() < 3 ? "" : "@dre@" + player1.getAchievementTracker().taskProgress(achievement.tasks.get(2)).progress + "/" + achievement.tasks.get(2).threshold + (achievement.tasks.get(2).desc.length() < 70 ? "@bla@ " + achievement.tasks.get(2).desc : ""),
                            //                                    "" + buildPercentBar(average)
                            //                            );
                            final AchievementProgress ap = player1.getAchievementTracker().progress(achievement);
                            player1.sendMessage(ap.info());
                            if(ap.finished()){
                                final Timestamp start = ap.firstStart();
                                final Timestamp finish = ap.lastFinish();
                                if(start != null && finish != null)
                                    player1.sendf("Started: @blu@%s @bla@| Finished: @blu@%s", start, finish);
                            }else{
                                for(int tid = 0; tid < achievement.tasks.size(); tid++){
                                    final AchievementTaskProgress atp = ap.progress(tid);
                                    if(ap.shouldSendInfoFor(tid))
                                        for(final String info : atp.info(player1))
                                            player1.sendMessage(info);
                                }
                            }
                        }
                        player1.getAchievementTab().clickId = START_INDEX + i2;
                        player1.getAchievementTab().lastClick = System.currentTimeMillis();
                    });
                    i++;
                }
                i += 2;
            }

            ActionsManager.getManager().submit(32002, (player1, id) -> player1.sendMessage("l4unchur13 http://www.arteropk.wikia.com/wiki/Achievements"));
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    private final Player player;
    private long lastClick;
    private int clickId;

    public AchievementTab(final Player player) {
        this.player = player;
    }

    public static String buildPercentBar(final double percentage) {
        int rounded = (int) percentage;
        int bars = 0;
        final StringBuilder sb = new StringBuilder("@gre@");
        for(; rounded > 0; rounded -= 1){
            sb.append("|");
            bars++;
        }
        sb.append("@red@");
        for(; bars <= 99; bars++){
            sb.append("|");
        }
        return sb.toString() + "@bla@  " + percentage + "%";
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
        sendEliteAchievements();
        player.getActionSender().sendScrollbarLength(32010, (Achievements.get().size() + difficulty.values().length + 1) * 14);
        sendAchievementCompleted();
    }

    public void sendAchievements(final String difficulty) {
        if(achievements == null)
            return;
        final List<Achievement> currentAchievements = achievements.get(AchievementTab.difficulty.valueOf(difficulty.toUpperCase()).ordinal());
        if(currentAchievements == null)
            return;
        int otherAchievements = 0;
        for(int i = 0; i < AchievementTab.difficulty.valueOf(difficulty.toUpperCase()).ordinal(); i++)
            if(achievements.get(i) != currentAchievements)
                otherAchievements += achievements.get(i).size() + 2;

        int startIndex = otherAchievements;
        if(startIndex > 0){
            player.getActionSender().sendString(START_INDEX + startIndex - 1, "");
            player.getActionSender().sendTooltip(START_INDEX + startIndex - 1, "");
        }
        player.getActionSender().sendString(START_INDEX + startIndex, "@or1@" + Misc.ucFirst(difficulty));
        player.getActionSender().sendFont(START_INDEX + startIndex++, 2);
        for(final Achievement achievement : currentAchievements){
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

    public void sendEliteAchievements() {
        sendAchievements("elite");
    }

    public void sendAchievementCompleted() {
        int finished = 0;
        for(final Achievement achievement : Achievements.get().values())
            if(player.getAchievementTracker().progress(achievement.id).finished())
                finished++;
        player.getActionSender().sendString("Achievement completed: " + finished + "/" + Achievements.get().size(), 32004);
    }

    public enum difficulty {
        EASY,
        MEDIUM,
        HARD,
        ELITE
    }
}
