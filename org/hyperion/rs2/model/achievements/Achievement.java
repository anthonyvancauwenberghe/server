package org.hyperion.rs2.model.achievements;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

import java.util.ArrayList;


/**
 * Created by Scott Perretta on 5/20/2015.
 */
public abstract class Achievement {

    public static ArrayList<Achievement> achievements = new ArrayList<>();

    protected String name;
    protected AchievementData achievementData;
    protected int currentStep;

    public Achievement(String name, AchievementData achievementData) {
        this.name = name;
        this.achievementData = achievementData;
        currentStep = 0;
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

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public AchievementData getAchievementData() {
        return achievementData;
    }

    public String getName() {
        return name;
    }

    public void loadInformation(Player player) {
        for(int i = 28989; i < achievementData.getInstructions().length + 28989; i++) {
            player.getActionSender().sendString(i, achievementData.getInstructions()[i - 28989]);
        }
        for(int i = 29402; i < achievementData.getRewards().length + 29402; i++) {
            player.getActionSender().sendString(i, achievementData.getRewards()[i - 29402]);
        }
    }

    public void giveReward(Player player) {
        player.getPoints().inceasePkPoints(achievementData.getPkp());
        String message = "For completing " + name + ", you have been rewarded " + achievementData.getPkp() + " pkp";
        if(achievementData.getDp() > 0) {
            player.getPoints().increaseDonatorPoints(achievementData.getDp());
            message += ", " + achievementData.getDp() + " donator points!";
        }
        if(achievementData.getItems() != null) {
            for(Item item : achievementData.getItems()) {
                player.getBank().add(item);
            }
            player.getActionSender().sendMessage("Your rewards were added to your bank!");
        }
        player.getActionSender().sendMessage(message);
    }

}
