package org.hyperion.rs2.model.content.skill.agility;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.agility.courses.GnomeStronghold;

import java.io.FileNotFoundException;

/**
 * Created by Gilles on 10/09/2015.
 */
public class Agility {

    private Player player;

    private int GnomeCourseProgress = 0;
    private int WildernessCourseProgress = 0;

    private boolean isBusy = false;

    public Agility(Player player) {
        this.player = player;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public void progressGnomeCourse(int progress, Course course) {
        if (GnomeCourseProgress + 1 == progress)
            GnomeCourseProgress = progress;
        if (GnomeCourseProgress == course.getMaxCourseProgress()) {
            player.getSkills().addExperience(Skills.AGILITY, course.getCourseBonusExp());
            player.sendMessage("You just completed the " + course.toString().toLowerCase() + "!");
            GnomeCourseProgress = 0;
        }
    }

    public void progressWildernessCourse(int progress, Course course) {
        if (WildernessCourseProgress + 1 == progress)
            WildernessCourseProgress = progress;
        if (WildernessCourseProgress == course.getMaxCourseProgress()) {
            player.getSkills().addExperience(Skills.AGILITY, course.getCourseBonusExp());
            player.sendMessage("You just completed the " + course.toString().toLowerCase() + "!");
            WildernessCourseProgress = 0;
        }
    }
}
