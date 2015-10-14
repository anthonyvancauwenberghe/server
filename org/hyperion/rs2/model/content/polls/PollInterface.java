package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;

import java.text.SimpleDateFormat;

/**
 * Created by Gilles on 10/10/2015.
 */
public class PollInterface {

    private static int INTERFACE_ID = 27500;
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM hh:mm");

    static {

        ActionsManager.getManager().submit(27508, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getActionSender().removeAllInterfaces();
            }
        });

        ActionsManager.getManager().submit(27511, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                if (Poll.getPolls().isEmpty()) {
                    player.sendMessage("There are currently no polls going.");
                    return;
                }
                player.getPoll().voteYes();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27514, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                if (Poll.getPolls().isEmpty()) {
                    player.sendMessage("There are currently no polls going.");
                    return;
                }
                player.getPoll().voteNo();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27516, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPoll().selectPreviousPoll();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27517, new ButtonAction() {
            @Override
            public void handle(Player player, int id) {
                player.getPoll().selectNextPoll();
                player.getPoll().fillInterface();
            }
        });

    }

    private Player player;
    private int activePoll = 0;

    public PollInterface(Player player) {
        this.player = player;
    }

    public static int getPercentageYes(int votesYes, int votesNo) {

        if (votesYes == 0)
            return 0;
        if (votesNo == 0)
            return 100;
        return (int) (votesYes * 100f / votesNo);
    }

    public void openInterface() {
        Poll.removeInactivePolls();
        if (Poll.getPolls().isEmpty()) {
            player.sendMessage("There are currently no polls going.");
            return;
        }
        fillInterface();
        player.getActionSender().showInterface(INTERFACE_ID);
    }

    public void selectNextPoll() {
        selectPoll(activePoll + 1);
    }

    public void selectPreviousPoll() {
        selectPoll(activePoll - 1);
    }

    public void selectPoll(int number) {
        if (number < Poll.getPolls().size() && number >= 0)
            activePoll = number;
    }

    public void fillInterface() {
        Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        String[] content = poll.getDescription().split("\n");
        sendArrows();
        sendButtons();
        player.getActionSender().sendString(27505, poll.getQuestion());
        player.getActionSender().sendString(27634, "Ends: " + timeFormat.format(poll.getEndDate()));
        for (int i = 0; i < content.length; i++)
            player.getActionSender().sendString(27519 + i, content[i]);
        sendPercentBar(getPercentageYes(poll.getYesVotes().size(), poll.getNoVotes().size()));
    }

    public void sendPercentBar(int percentYes) {
        int i = 0;
        player.getActionSender().sendString(27630, percentYes + "%");
        int percentNo = 100 - percentYes;
        for (; i < percentNo; i++) {
            player.getActionSender().sendHideComponent(27529 + i, true);
        }
        for (; i < 100; i++) {
            player.getActionSender().sendHideComponent(27529 + i, false);
        }
    }

    public void sendArrows() {
        if (activePoll == 0) {
            player.getActionSender().sendHideComponent(27516, true);
        } else {
            player.getActionSender().sendHideComponent(27516, false);
        }
        if (activePoll >= Poll.getPolls().size() - 1) {
            player.getActionSender().sendHideComponent(27517, true);
        } else {
            player.getActionSender().sendHideComponent(27517, false);
        }
    }

    public void sendButtons() {
        Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if (poll.getYesVotes().contains(player.getName()) || poll.getNoVotes().contains(player.getName())) {
            if (poll.canChange()) {
                if (poll.getYesVotes().contains(player.getName())) {
                    sendButtons(true, false);
                    return;
                }
                sendButtons(false, true);
                return;
            }
            sendButtons(true, true);
            return;
        } else {
            sendButtons(false, false);
        }
    }

    public void sendButtons(boolean yes, boolean no) {
        player.getActionSender().sendHideComponent(27510, yes);
        player.getActionSender().sendHideComponent(27513, no);
        if(yes || no) {
            player.getActionSender().sendString("You voted " + (yes ? "yes" : "no  ") + "        ", 27633);
        } else {
            player.getActionSender().sendString("", 27633);
        }
    }

    public void voteYes() {
        Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if (poll.getYesVotes().contains(player.getName()))
            return;
        if (poll.getNoVotes().contains(player.getName())) {
            if (poll.canChange())
                poll.changeVote(player);
            return;
        }
        poll.addYesVote(player);
    }

    public void voteNo() {
        Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if (poll.getNoVotes().contains(player.getName()))
            return;
        if (poll.getYesVotes().contains(player.getName())) {
            if (poll.canChange())
                poll.changeVote(player);
            return;
        }
        poll.addNoVote(player);
    }
}