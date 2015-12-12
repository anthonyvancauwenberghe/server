package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.content.ContentTemplate;

public class Zanaris implements ContentTemplate {

    public final static Location LOCATION = Location.create(2395, 4455, 0);


    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int b, final int c, final int d) {
        if(type == 7 || type == 6){
            switch(a){
                case 12121:
                case 12120:
                    Bank.open(player, false);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 7 || type == 6){
            final int[] ids = {12121, 12120, 12355};
            return ids;
        }
        return null;
    }


}
