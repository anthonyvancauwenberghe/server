package org.hyperion.rs2.model.content.specialareas.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;

import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/20/14
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PurePk extends SpecialArea {


    public static final int HEIGHT = 444;

    @Override public String canEnter(final Player player) {
        if(player.getSkills().getRealLevels()[Skills.DEFENCE] > 20)
            return "You need 20 def or less to enter this area";
        return "";
    }

    @Override public boolean inArea(final Player player) {
        return player.getLocation().getZ() == HEIGHT;
    }

    @Override public boolean isPkArea() {
        return false;
    }

    @Override public Location getDefaultLocation() {
        return Location.create(3087, 3515, HEIGHT);
    }

    @Override public boolean canSpawn() {
        return true;
    }
}
