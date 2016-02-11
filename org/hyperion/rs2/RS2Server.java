package org.hyperion.rs2;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.hyperion.Server;
import org.hyperion.Uptime;
import org.hyperion.rs2.model.World;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * Starts everything else including MINA and the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class RS2Server {

	/**
	 * The version.
	 */
	public static final int VERSION = Server.getConfig().getInteger("version");

	/**
	 * The port to listen on.
	 */
	public static final int PORT = Server.getConfig().getInteger("port");

	/**
	 * The <code>IoAcceptor</code> instance.
	 */
	private final IoAcceptor acceptor = new NioSocketAcceptor();

	/**
	 * The <code>GameEngine</code> instance.
	 */
	private static final GameEngine engine = new GameEngine();

	/**
	 * Creates the server and the <code>GameEngine</code> and initializes the
	 * <code>World</code>.
	 *
	 * @throws IOException            if an I/O error occurs loading the world.
	 * @throws ClassNotFoundException if a class the world loads was not found.
	 * @throws IllegalAccessException if a class loaded by the world was not accessible.
	 * @throws InstantiationException if a class loaded by the world was not created.
	 */
	public RS2Server() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		World.getWorld().init(engine);
		acceptor.setHandler(new ConnectionHandler());
		//acceptor.getFilterChain().addFirst("throttleFilter", new ConnectionThrottleFilter());
	}

	/**
	 * Binds the server to the specified port.
	 *
	 * @param port The port to bind to.
	 * @return The server instance, for chaining.
	 * @throws IOException
	 */
	public RS2Server bind(int port) throws IOException {
		//logger.info("Binding to port : " + port + "...");
		acceptor.bind(new InetSocketAddress(port));
		return this;
	}

	/**
	 * Starts the <code>GameEngine</code>.
	 *
	 * @throws ExecutionException if an error occured during background loading.
	 */
	public void start() throws ExecutionException {
		try {
			engine.start();
			bind(PORT);
			//logger.info("Ready");
			System.out.println("--" + Server.NAME + " Loaded in " + (System.currentTimeMillis() - Uptime.SERVER_STARTUP) + "ms --");
			//TextUtils.printItemNames();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the <code>GameEngine</code>.
	 *
	 * @return The game engine.
	 */
	public static GameEngine getEngine() {
		return engine;
	}

}
