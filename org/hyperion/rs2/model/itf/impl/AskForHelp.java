package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;

/**
 * Created by Jet on 1/10/2015.
 */
public class AskForHelp extends Interface {

    private static final int ID = 2;

    public AskForHelp() {
        super(ID);
    }

    public void handle(final Player player, final Packet pkt) {

        final String title = pkt.getRS2String();
        final String text = pkt.getRS2String();

        

    }
}
