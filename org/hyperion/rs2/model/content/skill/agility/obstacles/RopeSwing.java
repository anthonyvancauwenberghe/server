package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;

/**
 * Created by Gilles on 11/09/2015.
 */
public class RopeSwing extends Obstacle{
    private Location start,
            end;
    private int direction;

    public RopeSwing(int objectId, int skillXp, int levelReq, Location start, Location end, int direction, int failRate, Course course, int progress) {
        super(objectId, 751, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
        this.direction = direction;
    }

    @Override
    public boolean overCome(Player player) {
        if(player.getLocation().getX() != start.getX() || player.getLocation().getY() != start.getY())
            return false;
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        if(failRate != 0) {
            player.sendMessage("You ready yourself to swing the rope...");
            executeObject(player, "...And make it to the other side safely.", "...But slip and fall!");
        } else {
            executeObject(player);
        }
        return true;
    }

    @Override
    public void succeed(Player player, int tick, String message) {
        final int a = player.getAppearance().getStandAnim();
        final int b = player.getAppearance().getWalkAnim();
        final int c = player.getAppearance().getRunAnim();
        if(direction == 0)
            player.face(Location.create(player.getLocation().getX() + 1, player.getLocation().getY(), player.getLocation().getZ()));
        if(direction == 1)
            player.face(Location.create(player.getLocation().getX(), player.getLocation().getY() - 1, player.getLocation().getZ()));
        if(direction == 2)
            player.face(Location.create(player.getLocation().getX() - 1, player.getLocation().getY(), player.getLocation().getZ()));
        if(direction == 3)
            player.face(Location.create(player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ()));

        World.getWorld().submit(new Event(600) {
            int progress = start.distance(end);
            @Override
            public void execute() {
                if(progress == start.distance(end)) {
                    player.getWalkingQueue().setRunningToggled(true);
                    player.getAppearance().setAnimations(a, animId, animId);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                }
                else if(progress == start.distance(end) - 1) {
                    if(direction == 0)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() + 1);
                    if(direction == 1)
                        player.getActionSender().forceMovement(player.getLocation().getX() + 1, player.getLocation().getY());
                    if(direction == 2)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() - 1);
                    if(direction == 3)
                        player.getActionSender().forceMovement(player.getLocation().getX() - 1, player.getLocation().getY());
                }
                else if(progress == start.distance(end) - 2) {
                    player.getActionSender().forceMovement(end.getX(), end.getY());
                }
                else if(progress == 0) {
                    player.getSkills().addExperience(Skills.AGILITY, skillXp);
                    reset(player);
                    if(!message.isEmpty())
                        player.sendMessage(message);
                    player.getAppearance().setAnimations(a, b, c);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    player.setBusy(false);
                    player.getAgility().setBusy(false);
                    course.progressCourse(player, progress);
                    stop();
                }
                progress--;
            }
        });
    }

    @Override
    public void fail(Player player, int tick, String message) {
        super.fail(player, start.distance(end) + 1, message);
        Location middle = Obstacle.calculateMiddle(start, end);
        player.getActionSender().forceMovement(middle.getX(), middle.getY(), animId);
    }

    @Override
    public String toString() {
        return "Rope swing";
    }
}
