package org.hyperion.rs2.model.content.skill.agility.courses;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.obstacles.*;

/**
 * Created by Gilles on 11/09/2015.
 */
public class WildernessAgility extends Course {

    private Location[] rockStart = {
            Location.create(2995, 3937, 0),
            Location.create(2994, 3937, 0),
            Location.create(2993, 3937, 0)
    };
    private Location[] rockEnd = {
            Location.create(2995, 3933, 0),
            Location.create(2994, 3933, 0),
            Location.create(2993, 3933, 0)
    };


    public WildernessAgility() {
    super(22000, 5);
    generateObstacles();
    }

    public void generateObstacles() {
        new ObstaclePipe(2288, 3000, 55, Location.create(3004, 3937, 0), Location.create(3004, 3950, 0), 0, this, 1);
        new RopeSwing(2283, 4000, 55, Location.create(3005, 3953, 0), Location.create(3005, 3958, 0), Location.create(3004, 10354, 0), 0, 30, this, 2);
        new SteppingStone(2311, 4000, 55, Location.create(3002, 3960, 0), Location.create(2996, 3960, 0), Location.create(2999, 3957, 0), 3, 30, this, 3);
        new LogBalance(2297, 4000, 55, Location.create(3002, 3945, 0), Location.create(2994, 3945, 0), Location.create(2998, 10345, 0), 30, this, 4);
        new RockClimbing(2328, 3000, 55, rockStart, rockEnd, 0, this, 5);
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
