package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 11/09/2015.
 */
public class RopeSwing extends Obstacle {
    private final Location start;
    private final Location end;
    private final Location fail;
    private final int direction;

    public RopeSwing(final int objectId, final int skillXp, final int levelReq, final Location start, final Location end, final Location fail, final int direction, final int failRate, final Course course, final int progress) {
        super(objectId, 751, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
        this.direction = direction;
        this.fail = fail;
    }

    @Override
    public boolean overCome(final Player player) {
        if(player.getLocation().getX() != start.getX() || player.getLocation().getY() != start.getY())
            return false;
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        if(failRate != 0){
            player.sendMessage("You ready yourself to swing the rope...");
            executeObject(player, "...And make it to the other side safely.", "...But slip and fall!");
        }else{
            executeObject(player);
        }
        return true;
    }

    @Override
    public void succeed(final Player player, final int tick, final String message) {
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
                if(progress == start.distance(end)){
                    player.getWalkingQueue().setRunningToggled(true);
                    player.getAppearance().setAnimations(a, animId, animId);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                }else if(progress == start.distance(end) - 1){
                    if(direction == 0)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() + 1);
                    if(direction == 1)
                        player.getActionSender().forceMovement(player.getLocation().getX() + 1, player.getLocation().getY());
                    if(direction == 2)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() - 1);
                    if(direction == 3)
                        player.getActionSender().forceMovement(player.getLocation().getX() - 1, player.getLocation().getY());
                }else if(progress == start.distance(end) - 2){
                    player.getActionSender().forceMovement(end.getX(), end.getY());
                }else if(progress == 0){
                    player.getSkills().addExperience(Skills.AGILITY, skillXp);
                    reset(player);
                    if(!message.isEmpty())
                        player.sendMessage(message);
                    player.getAppearance().setAnimations(a, b, c);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    course.progressCourse(player, getProgress());
                    stop();
                }
                progress--;
            }
        });
    }

    @Override
    public void fail(final Player player, final int tick, final String message) {
        World.getWorld().submit(new Event(600) {
            int progress = start.distance(end);

            @Override
            public void execute() {
                if(progress == start.distance(end)){
                    player.getWalkingQueue().setRunningToggled(true);
                }else if(progress == start.distance(end) - 1){
                    if(direction == 0)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() + 1, 768);
                    if(direction == 1)
                        player.getActionSender().forceMovement(player.getLocation().getX() + 1, player.getLocation().getY(), 768);
                    if(direction == 2)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() - 1, 768);
                    if(direction == 3)
                        player.getActionSender().forceMovement(player.getLocation().getX() - 1, player.getLocation().getY(), 768);
                    if(!message.isEmpty())
                        player.sendMessage(message);
                }else if(progress == start.distance(end) - 2){
                    player.setTeleportTarget(Location.create(fail.getX(), fail.getY(), fail.getZ()));
                    player.getAgility().appendHit(Misc.random(3) + 3);
                }else if(progress == 0){
                    reset(player);
                    stop();
                }
                progress--;
            }
        });
    }

    @Override
    public String toString() {
        return "Rope swing";
    }
}
