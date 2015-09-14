package org.hyperion.rs2.model.content.bounty;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.util.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 14/09/2015.
 */
public class BountyHunterLogout extends Event {

    public BountyHunterLogout() {
        super(Time.ONE_MINUTE * 20);
    }

    private static int logoutLimit = 2;

    private static List<String> logoutsInWild = new ArrayList();
    private static List<Player> blocked = new ArrayList();

    public static void addLogout(Player player) {
        logoutsInWild.add(player.getName());
        System.out.println("Added " + player.getSafeDisplayName() + " to the logouts.");
    }

    public List getLogouts() {
        return logoutsInWild;
    }

    public static boolean isBlocked(Player player) {
        if(player == null)
            return false;
        return blocked.contains(player);
    }

    public static void playerLogout(Player player) {
        addLogout(player);
        int logouts = 0;
        for(int i = 0; i < logoutsInWild.size(); i++) {
            if(logoutsInWild.get(i).equalsIgnoreCase(player.getName())) {
                logouts++;
            }
        }
        System.out.println(player.getSafeDisplayName() + " is on the list " + logouts + " times.");
        if(logouts >= logoutLimit) {
            if(!blocked.contains(player))
                blocked.add(player);
        }
    }

    public void execute() {
        logoutsInWild.clear();
        blocked.clear();
        System.out.println("Bounty hunter blocklist has been reset.");
    }

}
