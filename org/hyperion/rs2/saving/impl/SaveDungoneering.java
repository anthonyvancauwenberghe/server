package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/27/15
 * Time: 5:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveDungoneering extends SaveString {

    public SaveDungoneering() {
        super("dungoneeringdata");
    }

    @Override
    public void setValue(Player player, String value) {
        try {
            player.getDungoneering().load(value);
        }catch(final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getValue(Player player) {
        return player.getDungoneering().save();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
