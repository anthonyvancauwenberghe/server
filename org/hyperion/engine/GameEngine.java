package org.hyperion.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.saving.PlayerSaving;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by Gilles on 12/02/2016.
 */
public final class GameEngine implements Runnable {

    /**
     * The engine state that the server starts on. This is also the state that is used for the World updating.
     */
    private final static int DEFAULT_ENGINE_STATE = 1;

    /**
     * This is the thread that handles logic that has nothing to do with the game directly.
     */
    private final ScheduledExecutorService logicService = createService("LogicServiceThread");

    /**
     * This thread handles input and output tasks, such as file writing.
     */
    private final ScheduledExecutorService IOService = createService("IoServiceThread");

    /**
     * This thread handles input and output tasks, such as file writing.
     */
    private final ScheduledExecutorService sqlService = createService("SqlServiceThread");

    /**
     * The current engine state of the server.
     */
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
            World.getPlayers().stream().filter(player -> player != null).forEach(PlayerSaving::save);
        }
    }

    /**
     * This executes a logic task as soon as the thread has any space.
     * @param logicTask The task to execute
     */
    public void submitLogic(EngineTask logicTask) {
        try {
            Future taskResult = logicService.submit(logicTask);
            if(logicTask.isCancellable()) {
                try {
                    taskResult.get(logicTask.getTimeout(), logicTask.getTimeUnit());
                } catch (TimeoutException e) {
                    logicTask.stopTask();
                    taskResult.cancel(true);
                }
            }
        } catch(InterruptedException ex) {
            Server.getLogger().warning("Engine logic task '" + logicTask.getTaskName() + "' took too long, interrupted.");
        } catch(Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_logic_errors.txt", e);
        }
    }

    /**
     * This executes a IO task as soon as the thread has any space.
     * This will return the object that is asked from the IO task.
     * @param ioTask The task to execute.
     * @return The result of the executed task.
     */
    public <T> Optional<T> submitIO(EngineTask<T> ioTask) {
        try {
            Future<T> taskResult = IOService.submit(ioTask);
            if(ioTask.isCancellable()) {
                try {
                    return Optional.of(taskResult.get(ioTask.getTimeout(), ioTask.getTimeUnit()));
                } catch (TimeoutException e) {
                    taskResult.cancel(true);
                    ioTask.stopTask();
                }
            } else {
                return Optional.of(taskResult.get());
            }
        } catch(InterruptedException ex) {
            Server.getLogger().warning("Engine IO task '" + ioTask.getTaskName() + "' took too long, cancelled.");
        } catch(Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_io_errors.txt", e);
        }
        return Optional.empty();
    }

    /**
     * This executes a IO task as soon as the thread has any space.
     * This will return the object that is asked from the IO task.
     * @param sqlTask The task to execute.
     * @return The result of the executed task.
     */
    public <T> Optional<T> submitSql(EngineTask<T> sqlTask) {
        try {
            Future<T> taskResult = sqlService.submit(sqlTask);
            if(sqlTask.isCancellable()) {
                try {
                    return Optional.of(taskResult.get(sqlTask.getTimeout(), sqlTask.getTimeUnit()));
                } catch (TimeoutException e) {
                    taskResult.cancel(true);
                    sqlTask.stopTask();
                }
            } else {
                return Optional.of(taskResult.get());
            }
        } catch(InterruptedException ex) {
            Server.getLogger().warning("Engine Sql task '" + sqlTask.getTaskName() + "' took too long, cancelled.");
        } catch(Exception e) {
            e.printStackTrace();
            FileLogging.writeError("game_engine_sql_errors.txt", e);
        }
        return Optional.empty();
    }

    /**
     * This will switch the engine to the next possible state.
     */
    private void nextEngineState() {
        if(engineState == 600 / Configuration.getInt(Configuration.ConfigurationObject.ENGINE_DELAY))
            engineState = DEFAULT_ENGINE_STATE - 1;
        engineState++;
    }

    public void finish() {
        logicService.shutdown();
        sqlService.shutdown();
        IOService.shutdown();
    }

    /**
     * This will create a thread with the given name.
     * @param threadName The name for the thread
     * @return The created thread
     */
    public static ScheduledExecutorService createService(String threadName) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat(threadName).build());
        executor.setKeepAliveTime(45, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        return Executors.unconfigurableScheduledExecutorService(executor);
    }
}
