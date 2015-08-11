package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.ServerMinigame;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.util.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gilles on 11/08/2015.
 */
public class HideNSeek {

    private static final String TITLE = "[@whi@HideNSeek@bla@] ";

    private static int CYCLE_TIME = 60000;
    private List<String> clues = new LinkedList<String>();
    private Player owner;
    private int currentClue = 0;

    private static HideNSeek bot = new HideNSeek();

    public static HideNSeek getBot() {
        return bot;
    }

    private final Event HIDENSEEK_EVENT = new Event(CYCLE_TIME) {
        @Override
        public void execute() {
            if(!CHECKWINNER.isRunning())
                World.getWorld().submit(CHECKWINNER);
            if(currentClue < clues.size()) {
                yellMessage("Clue #" + (currentClue + 1) + ": " + clues.get(currentClue));
                currentClue++;
            } else if(currentClue == clues.size()){
                yellMessage("That was the last clue! Use ::showclues to repeat them!");
                currentClue++;
            } else {
                yellMessage("Use ::showclues to repeat all the clues again...");
                yellMessage(owner.getSafeDisplayName() + " is still out there.");
            }
        }
    };

    private final Event CHECKWINNER = new Event(1000) {
        @Override
        public void execute() {
            checkWinner();
        }
    };

    public void createHideNSeek(Player owner) {
        if(this.owner != null) {
            sendMessage(owner, this.owner.getSafeDisplayName() + " is already doing a HideNSeek event.");
            sendMessage(owner, "You can reset HideNSeek by doing ::resethns.");
            return;
        }
        this.owner = owner;
        sendMessage("HideNSeek event successfully created.");
    }

    public void startHideNSeek(Player player) {
        if(owner == null) {
            sendMessage(player, "There is no HideNSeek event created yet.");
            sendMessage(player, "You can create a HideNSeek event by doing ::createhns.");
            return;
        }
        if(!player.equals(owner)) {
            sendMessage(player, "This HideNSeek event is not yours, but owned by " + owner.getSafeDisplayName() + ".");
            sendMessage(player, "You can reset this event by doing ::resethns.");
            return;
        }

        if(clues.size() <= 0) {
            sendMessage(player, "You cannot start a HideNSeek event without at least 1 clue.");
            return;
        }

        if(CYCLE_TIME == -1) {
            sendMessage(player, "You cannot start a HideNSeek event without specifying the timer.");
            return;
        }

        if(Events.eventName != "") {
            sendMessage(player, "There is already another event running, please wait for it to finish.");
            return;
        }
        Events.fireNewEvent("HideNSeek", true, 60, Location.create(3074, 3504, 0));
        System.out.println(CYCLE_TIME);
        World.getWorld().submit(HIDENSEEK_EVENT);
        yellMessage("HideNSeek will start in 1 minute!.");
    }

    public void stopHideNSeek(Player player) {
        if(!HIDENSEEK_EVENT.isRunning()) {
            sendMessage(player, "There was no HideNSeek event running.");
            return;
        }

        if(Events.eventName == "HideNSeek") {
            Events.resetEvent();
        }
        HIDENSEEK_EVENT.stop();
        yellMessage(player.getSafeDisplayName() + " ended the event early, there is no winner!");
    }

    public void setTimer(Player player, int time) {
        if(owner == null) {
            sendMessage(player, "There is no HideNSeek event created yet.");
            sendMessage(player, "You can create a HideNSeek event by doing ::createhns.");
            return;
        }

        CYCLE_TIME = time * 60 * 1000;
        sendMessage(player, "Delay between clues set to " + time + " minute" + (time == 1 ? "." : "s."));
    }

    public void resetHideNSeek(Player player) {
        if(HIDENSEEK_EVENT.isRunning()) {
            yellMessage(player.getSafeDisplayName() + " ended the event early, there is no winner!");
        } else {
            sendMessage(player, "HideNSeek event has been successfully reset.");
            sendMessage(player.getSafeDisplayName() + " has reset your HideNSeek event.");
        }
        resetHideNSeek();
    }

    public void resetHideNSeek() {
        if(Events.eventName == "HideNSeek") {
            Events.resetEvent();
        }
        owner = null;
        clues.clear();
        CYCLE_TIME = 60000;
        currentClue = 0;
        HIDENSEEK_EVENT.stop();
        CHECKWINNER.stop();
    }

    public void addClue(Player player, String clue) {
        if(owner == null) {
            sendMessage(player, "There is no HideNSeek event created yet.");
            sendMessage(player, "You can create a HideNSeek event by doing ::createhns.");
            return;
        }

        if(!player.equals(owner)) {
            sendMessage(player, "This HideNSeek event is not yours, but owned by " + owner.getSafeDisplayName() + ".");
            sendMessage(player, "You can reset this event by doing ::resethns.");
        }

        if(clue.length() == 7) {
            sendMessage(player, "Use as ::addclue CLUE");
            return;
        }

        if(clue.length() == 57) {
            sendMessage(player, "A clue cannot contain more than 50 characters.");
            return;
        }

        clues.add(TextUtils.ucFirst(clue.toLowerCase()));
        sendMessage("Clue #" + clues.size() + " has been successfully added.");
    }

    private void checkWinner() {
        for (Player p : owner.getRegion().getPlayers()) {
            if(!Rank.isStaffMember(p))
                endEvent(p);
        }
    }

    private void endEvent(Player winner) {
        yellMessage("Player " + winner.getSafeDisplayName() + " has won the HideNSeek!");
        resetHideNSeek();
    }

    public void showClues(Player player) {
        if(player.equals(owner)) {
            for (int i = 0; i < clues.size(); i++)
                sendMessage(player, "Clue #" + (i + 1) + ": " + clues.get(i));
        } else {
            for (int i = 0; i < currentClue; i++)
                sendMessage(player, "Clue #" + (i + 1) + ": " + clues.get(i));
        }
    }

    private void sendMessage(Player player, String message) {
        player.getActionSender().sendMessage(TITLE + message);
    }

    private void sendMessage(String message) {
        try {
            owner.getActionSender().sendMessage(TITLE + message);
        } catch(Exception e) {}
    }

    private void yellMessage(String message) {
        for(Player p : World.getWorld().getPlayers()) {
                p.getActionSender().sendMessage(TITLE + message);
        }
    }

    public void showInfo(Player player) {
        sendMessage(player, "::createhns - Creates a HideNSeek event.");
        sendMessage(player, "::starthns - Starts a HideNSeek event.");
        sendMessage(player, "::stophns - Stops a HideNSeek event.");
        sendMessage(player, "::resethns - Resets a HideNSeek event.");
        sendMessage(player, "::hnstimer - Set a timer for a HideNSeek event.");
        sendMessage(player, "::addclue - Adds a clue to the cluelist.");
        sendMessage(player, "::showclues - Shows the current clues.");
    }
}
