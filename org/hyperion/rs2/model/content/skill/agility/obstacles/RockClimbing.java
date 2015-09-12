package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 12/09/2015.
 */
public class RockClimbing extends Obstacle {
    private Location[] start,
            end;

    public RockClimbing(int objectId, int skillXp, int levelReq, Location[] start, Location[] end, int failRate, Course course, int progress) {
        super(objectId, 844, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean overCome(Player player) {
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        for(int i = 0; i < start.length; i++) {
            if(player.getLocation().getX() == start[i].getX() && player.getLocation().getY() == start[i].getY()) {
                executeObject(player);
            }
        }
        return true;

    }

    @Override
    public void succeed(Player player, int tick, String message) {
        int j = 0;
        for(int i = 0; i < start.length; i++) {
            if(start[i].getX() == player.getLocation().getX() && start[i].getY() == player.getLocation().getY())
                j = i;
        }
        player.getActionSender().forceMovement(end[j].getX(), end[j].getY(), animId);
        super.succeed(player, start[j].distance(end[j]) + 1, message);
    }

    @Override
    public void fail(Player player, int tick, String message) {
        int j = 0;
        for(int i = 0; i < start.length; i++) {
            if(start[i].getX() == player.getLocation().getX() && start[i].getY() == player.getLocation().getY())
                j = i;
        }
        super.fail(player, start[j].distance(end[j]) + 1, message);
    }

    @Override
    public String toString() {
        return "Rocks";
    }
}
