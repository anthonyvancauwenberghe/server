package org.hyperion.rs2.model.joshyachievementsv2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.hyperion.rs2.model.joshyachievementsv2.io.IO;
import org.hyperion.rs2.model.joshyachievementsv2.task.Task;

public final class Achievements{

    private static Achievements instance;

    private final Map<Integer, Achievement> map;
    private final Map<Class<? extends Task>, List<Task>> tasksByClass;

    public Achievements(final Collection<Achievement> collection){
        map = new TreeMap<>();

        tasksByClass = new HashMap<>();

        collection.forEach(this::add);
    }

    public int size(){
        return map.size();
    }

    public List<Task> tasks(final Class<? extends Task> taskClass){
        return tasksByClass.get(taskClass);
    }

    public Stream<Task> streamTasks(final Class<? extends Task> taskClass){
        return tasks(taskClass).stream();
    }

    public Collection<Achievement> values(){
        return map.values();
    }

    public Stream<Achievement> stream(){
        return values().stream();
    }

    public void add(final Achievement achievement){
        map.put(achievement.id, achievement);

        for(final Task t : achievement.tasks.values()){
            if(!tasksByClass.containsKey(t.getClass()))
                tasksByClass.put(t.getClass(), new ArrayList<>());
            tasksByClass.get(t.getClass()).add(t);
        }
    }

    public Achievement get(final int id){
        return map.get(id);
    }

    public static Achievements get(){
        return instance;
    }

    public static boolean load(){
        return (instance = IO.achievements.load()) != null;
    }
}