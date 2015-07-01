package org.hyperion.rs2.model.achievements;

import org.hyperion.rs2.model.Player;

import java.util.ArrayList;


/**
 * Created by Scott Perretta on 5/20/2015.
 */
public abstract class Achievement {

    public static ArrayList<Achievement> achievements = new ArrayList<>();

    private String name;
    private Difficulty difficulty;
    private int interfaceId;
    private int state;
    private String[] instructions, reward;

    public Achievement(String name, Difficulty difficulty, int interfaceId, String[] reward, String... instructions) {
        this.name = name;
        this.difficulty = difficulty;
        this.interfaceId = interfaceId;
        this.reward = reward;
        this.instructions = instructions;
        achievements.add(this);
    }

    /**
     * Handles the progression of the achievement. Checks to see if
     * the achievement is completed and updates the state of the
     * achievement.
     *
     * @param player Player player
     */
    public abstract void progress(Player player);

    public String getName() {
        return name;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void loadInformation(Player player) {
        for(int i = 28989; i < instructions.length + 28989; i++) {
            player.getActionSender().sendString(i, instructions[i - 28989]);
        }
        for(int i = 29402; i < reward.length + 29402; i++) {
            player.getActionSender().sendString(i, reward[i - 29402]);
        }
    }

    public void giveReward(Player player, int pkp, int dp) {
        player.getPoints().inceasePkPoints(pkp);
        String message = "For completing " + name + ", you have been rewarded " + pkp + " pk points";
        if(dp > 0) {
            player.getPoints().increaseDonatorPoints(dp);
            message += " and " + dp + " donator points!";
        }
        player.getActionSender().sendMessage(message);
    }

}
