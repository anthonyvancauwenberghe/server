package org.hyperion.rs2.model.content.pvptasks.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.pvptasks.PvPTask;
import org.hyperion.rs2.model.newcombat.Skills;

public class MainTask extends PvPTask {

    @Override
    public boolean isTask(final Player p, final Player o) {
        return p.getPvPTask() != null && p.getPvPTask() instanceof MainTask &&
                o.getSkills().getRealLevels()[Skills.DEFENCE] >= 80;
    }

}
