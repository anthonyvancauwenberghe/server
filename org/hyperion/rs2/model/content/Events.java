package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;

/**
 * Created by Allen Kinzalow on 4/8/2015.
 *
 * Current Global Event
 */
public class Events {

    /**
     * Event Name
     */
    public static String eventName = "";

    /**
     * Is the event dangerous or safe?
     */
    public static boolean eventSafe = true;

    /**
     * The time in ms that the event is fired
     */
    public static long eventStartTime = 0;

    /**
     * The time until players can enter the event.
     */
    public static int eventTimeTillStart = 0;

    /**
     * The location of the event.
     */
    public static Location eventLocation = null;

    public static void resetEvent() {
        eventName = "";
        eventSafe = true;
        eventStartTime = 0;
        eventTimeTillStart = 0;
        eventLocation = null;
        for(Player player : World.getWorld().getPlayers()) {
            player.getQuestTab().sendUptime();
            player.getActionSender().sendString("cancel", 32456);
        }
    }

    public static boolean isEventActive() {
        return eventStartTime != 0;
    }

    public static void fireNewEvent(String name, boolean safe, int timeTillStart, Location location) {
        eventName = name;
        eventSafe = safe;
        eventTimeTillStart = timeTillStart;
        eventStartTime = System.currentTimeMillis();
        eventLocation = location;
        for(Player player : World.getWorld().getPlayers()) {
            player.getQuestTab().sendUptime();
            if(!player.hasJoinedHns())
                player.getActionSender().sendString(eventName + "," + eventSafe + "," + eventTimeTillStart, 32456);
        }
    }

    public static void joinEvent(Player player) {
        if(eventLocation == null || !isEventActive()) {
            player.getActionSender().sendMessage("There was an error joining this event, try again later.");
            player.getActionSender().sendString("cancel", 32456);
            return;
        }

        Magic.teleport(player, eventLocation, false);
        System.out.println("You teleport to the event: " + eventName);
        player.getActionSender().sendString("cancel", 32456);
    }

}
