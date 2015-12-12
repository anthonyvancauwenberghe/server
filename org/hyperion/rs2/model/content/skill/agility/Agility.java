package org.hyperion.rs2.model.content.skill.agility;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * Created by Gilles on 10/09/2015.
 */
public class Agility {

    private final Player player;

    private int gnomeCourseProgress = 0;
    private int wildernessCourseProgress = 0;

    private boolean isBusy = false;

    public Agility(final Player player) {
        this.player = player;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(final boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void appendHit(final int hit) {
        player.getCombat().hit(hit, player, false, Constants.DEFLECT);
    }

    public void progressGnomeCourse(final int progress, final Course course) {
        if(gnomeCourseProgress + 1 == progress)
            gnomeCourseProgress = progress;
        if(gnomeCourseProgress == course.getMaxCourseProgress()){
            player.getSkills().addExperience(Skills.AGILITY, course.getCourseBonusExp());
            player.sendMessage("You just completed the " + course.toString().toLowerCase() + "!");
            gnomeCourseProgress = 0;
        }
    }

    public void progressWildernessCourse(final int progress, final Course course) {
        if(wildernessCourseProgress + 1 == progress)
            wildernessCourseProgress = progress;
        if(wildernessCourseProgress == course.getMaxCourseProgress()){
            player.getSkills().addExperience(Skills.AGILITY, course.getCourseBonusExp());
            player.sendMessage("You just completed the " + course.toString().toLowerCase() + "!");
            wildernessCourseProgress = 0;
        }
    }
}
