package org.hyperion.rs2.model.content.jge;

import org.hyperion.rs2.model.content.jge.entry.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 9/23/2015.
 */
public class JGrandExchange {

    private final Map<Integer, List<Entry>> map;

    public JGrandExchange(){
        map = new TreeMap<>();
    }

    public void add(final Entry entry){
        if(!map.containsKey(entry.itemId))
            map.put(entry.itemId, new ArrayList<>());
        map.get(entry.itemId).add(entry);
    }

    public void remove(final Entry entry){
        if(map.containsKey(entry.itemId))
            map.get(entry.itemId).remove(entry);
    }
}
