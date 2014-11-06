package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 11/3/14
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class SaveSlayer extends SaveString {

    public SaveSlayer(String name) {
        super(name);
    }

    @Override
    public void setValue(Player player, String value) {
        player.getSlayer().load(value);
    }

    @Override
    public String getValue(Player player) {
        return player.getSlayer().toString();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
