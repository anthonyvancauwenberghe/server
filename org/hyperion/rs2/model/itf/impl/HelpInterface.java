package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.net.Packet;

/**
 * Created by Jet on 1/10/2015.
 */
public class HelpInterface extends Interface {

    private static final int ID = 3;

    public HelpInterface() {
        super(ID);
    }

    public void handle(final Player player, final Packet pkt) {

        final int id = pkt.getInt();

        System.out.println("ID is: "+id);

        World.getWorld().getTicketManager().assist(player, id);

    }

    static {
        CommandHandler.submit(new Command("checktickets", Rank.HELPER) {
            @Override
            public boolean execute(final Player player, final String input) {
                World.getWorld().getTicketManager().display(player);
                player.write(Interface.createStatePacket(SHOW, ID));
                return true;
            }
        });
    }


}
