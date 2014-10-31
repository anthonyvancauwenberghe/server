package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveInteger;

/**
 * Created by Jet on 10/29/2014.
 */
public class SaveMac extends SaveInteger {

    public SaveMac(){
        super("Mac");
    }

    public Integer getValue(final Player player){
        return player.getUID();
    }

    public int getDefaultValue(){
        return -1;
    }

    public void setValue(final Player player, final int value){

    }
}
