package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class ClimbNet extends Obstacle {
    private Location[]  start,
                        end;

    public ClimbNet(int objectId, int skillXp, int levelReq, Location[] start, Location[] end, int failRate, Course course, int progress) {
        super(objectId, 828, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean overCome(Player player) {
        if(!super.overCome(player))
            return false;
        for(int i = 0; i < start.length; i++) {
            if(player.getLocation().getX() == start[i].getX() && player.getLocation().getY() == start[i].getY()) {
                executeObject(player);
                return true;
            }
        }
        return true;
    }

    @Override
    public void succeed(Player player, int tick, String message) {
        super.succeed(player, 1, message);
        player.playAnimation(Animation.create(animId));
        World.getWorld().submit(new Event(700) {
            @Override
            public void execute() {
                int j = 0;
                for(int i = 0; i < start.length; i++) {
                    if(start[i].getX() == player.getLocation().getX() && start[i].getY() == player.getLocation().getY())
                        j = i;
                }
                player.setTeleportTarget(Location.create(end[j].getX(), end[j].getY(), end[j].getZ()));
                this.stop();
            }
        });
    }

    @Override
    public String toString() {
        return "Obstacle net";
    }

}
