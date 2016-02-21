package org.hyperion.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hyperion.Configuration;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.model.EntityHandler;
import org.hyperion.rs2.model.World;

import java.util.concurrent.*;

/**
 * Created by Gilles on 12/02/2016.
 */
public final class GameEngine implements Runnable {

    private final static int ENGINE_DELAY = Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY);
    private final static int DEFAULT_ENGINE_STATE = 1;
    private final ScheduledExecutorService logicService = createLogicService();
    private int engineState = DEFAULT_ENGINE_STATE;

    @Override
    public void run() {
        try {
            if(engineState == DEFAULT_ENGINE_STATE)
                World.sequence();
            TaskManager.sequence();
            nextEngineState();
        } catch (Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_running_errors.txt", e);
            World.getPlayers().stream().filter(player -> player != null).forEach(EntityHandler::deregister);
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
        if(engineState == 600 / ENGINE_DELAY)
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
}
