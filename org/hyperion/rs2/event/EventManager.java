package org.hyperion.rs2.event;

import org.hyperion.Server;

import java.util.concurrent.TimeUnit;

/**
 * A class that manages <code>Event</code>s for a specific
 * <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class EventManager {

	public static void submit(final Event event) {
		submit(event, event.getDelay());
	}

	private static void submit(final Event event, final long delay) {
		Server.getLoader().getEngine().scheduleLogic((Runnable) () -> {
            long start = System.currentTimeMillis();
            //System.out.println("Starting event: " + event.getClass().getName());
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
            //System.out.printf("Finished event %s in %,d MS%n", event.getClass().getName(), elapsed);
            //ServerTimeManager.getSingleton().add(event.getName(), elapsed);
            long remaining = event.getDelay() - elapsed;
            if(remaining <= 0) {
                remaining = 0;
            }
            //System.out.printf("Submitting event %s again with delay %,d MS%n", event.getClass().getName(), remaining);
            submit(event, remaining);
        }, delay, TimeUnit.MILLISECONDS);
	}


}
