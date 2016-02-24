package org.hyperion.sql.impl.vote.work;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.sql.DbHub;
import org.hyperion.sql.impl.vote.WaitingVote;
import org.hyperion.util.Time;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CheckWaitingVotesTask extends Task {

    /**
     * The time the task will put in-between each execution.
     */
    private final static long CYCLE_TIME = Time.FIVE_MINUTES;

    /**
     * The instance, so we can request the time left.
     */
    private static CheckWaitingVotesTask INSTANCE;

    public CheckWaitingVotesTask() {
        super(CYCLE_TIME);
        if(INSTANCE != null)
            throw new IllegalStateException("There is already an instance of " + getClass().getSimpleName() + " running.");
        INSTANCE = this;
    }

    @Override
    protected void execute() {
        Server.getLoader().getEngine().submitSql(new EngineTask<Boolean>("Waiting votes query", 5, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                if (!DbHub.initialized() || !DbHub.getDonationsDb().isInitialized())
                    return false;

                List<WaitingVote> waitingVotes = DbHub.getDonationsDb().votes().getWaiting();
                if (waitingVotes == null || waitingVotes.isEmpty())
                    return true;
                waitingVotes.stream().filter(vote -> World.getPlayerByName(vote.playerName()) != null).collect(Collectors.groupingBy(vote -> World.getPlayerByName(vote.playerName()))).forEach((player, donationList) -> TaskManager.submit(new HandleWaitingVoteTask(player, donationList)));
                return true;
            }
        });
    }

    public static void archiveVotes(Player player, boolean deleteAllProcessed, List<WaitingVote> votes, int runelocusVotes, int rspslistVotes, int topgVotes) {
        Server.getLoader().getEngine().submitSql(new EngineTask<Boolean>("Waitingvotes query", 5, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                List<WaitingVote> processedVotes = votes.stream().filter(vote -> vote.processed() && (deleteAllProcessed || !vote.date().toLocalDate().equals(LocalDate.now()))).collect(Collectors.toList());
                processedVotes.forEach(vote -> DbHub.getDonationsDb().votes().delete(vote));
                DbHub.getDonationsDb().votes().insertVote(player, runelocusVotes, rspslistVotes, topgVotes);
                return true;
            }
        });
    }

    public static int getSecondLeft() {
        if(INSTANCE == null)
            return -1;
        return (int)((INSTANCE.getCountdown() * Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY)) / 1000);
    }
}