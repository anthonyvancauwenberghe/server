package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.polls.Poll;
import org.hyperion.rs2.model.content.polls.PollInterface;
import org.hyperion.rs2.sql.requests.VoteRequest;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Calendar;

public class PromoteEvent extends Event {

    public static final long CYCLE_TIME = Time.ONE_MINUTE * 2;

    public PromoteEvent() {
        super(CYCLE_TIME);
    }

    @Override
    public void execute() {
        World.getWorld().getPlayers().forEach(player -> {
            if(Poll.getPolls().size() > 0){
                boolean hasVoted = true;
                for(Poll poll : Poll.getPolls().values()){
                    if(poll.hasVoted(player) || !PollInterface.canVote(player))
                        continue;
                    hasVoted = false;
                    break;
                }
                if(!hasVoted && Misc.random(1) == 1){
                    player.sendServerMessage("There is a poll available you haven't voted for!");
                    return;
                }
            }
            String lastVoted = player.getPermExtraData().getString("lastVoted");
            if(lastVoted != null)
                if(!lastVoted.equalsIgnoreCase(VoteRequest.FORMAT_PLAYER.format(Calendar.getInstance().getTime())))
                    if(!Rank.hasAbility(player, Rank.DEVELOPER))
                        player.sendServerMessage("Don't forget to vote again using the ::vote command!");
            if(lastVoted == null)
                player.sendServerMessage("Remember to vote using the ::vote command!");
        });
    }
}
