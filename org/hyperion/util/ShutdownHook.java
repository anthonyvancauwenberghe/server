package org.hyperion.util;

import org.hyperion.Server;
import org.hyperion.rs2.model.EntityHandler;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.skill.dungoneering.Dungeon;

import java.util.logging.Logger;

/**
 * Created by Gilles on 11/02/2016.
 */
public class ShutdownHook extends Thread {

    /**
     * The ShutdownHook logger to print out information.
     */
    private static final Logger logger = Logger.getLogger(ShutdownHook.class.getName());

    @Override
    public void run() {
        logger.info("The shutdown hook is processing all required actions...");
        World.getPlayers().forEach(Trade::declineTrade);
        Dungeon.activeDungeons.forEach(Dungeon::complete);
        Server.setUpdating(true);
        World.getPlayers().stream().filter(player -> player != null).forEach(EntityHandler::deregister);
        ClanManager.save();
        logger.info("The shutdown hook actions have been completed, shutting the server down...");
    }
}
