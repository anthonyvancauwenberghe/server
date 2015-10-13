package org.hyperion.rs2.event;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.ServerTimeManager;

import java.util.concurrent.TimeUnit;

/**
 * A class that manages <code>Event</code>s for a specific
 * <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class EventManager {

	/**
	 * The <code>GameEngine</code> to manager events for.
	 */
	private GameEngine engine;

	/**
	 * Creates an <code>EventManager</code> for the specified
	 * <code>GameEngine</code>.
	 *
	 * @param engine The game engine the manager is managing events for.
	 */
	public EventManager(GameEngine engine) {
		this.engine = engine;
	}

	/**
	 * Submits a new event to the <code>GameEngine</code>.
	 *
	 * @param event The event to submit.
	 */
	public void submit(final Event event) {
		submit(event, event.getDelay());
	}

	/**
	 * Schedules an event to run after the specified delay.
	 *
	 * @param event The event.
	 * @param delay The delay.
	 */
	private void submit(final Event event, final long delay) {
		engine.scheduleLogic(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				System.out.println("Starting event: " + event.getClass().getName());
				if(event.isRunning()) { // this must be false, however
					try {

						event.execute(); // doesnt execute this
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					return;
				}
				long elapsed = System.currentTimeMillis() - start;
				System.out.printf("Finished event %s in %,d MS%n", event.getClass().getName(), elapsed);
				//ServerTimeManager.getSingleton().add(event.getName(), elapsed);
				long remaining = event.getDelay() - elapsed;
				if(remaining <= 0) {
					remaining = 0;
				}
				System.out.printf("Submitting event %s with delay %,d MS%n", event.getClass().getName(), remaining);
				submit(event, remaining);
			}
		}, delay, TimeUnit.MILLISECONDS);
	}


}
