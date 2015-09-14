package org.hyperion.rs2.model.content.bounty;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.util.Time;

import java.util.List;

/**
 * Created by Gilles on 14/09/2015.
 */
public class BountyHunterLogout extends Event {

    public BountyHunterLogout() {
        super(Time.ONE_MINUTE * 20);
    }

    private static int logoutLimit = 5;

    private static List<String> logoutsInWild;
    private static List<Player> blocked;

    public void addLogout(Player player) {
        logoutsInWild.add(player.getName());
    }

    public List getLogouts() {
        return logoutsInWild;
    }

    public static boolean isBlocked(Player player) {
        return blocked.contains(player);
    }

    public static void playerLogout(Player player) {
        int logouts = 0;
        for(int i = 0; i < logoutsInWild.size(); i++) {
            if(logoutsInWild.get(i).equalsIgnoreCase(player.getName())) {
                logouts++;
            }
        }
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
