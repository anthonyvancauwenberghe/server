package org.hyperion.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hyperion.Configuration;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

import java.util.concurrent.*;

/**
 * Created by Gilles on 12/02/2016.
 */
public final class GameEngine implements Runnable {

    private final static int DEFAULT_ENGINE_STATE = 1;
    private final ScheduledExecutorService logicService = createLogicService();
    private int engineState = DEFAULT_ENGINE_STATE;

    private long lastEngineRun = System.currentTimeMillis();
    private static long totalTime = 0;
    private static int totalRuns = 0;

    @Override
    public void run() {
        try {
            if(engineState == DEFAULT_ENGINE_STATE) {
                World.sequence();
                if(totalRuns > 10)
                    totalTime += (System.currentTimeMillis() - lastEngineRun);
                    totalRuns++;
                lastEngineRun = System.currentTimeMillis();
            }
            TaskManager.sequence();
            nextEngineState();
        } catch (Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_running_errors.txt", e);
        }
    }

    public void submit(Runnable t) {
        try {
            logicService.execute(t);
        } catch(Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_logic_errors.txt", e);
        }
    }

    private void nextEngineState() {
        if(engineState == 600 / Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY))
            engineState = DEFAULT_ENGINE_STATE - 1;
        engineState++;
    }

    public static ScheduledExecutorService createLogicService() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
        executor.setKeepAliveTime(45, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        return Executors.unconfigurableScheduledExecutorService(executor);
    }

    static {
        NewCommandHandler.submit(new NewCommand("enginestate", Rank.DEVELOPER) {
            @Override
            protected boolean execute(Player player, String[] input) {
                player.sendMessage("Average time: " + (totalTime / (totalRuns - 10)) + "ms");
                player.sendMessage("Total runs: " + totalRuns);
                return true;
            }
        });
    }
}
