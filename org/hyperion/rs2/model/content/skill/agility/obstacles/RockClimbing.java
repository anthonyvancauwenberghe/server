package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 12/09/2015.
 */
public class RockClimbing extends Obstacle {
    private final Location[] start;
    private final Location[] end;

    public RockClimbing(final int objectId, final int skillXp, final int levelReq, final Location[] start, final Location[] end, final int failRate, final Course course, final int progress) {
        super(objectId, 844, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean overCome(final Player player) {
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        for(int i = 0; i < start.length; i++){
            if(player.getLocation().getX() == start[i].getX() && player.getLocation().getY() == start[i].getY()){
                executeObject(player);
            }
        }
        return true;

    }

    @Override
    public void succeed(final Player player, final int tick, final String message) {
        int j = 0;
        for(int i = 0; i < start.length; i++){
            if(start[i].getX() == player.getLocation().getX() && start[i].getY() == player.getLocation().getY())
                j = i;
        }
        player.getActionSender().forceMovement(end[j].getX(), end[j].getY(), animId);
        super.succeed(player, start[j].distance(end[j]) + 1, message);
    }

    @Override
    public void fail(final Player player, final int tick, final String message) {
        int j = 0;
        for(int i = 0; i < start.length; i++){
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
