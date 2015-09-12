package org.hyperion.rs2.model.content.skill.agility.obstacles;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.Obstacle;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 12/09/2015.
 */
public class SteppingStone extends Obstacle {
    private Location start,
            end,
            fail;
    private int direction;

    public SteppingStone(int objectId, int skillXp, int levelReq, Location start, Location end, Location fail, int direction, int failRate, Course course, int progress) {
        super(objectId, 769, levelReq, skillXp, failRate, course, progress);
        this.start = start;
        this.end = end;
        this.direction = direction;
        this.fail = fail;
    }

    @Override
    public boolean overCome(Player player) {
        if(player.getLocation().getX() != start.getX() || player.getLocation().getY() != start.getY())
            return false;
        if(!super.overCome(player))
            return false;
        player.getWalkingQueue().setRunningToggled(false);
        if(failRate != 0) {
            player.sendMessage("You carefully start crossing the stepping stones...");
            executeObject(player, "...And safely cross to the other side.", "...But slip and fall in the lava!");
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
        player.getAppearance().setAnimations(a, animId, animId);
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);

        World.getWorld().submit(new Event(600) {
            int progress = start.distance(end) * 3;
            @Override
            public void execute() {
                if(progress == 0) {
                    player.getSkills().addExperience(Skills.AGILITY, skillXp);
                    reset(player);
                    player.getAppearance().setAnimations(a, b, c);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    if(!message.isEmpty())
                        player.sendMessage(message);
                    course.progressCourse(player, getProgress());
                    stop();
                }
                else if(0 == progress %3) {
                    if(direction == 0)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() + 1);
                    if(direction == 1)
                        player.getActionSender().forceMovement(player.getLocation().getX() + 1, player.getLocation().getY());
                    if(direction == 2)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() - 1);
                    if(direction == 3)
                        player.getActionSender().forceMovement(player.getLocation().getX() - 1, player.getLocation().getY());
                }
                progress--;
            }
        });
    }

    @Override
    public void fail(Player player, int tick, String message) {
        final int a = player.getAppearance().getStandAnim();
        final int b = player.getAppearance().getWalkAnim();
        final int c = player.getAppearance().getRunAnim();
        player.getAppearance().setAnimations(a, animId, animId);
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);

        World.getWorld().submit(new Event(600) {
            int progress = start.distance(calculateMiddle(start, end)) * 3;

            @Override
            public void execute() {
                if (progress == 0) {
                    player.setTeleportTarget(Location.create(fail.getX(), fail.getY(), fail.getZ()));
                    player.getAgility().appendHit(Misc.random(3) + 3);
                    reset(player);
                    player.getAppearance().setAnimations(a, b, c);
                    player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                    stop();
                }
                else if (player.getLocation().getX() == calculateMiddle(start, end).getX() && player.getLocation().getY() == calculateMiddle(start, end).getY()) {
                    player.playAnimation(Animation.create(770));
                    if (!message.isEmpty())
                        player.sendMessage(message);
                }
                else if (0 == progress % 3) {
                    if (direction == 0)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() + 1);
                    if (direction == 1)
                        player.getActionSender().forceMovement(player.getLocation().getX() + 1, player.getLocation().getY());
                    if (direction == 2)
                        player.getActionSender().forceMovement(player.getLocation().getX(), player.getLocation().getY() - 1);
                    if (direction == 3)
                        player.getActionSender().forceMovement(player.getLocation().getX() - 1, player.getLocation().getY());
                }
                progress--;
            }
        });
    }

    @Override
    public String toString() {
        return "Stepping stones";
    }
}
