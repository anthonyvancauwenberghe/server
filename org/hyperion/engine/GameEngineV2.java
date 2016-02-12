package org.hyperion.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.model.World;

import java.util.concurrent.*;

/**
 * Created by Gilles on 12/02/2016.
 */
public class GameEngineV2 implements Runnable {

    private final ScheduledExecutorService logicService = createLogicService();

    @Override
    public void run() {
        try {
            World.sequence();
            TaskManager.sequence();
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

    public static ScheduledExecutorService createLogicService() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
        executor.setKeepAliveTime(45, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        return Executors.unconfigurableScheduledExecutorService(executor);
    }
}
