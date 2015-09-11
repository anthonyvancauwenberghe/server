package org.hyperion.rs2.model.content.skill.agility;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.content.skill.agility.obstacles.*;

/**
 * Created by Gilles on 11/09/2015.
 */
public class Shortcuts extends Course {

    public Shortcuts() {
        super(0, 0);
        generateObstacles();
    }

    public void generateObstacles() {
        new ObstaclePipe(9293, 0, 75, Location.create(2886, 9799, 0), Location.create(2892, 9799, 0), 0, this, 0);
        new ObstaclePipe(9293, 0, 75, Location.create(2892, 9799, 0), Location.create(2886, 9799, 0), 0, this, 0);
    }
}
