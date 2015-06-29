package org.hyperion.rs2.model.content.achievements;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.achievements.Achievement;
import org.hyperion.rs2.model.achievements.Difficulty;

/**
 * Created by User on 6/23/2015.
 */
public class SteppedAchievement extends Achievement {

    private int steps, currentStep, pkp, dp;

    public SteppedAchievement(String name, Difficulty difficulty, int interfaceId, int steps, int currentStep, int pkp, int dp, String[] reward, String... instructions) {
        super(name, difficulty, interfaceId, reward, instructions);
        this.steps = steps;
        this.currentStep = currentStep;
        this.pkp = pkp;
        this.dp = dp;
        setState(getStateForProgress());
        System.out.println(getState());
    }

    public int getSteps() {
        return steps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getStateForProgress() {
        if(currentStep == steps)
            return 2;
        else if(currentStep == 0)
            return 0; // not started
        else
            return 1; // in progress
    }

    @Override
    public void progress(Player player) {
        currentStep++;
        if(currentStep == getSteps()) {
            setState(2);
            giveReward(player, pkp, dp);
        } else if(currentStep == 1){
            setState(1);
        }
    }

}
