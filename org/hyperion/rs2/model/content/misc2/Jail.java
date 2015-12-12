package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class Jail implements ContentTemplate {

    public static final Location LOCATION = Location.create(2097, 4428, 0);

    public static boolean inJail(final Player player) {
        if(player.getLocation().getX() >= 2050 && player.getLocation().getX() <= 2130)
            if(player.getLocation().getY() >= 4400 && player.getLocation().getY() <= 4460)
                return true;
        return false;
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public int[] getValues(final int type) {
        // TODO Auto-generated method stub
        return null;
    }
    //4439,2089,2108,4419
}
