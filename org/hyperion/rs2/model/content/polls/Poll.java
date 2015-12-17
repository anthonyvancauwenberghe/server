package org.hyperion.rs2.model.content.polls;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gilles on 10/10/2015.
 */
public class Poll {
    private static final Map<Integer, Poll> polls = new HashMap<>();

    static {
        CommandHandler.submit(new Command("polls", Rank.DEVELOPER) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                player.getPoll().openInterface();
                return false;
            }
        });

        CommandHandler.submit(new Command("reloadpolls", Rank.DEVELOPER) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                if(Server.getConfig().getBoolean("logssql"))
                    World.getWorld().getLogsConnection().offer(new LoadAllPolls());
                return false;
            }
        });

        CommandHandler.submit(new Command("getresults", Rank.DEVELOPER) {
            @Override
            public boolean execute(final Player player, String input) throws Exception {
                input = filterInput(input);
                int pollId = -1;
                try{
                    pollId = Integer.parseInt(input);
                }catch(final Exception e){
                }
                if(pollId == -1){
                    player.sendMessage("Use as ::getresults POLLID");
                    return true;
                }
                if(!polls.containsKey(pollId)){
                    if(Server.getConfig().getBoolean("logssql"))
                        World.getWorld().getLogsConnection().offer(new LoadPoll(pollId));
                }
                final Poll poll = polls.get(pollId);
                if(poll == null){
                    player.sendMessage("Poll index does not exist.");
                    return true;
                }
                player.sendMessage("@dre@Question: @bla@" + poll.getQuestion());
                player.sendMessage("@dre@Yes votes: @bla@" + poll.getYesVotes().size());
                player.sendMessage("@dre@No votes: @bla@" + poll.getNoVotes().size());
                return false;
            }
        });
    }

    private final List<String> yesVotes = new ArrayList<>();
    private final List<String> noVotes = new ArrayList<>();
    private final int index;
    private final String question;
    private final String description;
    private final Date endDate;
    private final boolean canChange;
    private boolean active;

    public Poll(final int index, final String question, final String description, final Date endDate, final boolean canChange) {
        this(index, question, description, endDate, canChange, false);
    }

    public Poll(final int index, final String question, final String description, final Date endDate, final boolean canChange, final boolean getVotes) {
        this.index = index;
        this.question = question;
        this.description = description;
        this.canChange = canChange;
        this.endDate = endDate;
        this.active = true;
        addPoll(this.index, this);
        if(getVotes)
            if(Server.getConfig().getBoolean("logssql"))
                World.getWorld().getLogsConnection().offer(new LoadVotes(this.index));
    }

    public static void addPoll(final int index, final Poll poll) {
        polls.put(index, poll);
    }

    public static Poll getPoll(final int index) {
        return polls.get(index);
    }

    public static Map<Integer, Poll> getPolls() {
        return polls;
    }

    public static void removeInactivePolls() {
        polls.forEach((index, poll) -> {
            if(poll.getEndDate().before(Calendar.getInstance().getTime())){
                poll.active = false;
                World.getWorld().getLogsConnection().offer(new SavePolls());
            }
        });
    }

    public List<String> getYesVotes() {
        return yesVotes;
    }

    public List<String> getNoVotes() {
        return noVotes;
    }

    public boolean hasVoted(final Player player) {
        return (yesVotes.contains(player.getName()) || noVotes.contains(player.getName()));
    }

    public int getIndex() {
        return index;
    }

    public String getQuestion() {
        return question;
    }

    public String getDescription() {
        return Misc.wrapString(description, 40);
    }

    public boolean canChange() {
        return canChange;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void removeYesVote(final String playerName) {
        yesVotes.remove(playerName);
    }

    public void removeNoVote(final String playerName) {
        noVotes.remove(playerName);
    }

    public void addYesVote(final String playerName) {
        yesVotes.add(playerName);
        if(Server.getConfig().getBoolean("logssql"))
            World.getWorld().getLogsConnection().offer(new SaveVote(playerName, index, true));
    }

    public void addNoVote(final String playerName) {
        noVotes.add(playerName);
        if(Server.getConfig().getBoolean("logssql"))
            World.getWorld().getLogsConnection().offer(new SaveVote(playerName, index, false));
    }

    public void addNoVote(final Player player) {
        if(noVotes.contains(player.getName()))
            return;
        player.sendMessage("You have cast a vote for no.");
        addNoVote(player.getName());
        if(Server.getConfig().getBoolean("logssql"))
            World.getWorld().getLogsConnection().offer(new SaveVote(player.getName(), index, false));
    }

    public void addYesVote(final Player player) {
        if(yesVotes.contains(player.getName()))
            return;
        player.sendMessage("You have cast a vote for yes.");
        addYesVote(player.getName());
        if(Server.getConfig().getBoolean("logssql"))
            World.getWorld().getLogsConnection().offer(new SaveVote(player.getName(), index, true));
    }

    public void changeVote(final Player player) {
        final String playerName = player.getName();
        if(noVotes.contains(playerName)){
            removeNoVote(playerName);
            addYesVote(playerName);
            player.sendMessage("Your vote has been changed from no to yes.");
            return;
        }
        if(yesVotes.contains(playerName)){
            removeYesVote(playerName);
            addNoVote(playerName);
            player.sendMessage("Your vote has been changed from yes to no.");
            return;
        }
    }
}
