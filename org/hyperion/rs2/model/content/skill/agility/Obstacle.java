package org.hyperion.rs2.model.content.skill.agility;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.UpdateFlags;
import org.hyperion.rs2.model.World;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 10/09/2015.
 */
public class Obstacle {

    protected int objectId, animId, skillXp, levelReq, failRate, progress;
    protected Course course;

    public Obstacle(final int objectId, final int animId, final int levelReq, final int skillXp, final int failRate, final Course course, final int progress) {
        if(failRate > 100 || failRate < 0){
            this.failRate = 0;
            return;
        }else{
            this.failRate = failRate;
        }
        if(levelReq > 99 || levelReq < 0){
            this.levelReq = 0;
        }else{
            this.levelReq = levelReq;
        }
        this.objectId = objectId;
        this.animId = animId;
        this.skillXp = skillXp;
        this.course = course;
        this.progress = progress;
        course.addObstacle(this);
    }

    public static void reset(final Player player) {
        player.setBusy(false);
        player.getAgility().setBusy(false);
        player.getWalkingQueue().setRunningToggled(true);
        player.getAppearance().setWalkAnim(0x337); //default walk animation
        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
    }

    public static Location calculateMiddle(final Location start, final Location end) {
        int middleX = start.getX();
        int middleY = start.getY();

        if(start.getX() != end.getX()){
            if(start.getX() > end.getX())
                middleX = start.getX() - start.distance(end) / 2;
            else
                middleX = start.getX() + start.distance(end) / 2;
        }
        if(start.getY() != end.getY()){
            if(start.getY() > end.getY())
                middleY = start.getY() - start.distance(end) / 2;
            else
                middleY = start.getY() + start.distance(end) / 2;
        }
        return Location.create(middleX, middleY, start.getZ());
    }

    public int getObjectId() {
        return objectId;
    }

    public int getAnimId() {
        return animId;
    }

    public int getSkillXp() {
        return skillXp;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public int getFailRate() {
        return failRate;
    }

    public int getProgress() {
        return progress;
    }

    public Course getCourse() {
        return course;
    }

    public boolean overCome(final Player player) {
        if(player == null){
            return false;
        }
        if(player.isBusy()){
            return false;
        }
        if(player.getSkills().getLevel(Skills.AGILITY) < levelReq){
            player.sendMessage("You need an agility level of " + levelReq + " to use this " + (course.getClass() == Shortcuts.class ? "shortcut" : this.toString().toLowerCase()) + ".");
            return false;
        }
        if(player.getRandomEvent().skillAction())
            return false;
        return true;
    }

    public void executeObject(final Player player) {
        executeObject(player, "", "");
    }

    public void executeObject(final Player player, final int failRate) {
        executeObject(player, "", "");
    }

    public void executeObject(final Player player, final String succeedMessage, final String failMessage) {
        player.setBusy(true);
        player.getAgility().setBusy(true);
        if(failRate != 0){
            failRate -= ((player.getSkills().getLevel(Skills.AGILITY) - levelReq) / 2) * 10;
            if(Misc.random(1000) <= failRate){
                fail(player, 0, failMessage);
                return;
            }
        }
        succeed(player, 0, succeedMessage);
    }

    public void succeed(final Player player, final int tick, final String message) {
        World.getWorld().submit(new Event(tick * 600) {
            @Override
            public void execute() {
                player.getSkills().addExperience(Skills.AGILITY, skillXp);
                reset(player);
                if(!message.isEmpty())
                    player.sendMessage(message);
                player.setBusy(false);
                player.getAgility().setBusy(false);
                course.progressCourse(player, progress);
                this.stop();
            }
        });
    }

    public void fail(final Player player, final int tick, final String message) {
        World.getWorld().submit(new Event(tick * 600) {
            @Override
            public void execute() {
                reset(player);
                if(!message.isEmpty())
                    player.sendMessage(message);
                this.stop();
            }
        });
    }

    @Override
    public String toString() {
        return Misc.ucFirst(this.getClass().getSimpleName().toLowerCase());
    }


}
