package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;

import java.util.*;

/**
 * Created by Gilles on 10/10/2015.
 */
public class Poll {
    private static Map<Integer, Poll> polls = new HashMap<>();

    private List<String> yesVotes = new ArrayList<>();
    private List<String> noVotes = new ArrayList<>();
    private int index;
    private String question, description;
    private Date endDate;
    private boolean canChange, active;

    public Poll(int index, String question, String description, Date endDate, boolean canChange) {
        new Poll(index, question, description, endDate, canChange, false);
    }

    public Poll(int index, String question, String description, Date endDate, boolean canChange, boolean getVotes) {
        this.index = index;
        this.question = question;
        this.description = description;
        this.canChange = canChange;
        this.endDate = endDate;
        this.active = true;
        addPoll(this.index, this);
        if (getVotes)
            World.getWorld().getLogsConnection().offer(new LoadVotes(this.index));
    }

    public static void addPoll(int index, Poll poll) {
        polls.put(index, poll);
    }

    public static Poll getPoll(int index) {
        return polls.get(index);
    }

    public static Map<Integer, Poll> getPolls() {
        return polls;
    }

    public static boolean removeInactive(Poll poll) {
        if(poll.getEndDate().before(Calendar.getInstance().getTime())) {
            poll.active = false;
            World.getWorld().getLogsConnection().offer(new SavePolls());
            return true;
        }
        return false;
    }

    public List<String> getYesVotes() {
        return yesVotes;
    }

    public List<String> getNoVotes() {
        return noVotes;
    }

    public int getIndex() {
        return index;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return Misc.wrapString(description, 45);
    }

    public boolean canChange() {
        return canChange;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void addNoVote(String playerName) {
        noVotes.add(playerName);
        World.getWorld().getLogsConnection().offer(new SaveVote(playerName, index, false));
    }

    public boolean isActive() {
        return active;
    }

    public void removeYesVote(String playerName) {
        yesVotes.remove(playerName);
    }

    public void removeNoVote(String playerName) {
        noVotes.remove(playerName);
    }

    public void addYesVote(String playerName) {
        yesVotes.add(playerName);
        World.getWorld().getLogsConnection().offer(new SaveVote(playerName, index, true));
    }

    public void addNoVote(Player player) {
        if (noVotes.contains(player.getName()))
            return;
        player.sendMessage("You have cast a vote for no.");
        addNoVote(player.getName());
        World.getWorld().getLogsConnection().offer(new SaveVote(player.getName(), index, false));
    }

    public void addYesVote(Player player) {
        if (yesVotes.contains(player.getName()))
            return;
        player.sendMessage("You have cast a vote for yes.");
        addYesVote(player.getName());
        World.getWorld().getLogsConnection().offer(new SaveVote(player.getName(), index, true));
    }

    public void changeVote(Player player) {
        String playerName = player.getName();
        if (noVotes.contains(playerName)) {
            removeNoVote(playerName);
            addYesVote(playerName);
            player.sendMessage("Your vote has been changed from no to yes.");
            return;
        }
        if (yesVotes.contains(playerName)) {
            removeYesVote(playerName);
            addNoVote(playerName);
            player.sendMessage("Your vote has been changed from yes to no.");
            return;
        }
    }
}
