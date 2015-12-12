package org.hyperion.rs2.model.content.skill.agility;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.ArrayUtils;
import org.hyperion.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 10/09/2015.
 */
public class Course implements ContentTemplate {

    private static final List<Obstacle> obstacles = new ArrayList();
    private static final List obstacleId = new ArrayList();
    private int courseBonusExp, maxCourseProgress;

    public Course() {
    }

    public Course(final int courseBonusExp, final int maxCourseProgress) {
        this.courseBonusExp = courseBonusExp;
        this.maxCourseProgress = maxCourseProgress;
    }

    public static void addObstacle(final Obstacle obstacle) {
        obstacles.add(obstacle);
        obstacleId.add(obstacle.objectId);
    }

    public int getMaxCourseProgress() {
        return maxCourseProgress;
    }

    public int getCourseBonusExp() {
        return courseBonusExp;
    }

    public void progressCourse(final Player player, final int progress) {
    }

    @Override
    public int[] getValues(final int type) {
        if(type == ContentManager.OBJECT_CLICK1){
            return ArrayUtils.fromList(obstacleId);
        }
        return null;
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int objId, final int x, final int y, final int d) {
        boolean found = false;
        for(int i = 0; i < obstacles.size(); i++){
            if(objId == obstacles.get(i).objectId){
                obstacles.get(i).overCome(player);
                found = true;
            }
        }
        return found;
    }

    @Override
    public String toString() {
        return Misc.ucFirst(this.getClass().getSimpleName().toLowerCase());
    }
}
