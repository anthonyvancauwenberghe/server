package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.util.Misc;
import java.util.*;

/**
 * Created by Gilles on 9/10/2015.
 */
public class RandomEvent {

    private Player player;
    private int secondsLeft = SECONDS_DEFAULT;
    private boolean doingRandom = false;
    Map<Integer, Integer> answers = new HashMap<>();
    private int n1, n2;

    private int attempts;
    private static int MAX_ATTEMPTS = 3;
    private static int SECONDS_DEFAULT = 300;
    private static Location[] locations = {
            Location.create(2689, 3514, 0),
            Location.create(2942, 3395, 0),
            Location.create(2957, 3502, 0),
            Location.create(3350, 3343, 0),
            Location.create(3433, 2892, 0),
            Location.create(3519, 3365, 0),
            Location.create(2893, 5433, 0),
            Location.create(3024, 9582, 0),
            Location.create(2425, 4446, 0),
            Location.create(3224, 3174, 0)
    };

    public boolean isDoingRandom() {
        return doingRandom;
    }

    public RandomEvent(Player player) {
        this.player = player;
    }

    public boolean skillAction() {
        return skillAction(10);
    }

    public boolean skillAction(int amount) {
        return reduceSecondsLeft(Misc.random(amount));
    }

    public boolean reduceSecondsLeft(int amount) {
        secondsLeft -= amount;
        if(secondsLeft <= 0) {
            triggerRandom(player, true);
            return true;
        }
        return false;
    }

    public static void triggerRandom(Player player, boolean checks) {
        if(checks) {
            if(player.getRandomEvent().doingRandom) {
                player.getRandomEvent().randomTeleport();
                return;
            }
        }
        player.getRandomEvent().secondsLeft = 60 * 5;
        player.getRandomEvent().doingRandom = true;
        player.getRandomEvent().attempts = 0;
        player.getRandomEvent().generateSum();
    }

    public void generateSum() {
        int number1 = Misc.random(4);
        int number2 = Misc.random(4);
        player.getRandomEvent().n1 = number1;
        player.getRandomEvent().n2 = number2;
        player.getRandomEvent().displaySum();
    }

    public void displaySum() {

        answers.clear();
        while(answers.size() <= 4) {
            int number = Misc.random(10);
            if(!answers.containsValue(number))
                answers.put(answers.size(), number);
        }
        if(!answers.containsValue((n1 + n2)))
            answers.put(Misc.random(3), (n1 + n2));
        display();

    }

    public void display() {
        player.getActionSender().sendDialogue("What is " + n1 + "+" + n2 + "?", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT, "" + answers.get(0), "" + answers.get(1), "" + answers.get(2), "" + answers.get(3));
        player.getInterfaceState().setNextDialogueId(0, 610);
        player.getInterfaceState().setNextDialogueId(1, 611);
        player.getInterfaceState().setNextDialogueId(2, 612);
        player.getInterfaceState().setNextDialogueId(3, 613);
    }

    public void answer(int answer) {
        attempts++;
        if(answers.get(answer) != (n1 + n2)) {
            if(attempts != MAX_ATTEMPTS)
                player.getRandomEvent().generateSum();
            else
                randomTeleport();
            return;
        }
        player.getActionSender().removeChatboxInterface();
        player.sendMessage("You have given the correct answer!");
        doingRandom = false;
        secondsLeft = SECONDS_DEFAULT;
    }

    public void randomTeleport() {
        player.getActionSender().removeChatboxInterface();
        Magic.teleport(player, locations[Misc.random(locations.length - 1)], true);
        doingRandom = false;
    }

    static {
        CommandHandler.submit(new Command("triggerrandom", Rank.DEVELOPER) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                RandomEvent.triggerRandom(player, false);
                return false;
            }
        });
    }
}