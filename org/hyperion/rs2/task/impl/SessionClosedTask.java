package org.hyperion.rs2.task.impl;

import org.apache.mina.core.session.IoSession;
import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.Task;

import java.net.SocketAddress;
import java.util.logging.Logger;

/**
 * A task that is executed when a session is closed.
 *
 * @author Graham Edgecombe
 */
public class SessionClosedTask implements Task {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(SessionClosedTask.class.getName());

	/**
	 * The session that closed.
	 */
	private IoSession session;

	/**
	 * Creates the session closed task.
	 *
	 * @param session The session.
	 */
	public SessionClosedTask(IoSession session) {
		this.session = session;
	}

	@Override
	public void execute(GameEngine context) {
        if(session.containsAttribute("player")) {
            Player p = (Player) session.getAttribute("player");
            SocketAddress address = session.getRemoteAddress();
            if(p != null) {
            	if(!p.loggedOut) {
            		World.unregister(p);
            	}
            }else
        		System.out.println("Tried to logout player but the player was null..");
            if(address != null)
                System.out.println("Closing session: " + p.getName() + "," + address.toString());
        }
	}

}
