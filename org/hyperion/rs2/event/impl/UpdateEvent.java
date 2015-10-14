package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.sql.requests.HighscoresRequest;
import org.hyperion.rs2.task.ConsecutiveTask;
import org.hyperion.rs2.task.ParallelTask;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.task.impl.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An event which starts player update tasks.
 *
 * @author Graham Edgecombe
 */
public class UpdateEvent extends Event {

	/**
	 * The cycle time, in milliseconds.
	 */
	public static final int CYCLE_TIME = 600;

	/**
	 * If no update has happened for more than this amount of time, the
	 * 'Updating' will be set as 'shutdown'.
	 */
	public static final int SHUTDOWN_TIMER = 10000;

	/**
	 * Creates the update event to cycle every 600 milliseconds.
	 */
	public UpdateEvent() {
		super(CYCLE_TIME,"updatevent");
	}

	/**
	 * Holds the time when the last update happened.
	 */
	private static long lastupdate = System.currentTimeMillis() + 60000;

	/**
	 * Updates the time when the last update happened.
	 */
	public static void updateTimer() {
		lastupdate = System.currentTimeMillis();
	}

	/**
	 * Used to see whether Updating has been stopped.
	 *
	 * @returns true if no update has occured for at least
	 * <code>SHUTDOWN_TIMER</code> seconds.
	 */
	public static boolean shutDown() {
		return (System.currentTimeMillis() - lastupdate > SHUTDOWN_TIMER);
	}


	@Override
	public void execute() {
		int npcscount = World.getWorld().getNPCs().size();
		int playercount = World.getWorld().getPlayers().size();
        for(NPC npc : World.getWorld().npcsWaitingList) {
			// npc.getWalkingQueue().walkingCheck();
			try {
				World.getWorld().removeFromWaiting(npc);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		World.getWorld().npcsWaitingList.clear();
		List<Task> tickTasks = new ArrayList<Task>(npcscount + playercount);
		List<Task> updateTasks = new ArrayList<Task>(playercount);
		List<Task> resetTasks = new ArrayList<Task>(npcscount + playercount);

		for(NPC npc : World.getWorld().getNPCs()) {
			try {
				tickTasks.add(new NPCTickTask(npc));
				resetTasks.add(new NPCResetTask(npc));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
		while(it$.hasNext()) {
			try {
				Player player = it$.next();
	            /*
				 * if(!player.getSession().isConnected()) { it$.remove(); } else
				 */
				if(player != null) {

                    //if(player.getSession().isConnected()) {
                        tickTasks.add(new PlayerTickTask(player));
                        updateTasks.add(new ConsecutiveTask(
                                new PlayerUpdateTask(player),
                                new NPCUpdateTask(player)));
                        resetTasks.add(new PlayerResetTask(player));
                    //} else {
                        if(player.getSession() != null && !player.getSession().isConnected()) {
                        	if(!player.loggedOut) {
                        		player.forceMessage("I have x-logged");
                        		World.getWorld().unregister(player);
                        	}
                        	player.loggedOut = true;
                        }
                       //it$.remove();
                    //}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		// ticks can no longer be parallel due to region code
		Task tickTask = new ConsecutiveTask(tickTasks);
		Task updateTask = new ParallelTask(updateTasks);
		Task resetTask = new ParallelTask(resetTasks);

		World.getWorld().submit(new ConsecutiveTask(tickTask, updateTask, resetTask));
	}
}
