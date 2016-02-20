
		package org.hyperion.rs2;

		import org.apache.mina.core.service.IoHandlerAdapter;
		import org.apache.mina.core.session.IdleStatus;
		import org.apache.mina.core.session.IoSession;
		import org.apache.mina.filter.codec.ProtocolCodecFilter;
		import org.hyperion.Server;
		import org.hyperion.engine.task.Task;
		import org.hyperion.engine.task.TaskManager;
		import org.hyperion.rs2.model.Player;
		import org.hyperion.rs2.model.World;
		import org.hyperion.rs2.model.punishment.Combination;
		import org.hyperion.rs2.model.punishment.Punishment;
		import org.hyperion.rs2.model.punishment.Target;
		import org.hyperion.rs2.model.punishment.Type;
		import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
		import org.hyperion.rs2.net.Packet;
		import org.hyperion.rs2.net.PacketManager;
		import org.hyperion.rs2.net.RS2CodecFactory;
		import org.hyperion.rs2.util.TextUtils;
		import org.hyperion.util.ObservableCollection;

		import java.io.File;
		import java.io.FileWriter;
		import java.io.IOException;
		import java.net.SocketAddress;
		import java.util.ArrayList;
		import java.util.Collection;
		import java.util.HashMap;
		import java.util.Map;
		import java.util.concurrent.TimeUnit;

/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 *
 * @author Graham Edgecombe
 */
public class ConnectionHandler extends IoHandlerAdapter {

	private final static int MAX_CONNECTIONS_TRIES = 50;

	private final static ObservableCollection<String> ipBlackList = new ObservableCollection<>(new ArrayList<>());
	private final static Map<String, Integer> ipTries = new HashMap<>();

	public static void removeIp(String ip) {
		if(ipTries.containsKey(ip))
			ipTries.remove(ip);
	}

	static {
		TaskManager.submit(new Task(15000, "IPtries cleaning") {
			@Override
			protected void execute() {
				ipTries.clear();
			}
		});
	}

	public static Collection<String> getIpBlackList() {
		return ipBlackList;
	}

	public static void addIp(String ip) {
		ipBlackList.add(ip);
		System.out.println("Blocked IP " + ip + " in the firewall.");
		try (FileWriter writer = new FileWriter("./blockit.txt", true)) {
			writer.write(File.separator);
			writer.write(ip.replace("/", ""));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			Runtime.getRuntime().exec("cmd /c start blockit.bat");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		Object playerObject = session.getAttribute("player");
		if(playerObject != null && playerObject instanceof Player) {
			Player player = (Player)playerObject;
			World.unregister(player);
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
			if(packetCount > 50){
				player.sendImportantMessage("PLEASE STOP WHAT YOU'RE DOING OR YOU WILL BE KICKED!");
				if (player.getExtraData().getInt("packetCount") > 250) {
					player.getSession().close(false);
					PunishmentManager.getInstance().add(new Punishment(player, Combination.of(Target.SPECIAL, Type.BAN), org.hyperion.rs2.model.punishment.Time.create(1, TimeUnit.MINUTES), "Suspected layer 7 ddos."));
				}
				if(packetCount > 249) {
					System.out.printf("%s has a a %,d packet count, banning\n", player.getName(), player.getExtraData().getInt("packetCount"));
					session.close(false);
				}
				return;
			}
		}
		Server.getLoader().getEngine().submit(() -> {
			if(session.getAttribute("player") != null)
				PacketManager.getPacketManager().handle(session, (Packet)message);
			else {
				((Packet)message).getPayload().clear().free();
			}
		});
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Server.getLoader().getEngine().submit(() -> {
			if(session.containsAttribute("player")) {
				Player p = (Player) session.getAttribute("player");
				if(p != null) {
					if(!p.loggedOut) {
						World.unregister(p);
					}
				} else
					System.out.println("Tried to logout player but the player was null..");
			}
        });
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		Object playerobject = session.getAttribute("player");
		if(playerobject != null) {
			Player player = (Player) playerobject;
			System.out.println("Connection closed because its idle " + player.getName());
			World.unregister(player);
		} else
			session.close(false);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();
		String remoteIp = remoteAddress.toString();
		String ip = remoteIp.split(":")[0];
		String shortIp = TextUtils.shortIp(remoteIp);

		if(!HostGateway.canEnter(shortIp)) {
			session.close(true);
			return;
		}

		if(!ipTries.containsKey(shortIp))
			ipTries.put(shortIp, 0);
		ipTries.put(shortIp, ipTries.get(shortIp) + 1);

		if(ipTries.get(shortIp) > MAX_CONNECTIONS_TRIES) {
			addIp(shortIp);
			ipTries.remove(shortIp);
			return;
		}

		session.setAttribute("remote", remoteAddress);
		session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.LOGIN));
	}
}