package org.hyperion.rs2.model.itf.impl;


import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/29/15
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemContainer extends Interface {

    public ItemContainer() {
        super(10);
    }

    @Override
    public void handle(Player player, Packet pkt) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
