package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class Lumbridge implements ContentTemplate {

    public final static Location LOCATION = Location.create(3221, 3218, 0);

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

}
