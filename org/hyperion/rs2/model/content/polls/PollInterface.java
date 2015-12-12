package org.hyperion.rs2.model.content.polls;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;

import java.text.SimpleDateFormat;

/**
 * Created by Gilles on 10/10/2015.
 */
public class PollInterface {

    private static final int INTERFACE_ID = 27500;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM hh:mm");
    private static boolean enabled = true;

    static {

        ActionsManager.getManager().submit(27508, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getActionSender().removeAllInterfaces();
            }
        });

        ActionsManager.getManager().submit(27511, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                if(Poll.getPolls().isEmpty()){
                    player.sendMessage("There are currently no polls going.");
                    return;
                }
                player.getPoll().voteYes();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27514, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                if(Poll.getPolls().isEmpty()){
                    player.sendMessage("There are currently no polls going.");
                    return;
                }
                player.getPoll().voteNo();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27516, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getPoll().selectPreviousPoll();
                player.getPoll().fillInterface();
            }
        });

        ActionsManager.getManager().submit(27517, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getPoll().selectNextPoll();
                player.getPoll().fillInterface();
            }
        });

    }

    static {
        CommandHandler.submit(new Command("disablepolls") {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                PollInterface.enabled = false;
                player.sendMessage("Polls are now disabled.");
                return true;
            }
        });
        CommandHandler.submit(new Command("enablepolls") {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                PollInterface.enabled = true;
                player.sendMessage("Polls are now enabled.");
                return true;
            }
        });
    }

    private final Player player;
    private int activePoll = 0;

    public PollInterface(final Player player) {
        this.player = player;
    }

    private static int getPercentageYes(final int votesYes, final int votesNo) {
        if(votesYes == 0)
            return 0;
        if(votesNo == 0)
            return 100;
        if(votesYes == votesNo)
            return 50;
        return (int) ((votesYes * 100f) / (votesNo + votesYes));
    }

    public static boolean canVote(final Player player) {
        return (player.getSkills().getTotalLevel() >= 1800 || player.getPoints().getEloPeak() >= 1800) || Rank.hasAbility(player, Rank.SUPER_DONATOR) || Rank.hasAbility(player, Rank.DONATOR);
    }

    public void openInterface() {
        if(!enabled)
            return;
        Poll.removeInactivePolls();
        if(Poll.getPolls().isEmpty()){
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

    public void selectPoll(final int number) {
        if(number < Poll.getPolls().size() && number >= 0)
            activePoll = number;
    }

    public void fillInterface() {
        final Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        final String[] content = poll.getDescription().split("\n");
        sendArrows();
        sendButtons();
        player.getActionSender().sendString(27505, poll.getQuestion());
        player.getActionSender().sendString(27634, "Ends: " + timeFormat.format(poll.getEndDate()));
        int i = 0;
        for(; i < content.length; i++)
            player.getActionSender().sendString(27519 + i, content[i]);
        for(; i < 6; i++)
            player.getActionSender().sendString(27519 + i, "");
        sendPercentBar(getPercentageYes(poll.getYesVotes().size(), poll.getNoVotes().size()));
    }

    public void sendPercentBar(final int percentYes) {
        int i = 99;
        player.getActionSender().sendString(27630, percentYes + "%");
        for(; i >= percentYes; i--){
            player.getActionSender().sendHideComponent(27529 + i, true);
        }
        for(; i >= 0; i--){
            player.getActionSender().sendHideComponent(27529 + i, false);
        }
    }

    public void sendArrows() {
        if(activePoll == 0){
            player.getActionSender().sendHideComponent(27516, true);
        }else{
            player.getActionSender().sendHideComponent(27516, false);
        }
        if(activePoll >= Poll.getPolls().size() - 1){
            player.getActionSender().sendHideComponent(27517, true);
        }else{
            player.getActionSender().sendHideComponent(27517, false);
        }
    }

    public void sendButtons() {
        final Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if(poll.getYesVotes().contains(player.getName()) || poll.getNoVotes().contains(player.getName())){
            if(poll.canChange()){
                if(poll.getYesVotes().contains(player.getName())){
                    sendButtons(true, false);
                    return;
                }
                sendButtons(false, true);
                return;
            }
            sendButtons(true, true);
            return;
        }else{
            sendButtons(false, false);
        }
    }

    public void sendButtons(final boolean yes, final boolean no) {
        player.getActionSender().sendHideComponent(27510, yes);
        player.getActionSender().sendHideComponent(27513, no);
        if(yes || no){
            player.getActionSender().sendString("You voted " + (yes ? "yes" : "no  ") + "        ", 27633);
        }else{
            player.getActionSender().sendString("", 27633);
        }
    }

    public void voteYes() {
        final Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if(poll.getYesVotes().contains(player.getName()))
            return;
        if(poll.getNoVotes().contains(player.getName())){
            if(poll.canChange())
                poll.changeVote(player);
            return;
        }
        poll.addYesVote(player);
    }

    public void voteNo() {
        final Poll poll = Poll.getPolls().get(Poll.getPolls().keySet().toArray()[activePoll]);
        if(poll.getNoVotes().contains(player.getName()))
            return;
        if(poll.getYesVotes().contains(player.getName())){
            if(poll.canChange())
                poll.changeVote(player);
            return;
        }
        poll.addNoVote(player);
    }
}
