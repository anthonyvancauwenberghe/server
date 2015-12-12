package org.hyperion.rs2.model.content.transport;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class QuickTransport implements ContentTemplate {

    //hold on
    //let me explain first
    //i wanna add a few portals that just make u teleport when clicking on them
    //thats it
    public void QuickTeleport(final Player player, final int ObjectId) {
        switch(ObjectId){
            case 2156:
                Magic.teleport(player, 2958, 3196, 0, false); //To Rimmington
                break;
            case 2157:
                Magic.teleport(player, 2967, 3369, 0, false); //To Falador
                break;
            case 2158:
                break;
        }
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        if(type == 6){
            switch(a){
                case 2156:
                    Magic.teleport(player, 2958, 3196, 0, false); //To Rimmington
                    break;
                case 2157:
                    Magic.teleport(player, 2967, 3369, 0, false); //To Falador
                    break;
                case 2158:
                    break;
            }
        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {

    }

    @Override
    public int[] getValues(final int type) {
        if(type == 6){
            final int[] ids = {2156, 2157, 2158};
            return ids;
        }
        return null;
    }
}
