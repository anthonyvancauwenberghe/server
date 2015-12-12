package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/18/14
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SavePermExtraData extends SaveString {

    public SavePermExtraData() {
        super("permExtraData");
    }

    @Override
    public void setValue(final Player player, final String value) {
        player.getPermExtraData().parse(value);
    }

    @Override
    public String getValue(final Player player) {
        return player.getPermExtraData().getSaveableString();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
