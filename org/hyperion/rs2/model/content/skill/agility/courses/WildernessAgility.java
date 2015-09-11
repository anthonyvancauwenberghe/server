package org.hyperion.rs2.model.content.skill.agility.courses;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.obstacles.*;

/**
 * Created by Gilles on 11/09/2015.
 */
public class WildernessAgility extends Course {

    public WildernessAgility() {
    super(10000, 2);
    generateObstacles();
}

    public void generateObstacles() {
        /*
        new ObstaclePipe(2288, 0, 55, Location.create(3004, 3937, 0), Location.create(3004, 3950, 0), 0, this, 1);
        new RopeSwing(2283, 0, 55, Location.create(3005, 3953, 0), Location.create(3005, 3958, 0), 0, 40, this, 2);
        */
    }

    @Override
    public void progressCourse(Player player, int progress) {
        player.getAgility().progressWildernessCourse(progress, this);
    }

    @Override
    public String toString() {
        return "Wilderness agility course";
    }
}
