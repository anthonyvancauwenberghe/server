package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Position;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class ClimbNet extends Obstacle {
    private Position[]  start,
                        end;

    public ClimbNet(int objectId, int skillXp, int levelReq, Position[] start, Position[] end, int failRate, Course course, int progress) {
        super(objectId, 828, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean overCome(Player player) {
        if(!super.overCome(player))
            return false;
        for(int i = 0; i < start.length; i++) {
            if(player.getPosition().getX() == start[i].getX() && player.getPosition().getY() == start[i].getY()) {
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
        World.submit(new Task(700) {
            @Override
            public void execute() {
                int j = 0;
                for(int i = 0; i < start.length; i++) {
                    if(start[i].getX() == player.getPosition().getX() && start[i].getY() == player.getPosition().getY())
                        j = i;
                }
                player.setTeleportTarget(Position.create(end[j].getX(), end[j].getY(), end[j].getZ()));
                this.stop();
            }
        });
    }

    @Override
    public String toString() {
        return "Obstacle net";
    }

}
