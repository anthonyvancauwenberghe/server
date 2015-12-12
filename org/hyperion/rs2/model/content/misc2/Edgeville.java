package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class Edgeville implements ContentTemplate {


    public final static Location LOCATION = Location.create(3087, 3491, 0);

    public static NPC DICER = World.getWorld().getNPCManager().addNPC(3089, 3485, 0, 2999, -1);

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
