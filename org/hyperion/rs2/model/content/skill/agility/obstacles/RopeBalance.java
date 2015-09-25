package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class RopeBalance extends Obstacle {
    private Location    start,
                        end;

    public RopeBalance(int objectId, int skillXp, int levelReq, Location start, Location end, int failRate, Course course, int progress) {
        super(objectId, 762, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean overCome(Player player) {
        if(player.getLocation().getX() != start.getX() || player.getLocation().getY() != start.getY())
            return false;
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        if(failRate != 0) {
            player.sendMessage("You walk across the rope...");
            executeObject(player, "...And make it to the other side safely.", "...But lose you balance and fall!");
        } else {
            executeObject(player);
        }
        return true;
    }

    @Override
    public void succeed(Player player, int tick, String message) {
        super.succeed(player, start.distance(end) + 1, message);
        player.getActionSender().forceMovement(end.getX(), end.getY(), animId);
    }

    @Override
    public void fail(Player player, int tick, String message) {

    }

    @Override
    public String toString() {
        return "Balancing rope";
    }
}
