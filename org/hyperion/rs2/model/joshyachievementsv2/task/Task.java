package org.hyperion.rs2.model.joshyachievementsv2.task;

import java.util.function.Predicate;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.joshyachievementsv2.constraint.Constraints;

public abstract class Task{

    public static class Filter<T extends Task> implements Predicate<T>{

        public final Class<T> clazz;

        public Filter(final Class<T> clazz){
            this.clazz = clazz;
        }

        public boolean test(final T t){
            return t.getClass().equals(clazz);
        }
    }

    public int achievementId;

    public final int id;
    public final int threshold;

    public String desc;

    public final Constraints constraints;

    public int preTaskId;

    protected Task(final int id, final int threshold){
        this.id = id;
        this.threshold = threshold;

        constraints = new Constraints();
    }

    public boolean canProgress(final int currentProgress, final int progress){
        return true;
    }

    public boolean finished(final int progress){
        return progress >= threshold;
    }

    public boolean hasPreTask(){
        return preTask() != null;
    }

    public Task preTask(){
        return Achievements.get().get(achievementId).tasks.get(preTaskId);
    }
}
