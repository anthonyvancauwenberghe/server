package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/8/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveCustomSet extends SaveString {

    public SaveCustomSet() {
        super("customSetData");
    }


    @Override
    public void setValue(final Player player, final String value) {
        player.getCustomSetHolder().parse(value);
    }

    @Override
    public String getValue(final Player player) {
        return player.getCustomSetHolder().toString();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
