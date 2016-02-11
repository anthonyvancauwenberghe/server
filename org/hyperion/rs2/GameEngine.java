package org.hyperion.rs2;

import org.hyperion.rs2.event.impl.UpdateEvent;
import org.hyperion.rs2.model.World;
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

	private int errors = 0;

	private void increaseErrors() {
		errors++;
		System.out.println("Errorcount: " + errors);
		/*if(errors > 100) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter("./data/errorrestart.log", true));
				bw.write("RESTARTED SERVER : " + new Date().toString());
				bw.newLine();
				bw.close();
				for(Player player : World.getPlayers()) {
					Trade.declineTrade(player);
					PlayerFiles.saveGame(player);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			for(int i = 0; i < 10; i++) {
				System.out.println("Restarting Server because of errors!!");
			}
			System.exit(0);
		}*/
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
					submitLogic(new Runnable() {
						@Override
						public void run() {
							long start = System.currentTimeMillis();
							task.execute(GameEngine.this);
							long delta = System.currentTimeMillis() - start;
							//ServerTimeManager.getSingleton().add(task.getClass().getSimpleName(), delta);
						}
					});
				} catch(InterruptedException e) {
					System.out.println("Cought Interrupedexception!");
					e.printStackTrace();
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					World.writeError("game_engine_tasks_errors.txt", e);
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
		return logicService.schedule(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch(Exception e) {
					System.out.println("Server shit happening 1");
					e.printStackTrace();
					World.writeError("game_engine_logic_errors.txt", e);
					increaseErrors();
				}
			}
		}, delay, unit);
	}

	/**
	 * Submits a task to run in the parallel task service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitTask(final Runnable runnable) {
		taskService.submit(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch(Exception e) {
					System.out.println("sever shit happening 2");
					e.printStackTrace();
					World.writeError("game_engine_taskservice_errors.txt", e);
					//World.handleError(t);
					increaseErrors();
				}
			}
		});
	}

	/**
	 * Submits a task to run in the work service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitWork(final Runnable runnable) {
		workService.submit(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch(Exception e) {
					System.out.println("sever shit happening 3");
					e.printStackTrace();
					World.writeError("game_engine_workservice_errors.txt", e);
					//World.handleError(t);
					increaseErrors();
				}
			}
		});
	}

	/**
	 * Submits a task to run in the logic service.
	 *
	 * @param runnable The runnable.
	 */
	public void submitLogic(final Runnable runnable) {
		logicService.submit(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} catch(Exception e) {
					System.out.println("Logic pool exception!");
					e.printStackTrace();
					World.writeError("game_engine_logicservice2_errors.txt", e);
					//World.handleError(t);
					increaseErrors();
				}
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
