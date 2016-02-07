package org.hyperion.rs2;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.net.RS2CodecFactory;
import org.hyperion.rs2.task.impl.SessionClosedTask;
import org.hyperion.rs2.task.impl.SessionMessageTask;
import org.hyperion.rs2.util.TextUtils;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class ConnectionHandler extends IoHandlerAdapter {

	public static HashMap<String, Object> blackList = new HashMap<String, Object>();

	private final GameEngine engine = World.getWorld().getEngine();

	private static final ConnectionDebugger debugger = new ConnectionDebugger();

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		Object playerObject = session.getAttribute("player");
		if(playerObject != null && playerObject instanceof Player) {
			Player player = (Player)playerObject;
            World.getWorld().unregister(player);
		} else
            session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
        final Player player = (Player)session.getAttribute("player");
        if(player != null){
            player.getExtraData().put("packetsRead", player.getExtraData().getInt("packetsRead")+1);
			player.getExtraData().put("packetCount", player.getExtraData().getInt("packetCount")+1);
            int packetCount = player.getExtraData().getInt("packetCount");
            if(packetCount > 35){
                player.sendImportantMessage("PLEASE STOP WHAT YOU'RE DOING OR YOU WILL BE KICKED!");
				if (player.getExtraData().getInt("packetCount") > 40) {
					player.getSession().close(false);
					PunishmentManager.getInstance().add(new Punishment(player, Combination.of(Target.SPECIAL, Type.BAN), org.hyperion.rs2.model.punishment.Time.create(1, TimeUnit.MINUTES), "Suspected layer 7 ddos."));
				}
				if(packetCount > 39) {
					System.out.printf("%s has a a %,d packet count, banning\n", player.getName(), player.getExtraData().getInt("packetCount"));
					session.close(false);
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
}
