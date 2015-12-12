package org.hyperion.rs2.model.content.skill.agility.courses;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.agility.Course;
import org.hyperion.rs2.model.content.skill.agility.obstacles.ClimbBranch;
import org.hyperion.rs2.model.content.skill.agility.obstacles.ClimbNet;
import org.hyperion.rs2.model.content.skill.agility.obstacles.LogBalance;
import org.hyperion.rs2.model.content.skill.agility.obstacles.ObstaclePipe;
import org.hyperion.rs2.model.content.skill.agility.obstacles.RopeBalance;

/**
 * Created by Gilles on 10/09/2015.
 */
public class GnomeStronghold extends Course {
    private final static int EXPMULTIPLIER = (int) (Constants.XPRATE * 1.75) * 9;
    public static Location location = Location.create(2480, 3437, 0);

    Location[] net1Start = {Location.create(2476, 3426, 0), Location.create(2475, 3426, 0),
            Location.create(2474, 3426, 0), Location.create(2473, 3426, 0), Location.create(2472, 3426, 0),
            Location.create(2471, 3426, 0)};
    Location[] net1End = {Location.create(2476, 3424, 1), Location.create(2475, 3424, 1),
            Location.create(2474, 3424, 1), Location.create(2473, 3424, 1), Location.create(2472, 3424, 1),
            Location.create(2471, 3424, 1)};
    Location[] net2Start = {Location.create(2483, 3425, 0), Location.create(2484, 3425, 0),
            Location.create(2485, 3425, 0), Location.create(2486, 3425, 0), Location.create(2487, 3425, 0),
            Location.create(2488, 3425, 0),};
    Location[] net2End = {Location.create(2483, 3427, 0), Location.create(2484, 3427, 0),
            Location.create(2485, 3427, 0), Location.create(2486, 3427, 0), Location.create(2487, 3427, 0),
            Location.create(2488, 3427, 0)};
    Location[] branch1Start = {Location.create(2474, 3422, 1), Location.create(2473, 3423, 1),
            Location.create(2472, 3422, 1)};
    Location[] branch2Start = {Location.create(2486, 3420, 2), Location.create(2485, 3419, 2),
            Location.create(2486, 3418, 2)};
    Location[] branch3Start = {Location.create(2486, 3420, 2), Location.create(2486, 3418, 2),
            Location.create(2487, 3421, 2), Location.create(2488, 3420, 2),};

    public GnomeStronghold() {
        super(60 * EXPMULTIPLIER, 7);
        generateObstacles();
    }

    public void generateObstacles() {
        new LogBalance(2295, 10 * EXPMULTIPLIER, 1, Location.create(2474, 3436, 0), Location.create(2474, 3429, 0), 0, this, 1);
        new ClimbNet(2285, 15 * EXPMULTIPLIER, 1, net1Start, net1End, 0, this, 2);
        new ClimbBranch(2313, 30 * EXPMULTIPLIER, 1, branch1Start, Location.create(2473, 3420, 2), 0, this, 3);
        new RopeBalance(2312, 10 * EXPMULTIPLIER, 1, Location.create(2477, 3420, 2), Location.create(2483, 3420, 2), 0, this, 4);
        new ClimbBranch(2314, 30 * EXPMULTIPLIER, 1, branch2Start, Location.create(2487, 3420, 0), 0, this, 5);
        new ClimbBranch(2315, 30 * EXPMULTIPLIER, 1, branch3Start, Location.create(2487, 3420, 0), 0, this, 5);
        new ClimbNet(2286, 20 * EXPMULTIPLIER, 1, net2Start, net2End, 0, this, 6);
        new ObstaclePipe(154, 10 * EXPMULTIPLIER, 1, Location.create(2484, 3430, 0), Location.create(2484, 3437, 0), 0, this, 7);
        new ObstaclePipe(4058, 10 * EXPMULTIPLIER, 1, Location.create(2487, 3430, 0), Location.create(2487, 3437, 0), 0, this, 7);
    }

    @Override
    public void progressCourse(final Player player, final int progress) {
        player.getAgility().progressGnomeCourse(progress, this);
    }

    @Override
    public String toString() {
        return "Gnome stronghold agility course";
    }
}

