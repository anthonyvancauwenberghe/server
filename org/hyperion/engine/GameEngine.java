package org.hyperion.engine;

import org.hyperion.rs2.event.impl.UpdateEvent;
import org.hyperion.rs2.logging.FileLogging;
import org.hyperion.rs2.task.Task;
import org.hyperion.util.BlockingExecutorService;

import java.util.concurrent.*;

/**
 * The 'core' class of the server which processes all the logic tasks in one
 * single logic <code>ExecutorService</code>. This service is scheduled which
 * means <code>Event</code>s are also submitted to it.
 *
 * @author Graham Edgecombe
 */
public class GameEngine implements Runnable {

	/**
	 * A queue of pending tasks.
	 */
	private final BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();

	/**
	 * The logic service used for scheduled tasks.
	 */
	private final ScheduledExecutorService logicService = Executors.newScheduledThreadPool(1);

	/**
	 * The task service, used by <code>ParallelTask</code>s.
	 */
	private final BlockingExecutorService taskService = new BlockingExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

	/**
	 * The work service, generally for file I/O and other blocking operations.
	 */
	private final ExecutorService workService = Executors.newSingleThreadExecutor();

	/**
	 * Running flag.
	 */
	private boolean running = false;

	/**
	 * Thread instance.
	 */
	private Thread thread;

	/**
	 * Submits a new task which is processed on the logic thread as soon as
	 * possible.
	 *
	 * @param task The task to submit.
	 */
	public void pushTask(Task task) {
		tasks.offer(task);
	}


	/**
	 * Checks if this <code>GameEngine</code> is running.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Starts the <code>GameEngine</code>'s thread.
	 */
	public void start() {
		if(running) {
			throw new IllegalStateException("The engine is already running.");
		}
		running = true;
		thread = new Thread(this);
		thread.setName("Game Engine Thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	/**
	 * Stops the <code>GameEngine</code>'s thread.
	 */
	public void stop() {
		if(! running) {
			throw new IllegalStateException("The engine is already stopped.");
		}
		running = false;
		thread.interrupt();
	}

	@Override
	public void run() {
		try {
			while(running) {
				try {
					final Task task = tasks.take();
					UpdateEvent.updateTimer();
					submitLogic(() -> task.execute(GameEngine.this));
				} catch(InterruptedException e) {
					System.out.println("Cought Interrupedexception!");
					e.printStackTrace();
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					FileLogging.writeError("game_engine_tasks_errors.txt", e);
				}
			}
		} finally {
			logicService.shutdown();
			taskService.shutdown();
			workService.shutdown();
		}
	}

	/**
	 * Schedules a task to run in the logic service.
	 *
	 * @param runnable The runnable.
	 * @param delay    The delay.
	 * @param unit     The time unit.
	 * @return The <code>ScheduledFuture</code> of the scheduled logic.
	 */
	public ScheduledFuture<?> scheduleLogic(final Runnable runnable, long delay, TimeUnit unit) {
		return logicService.schedule((Runnable) () -> {
            try {
                runnable.run();
            } catch(Exception e) {
                e.printStackTrace();
                FileLogging.writeError("game_engine_logic_errors.txt", e);
            }
        }, delay, unit);
	}

	/**
	 * Submits a task to run in the parallel task service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitTask(final Runnable runnable) {
		taskService.submit((Runnable) () -> {
            try {
                runnable.run();
            } catch(Exception e) {
                e.printStackTrace();
                FileLogging.writeError("game_engine_taskservice_errors.txt", e);
            }
        });
	}

	/**
	 * Submits a task to run in the work service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitWork(final Runnable runnable) {
		workService.submit((Runnable) () -> {
            try {
                runnable.run();
            } catch(Exception e) {
                System.out.println("sever shit happening 3");
                e.printStackTrace();
                FileLogging.writeError("game_engine_workservice_errors.txt", e);
            }
        });
	}

	/**
	 * Submits a task to run in the logic service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitLogic(final Runnable runnable) {
		logicService.submit((Runnable) () -> {
            try {
                runnable.run();
            } catch(Exception e) {
                e.printStackTrace();
                FileLogging.writeError("game_engine_logicservice2_errors.txt", e);
            }
        });
	}

	/**
	 * Waits for pending parallel tasks.
	 *
	 * @throws ExecutionException If an error occurred during a task.
	 */
	public void waitForPendingParallelTasks() throws ExecutionException {
		taskService.waitForPendingTasks();
	}

}
