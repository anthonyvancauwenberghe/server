package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class ObstaclePipe extends Obstacle {
    private Location start,
            end;

    public ObstaclePipe(int objectId, int skillXp, int levelReq, Location start, Location end, int failRate, Course course, int progress) {
        super(objectId, 844, levelReq, skillXp, failRate, course, progress);
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
        executeObject(player);
        return true;
    }

    @Override
    public void succeed(Player player, int tick, String message) {
        if(start.distance(end) < 7) {
            super.succeed(player, start.distance(end) + 1, message);
            player.getActionSender().forceMovement(end.getX(), end.getY(), animId);
        } else {
            World.submit(new Task(600) {
                int progress = 7;
                @Override
                public void execute() {
                    if(progress == 7) {
                        int coordX = start.getX();
                        int coordY = start.getY();

                        if (start.getX() != end.getX()) {
                            if (start.getX() > end.getX())
                                coordX = start.getX() - 3;
                            else
                                coordX = start.getX() + 3;
                        }
                        if (start.getY() != end.getY()) {
                            if (start.getY() > end.getY())
                                coordY = start.getY() - 3;
                            else
                                coordY = start.getY() + 3;
                        }
                        player.getActionSender().forceMovement(coordX, coordY, animId);
                    }
                    else if(progress == 3) {
                        int coordX = end.getX();
                        int coordY = end.getY();

                        if (start.getX() != end.getX()) {
                            if (start.getX() > end.getX())
                                coordX = end.getX() + 3;
                            else
                                coordX = end.getX() - 3;
                        }
                        if (start.getY() != end.getY()) {
                            if (start.getY() > end.getY())
                                coordY = end.getY() + 3;
                            else
                                coordY = end.getY() - 3;
                        }
                        player.setTeleportTarget(Location.create(coordX, coordY, player.getLocation().getZ()));
                    }
                    else if(progress == 2 || progress == 1) {
                        player.getActionSender().forceMovement(end.getX(), end.getY(), animId);
                    }
                    else if(progress == 0) {
                        player.setTeleportTarget(end);
                        reset(player);
                        player.getSkills().addExperience(Skills.AGILITY, skillXp);
                        course.progressCourse(player, getProgress());
                        stop();
                    }
                    progress--;
                }
            });
        }
    }

    @Override
    public void fail(Player player, int tick, String message) {
        super.fail(player, start.distance(end) + 1, message);
        Location middle = Obstacle.calculateMiddle(start, end);
        player.getActionSender().forceMovement(middle.getX(), middle.getY(), animId);
    }

    @Override
    public String toString() {
        return "Tunnel pipe";
    }
}
