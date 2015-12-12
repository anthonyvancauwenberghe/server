package org.hyperion.rs2.model.content.pvptasks;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.pvptasks.impl.MainTask;
import org.hyperion.rs2.model.content.pvptasks.impl.PureTask;
import org.hyperion.rs2.model.content.pvptasks.impl.ZerkTask;

public abstract class PvPTask {
    public static String toString(final PvPTask task) {
        if(task instanceof PureTask)
            return "pure";
        else if(task instanceof MainTask)
            return "main";
        else if(task instanceof ZerkTask)
            return "zerk";
        return "";
    }

    public static PvPTask toTask(final int i) {
        switch(i){
            case 1:
                return new PureTask();
            case 2:
                return new MainTask();
            case 3:
                return new ZerkTask();
            default:
                return null;
        }
    }

    public static int toInteger(final PvPTask task) {
        if(task instanceof PureTask)
            return 1;
        else if(task instanceof MainTask)
            return 2;
        else if(task instanceof ZerkTask)
            return 3;
        return 0;
    }

    public abstract boolean isTask(Player p, Player o);
}
