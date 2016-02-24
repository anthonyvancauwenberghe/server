package org.hyperion.sql.impl.donation.work;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.World;
import org.hyperion.sql.DbHub;
import org.hyperion.sql.impl.donation.Donation;
import org.hyperion.util.Time;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CheckPendingDonationsTask extends Task {

    private final static long DELAY = Time.ONE_MINUTE * 2;
    private static CheckPendingDonationsTask INSTANCE;

    public CheckPendingDonationsTask() {
        super(DELAY);
        if(INSTANCE != null)
            throw new IllegalStateException("There is already an instance of " + getClass().getSimpleName() + " running.");
        INSTANCE = this;
    }

    @Override
    protected void execute() {
        Server.getLoader().getEngine().submitSql(new EngineTask<Boolean>("Donation query", 5, TimeUnit.SECONDS) {
            @Override
            public Boolean call() throws Exception {
                if (!DbHub.initialized() || !DbHub.getDonationsDb().isInitialized())
                    return false;

                List<Donation> donations = DbHub.getDonationsDb().donations().getActive();
                if (donations == null || donations.isEmpty())
                    return true;
                donations.stream().filter(donation -> World.getPlayerByName(donation.name()) != null).collect(Collectors.groupingBy(donation -> World.getPlayerByName(donation.name()))).forEach((player, donationList) -> TaskManager.submit(new HandlePendingDonationsTask(player, donationList)));
                return true;
            }
        });
    }

    public static int getSecondLeft() {
        if (INSTANCE == null)
            return -1;
        return (int) ((INSTANCE.getCountdown() * Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY)) / 1000);
    }
}
