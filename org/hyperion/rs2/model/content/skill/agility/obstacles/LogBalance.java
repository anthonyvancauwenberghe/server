package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class LogBalance extends Obstacle {
    private Location    start,
                        end;

    public LogBalance(int objectId, int skillXp, int levelReq, Location start, Location end, int failRate, Course course, int progress) {
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
            player.sendMessage("You walk across the log...");
            executeObject(player, "...And make it to the other side safely.", "...But slip and fall!");
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
        super.fail(player, start.distance(end) + 1, message);
        Location middle = Obstacle.calculateMiddle(start, end);
        player.getActionSender().forceMovement(middle.getX(), middle.getY(), animId);
    }

    @Override
    public String toString() {
        return "Balancing log";
    }
}
