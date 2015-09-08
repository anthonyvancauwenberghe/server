package org.hyperion.rs2.model.joshyachievementsv2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hyperion.rs2.model.Player;

public class Instructions{

    public final List<String> list;

    public Instructions(final Collection<String> collection){
        this.list = new ArrayList<>(collection);
    }

    public Instructions(){
        this(new ArrayList<>());
    }

    public void add(final String line){
        list.add(line);
    }

    public void instruct(final Player player){
        list.forEach(player::sendMessage);
    }
}
