package org.hyperion.rs2;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.RS2CodecFactory;
import org.hyperion.rs2.task.impl.SessionClosedTask;
import org.hyperion.rs2.task.impl.SessionMessageTask;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.io.File;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class ConnectionHandler extends IoHandlerAdapter {

	public static HashMap<String, Object> blackList = new HashMap<String, Object>();


	/**
	 * The <code>GameEngine</code> instance.
	 */
	private final GameEngine engine = World.getWorld().getEngine();

	/**
	 * The debugger.
	 */
	private static final ConnectionDebugger debugger = new ConnectionDebugger();

	public static final File LOG_FILE = new File("./logs/closedsession.log");

	/*private static void logStackTrace(String username, Location location) {
	    List<String> logs = new LinkedList<String>();
		int distance = location.distance(lastLocation);
		lastLocation = location;
		logs.add(Time.getGMTDate() + "\t" + username + " closed session, distance: " + distance + "\n");
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			logs.add("\tat " + ste.getClassName() + "." + ste.getMethodName()
					+ "(" + ste.getFileName() + ":" + ste.getLineNumber()
					+ ")\n");
		}
		PlayerSaving.getSaving().saveLog(LOG_FILE, logs);
	}*/

	private static Location lastLocation = Location.create(0, 0, 0);

	private static LinkedList<Long> lastLogouts = new LinkedList<Long>();

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable)
			throws Exception {
		Object playerobject = session.getAttribute("player");
		if(playerobject != null) {
			Player player = (Player) playerobject;
            World.getWorld().unregister(player);
		} else
            session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
        final Player p = (Player) session.getAttribute("player");
        if(p != null){
            p.getExtraData().put("packetsRead", p.getExtraData().getInt("packetsRead")+1);
            p.getExtraData().put("packetCount", p.getExtraData().getInt("packetCount")+1);
            int packetCount = p.getExtraData().getInt("packetCount");
            if(packetCount > 50){
                p.sendf("@red@PLEASE STOP WHAT YOU'RE DOING OR YOU WILL BE KICKED!");
				if (p.getExtraData().getInt("packetCount") > 250) {
					long expiration_time = System.currentTimeMillis() + Time.ONE_MINUTE;
					World.getWorld().getBanManager().moderate("Server", p, 2, true, expiration_time, "Suspected layer 7 ddos.");
				}
				if(packetCount > 149) {
					System.out.printf("%s has a a %,d packet count, banning\n", p.getName(), p.getExtraData().getInt("packetCount"));
                	//session.close(false);
				}
                return;
            }
        }
        engine.pushTask(new SessionMessageTask(session, (Packet) message));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		/*long currentTime = System.currentTimeMillis();
		if(currentTime - lastLogouts.get(0) < 5000) {
			Object playerobject = session.getAttribute("player");
			if (playerobject != null) {
				Player player = (Player) playerobject;
				System.out.println("Connection closed too fast for "
						+ player.getName());
				logStackTrace(player.getName(),player.getLocation());
			} else {
				System.out.println("Playerobject is null in ConnectionHandler..");
			}
		}
		lastLogouts.add(currentTime);
		if(lastLogouts.size() > 20) {
			lastLogouts.remove(0);
		}*/
		engine.pushTask(new SessionClosedTask(session));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		Object playerobject = session.getAttribute("player");
		if(playerobject != null) {
			Player player = (Player) playerobject;
			System.out.println("Connection closed because its idle "
					+ player.getName());
			World.getWorld().unregister(player);
		} else
			session.close(false);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		// System.out.println("Session opened!");
		SocketAddress remoteAddress = session.getRemoteAddress();
		String remoteIp = remoteAddress.toString();
		String ip = remoteIp.split(":")[0];
		String shortIp = TextUtils.shortIp(remoteIp);
		if(! HostGateway.canEnter(shortIp)) {
			System.out.println("Cant enter hostgateway: " + shortIp);
			session.close(true);
			return;
		}
		LoginDebugger.getDebugger().log("\n Connection opened: " + remoteIp);
		session.setAttribute("remote", remoteAddress);
		session.getFilterChain().addFirst("protocol",
				new ProtocolCodecFilter(RS2CodecFactory.LOGIN));
		// engine.pushTask(new SessionOpenedTask(session));
	}

	static {
		CommandHandler
				.submit(new Command("dumpconnlogs", Rank.ADMINISTRATOR) {
					@Override
					public boolean execute(Player player, String input) {
						debugger.dumpLogs();
						return true;
					}
				});
	}

}
