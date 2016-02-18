package org.hyperion.util;

import org.hyperion.Server;
import org.hyperion.rs2.model.EntityHandler;
import org.hyperion.rs2.model.World;

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
        Server.setUpdating(true);
        World.getPlayers().stream().filter(player -> player != null).forEach(EntityHandler::deregister);
        logger.info("The shutdown hook actions have been completed, shutting the server down...");
    }
}
