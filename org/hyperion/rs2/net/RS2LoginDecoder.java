package org.hyperion.rs2.net;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.hyperion.rs2.ConnectionHandler;
import org.hyperion.rs2.model.BanManager;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.ondemand.OnDemandPool;
import org.hyperion.rs2.net.ondemand.OnDemandRequest;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

/**
 * Login protocol decoding class.
 *
 * @author Graham Edgecombe
 */
public class RS2LoginDecoder extends CumulativeProtocolDecoder {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(RS2LoginDecoder.class.getName());

	/**
	 * Opcode stage.
	 */
	public static final int STATE_OPCODE = 0;

	/**
	 * Login stage.
	 */
	public static final int STATE_LOGIN = 1;

	/**
	 * Precrypted stage.
	 */
	public static final int STATE_PRECRYPTED = 2;

	/**
	 * Crypted stage.
	 */
	public static final int STATE_CRYPTED = 3;

	/**
	 * Update stage.
	 */
	public static final int STATE_UPDATE = - 1;

	/**
	 * Game opcode.
	 */
	public static final int OPCODE_GAME = 14;

	/**
	 * Update opcode.
	 */
	public static final int OPCODE_UPDATE = 15;

	/**
	 * Players Online opcode
	 */
	public static final int OPCODE_PLAYERCOUNT = 16;

	/**
	 * Secure random number generator.
	 */
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Initial login response.
	 */
	private static final byte[] INITIAL_RESPONSE = new byte[]{
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0
	};

	/**
	 * The ban manager.
	 */
	private static BanManager banmanager = World.getWorld().getBanManager();

	private static int invalidLogins = 0;

	private static HashMap<String, Long> loginAttempts = new HashMap<String, Long>();
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		synchronized(session) {
			int state = (Integer) session.getAttribute("state", STATE_OPCODE);
			LoginDebugger.getDebugger().log("Remote: " + session.getRemoteAddress() + ", state: " + state);
			switch(state) {
				case STATE_UPDATE:
					if(in.remaining() >= 4) {
	                /*
					 * Here we read the cache id (idx file), file id and priority.
					 */
						int cacheId = in.get() & 0xFF;
						int fileId = ((in.get() & 0xFF) << 8) | (in.get() & 0xFF);
						int priority = in.get() & 0xFF;

					/*
					 * We push the request into the ondemand pool so it can be served.
					 */
						OnDemandPool.getOnDemandPool().pushRequest(new OnDemandRequest(session, cacheId, fileId, priority));
						return true;
					} else {
						in.rewind();
						return false;
					}
				case STATE_OPCODE:
					if(in.remaining() >= 1) {
					/*
					 * Here we read the first opcode which indicates the type
					 * of connection.
					 * 
					 * 14 = game
					 * 15 = update
					 * 
					 * Updating is disabled in the vast majority of 317
					 * clients.
					 */
						int opcode = in.get() & 0xFF;
						switch(opcode) {
							case OPCODE_GAME:
								session.setAttribute("state", STATE_LOGIN);
								return true;
							case OPCODE_UPDATE:
								session.setAttribute("state", STATE_UPDATE);
								session.write(new PacketBuilder().put(INITIAL_RESPONSE).toPacket());
								return true;
							case OPCODE_PLAYERCOUNT:
								session.write(new PacketBuilder().putShort(World.getWorld().getPlayers().size()).toPacket());
								return true;
							default:
								invalidLogins++;
								if(invalidLogins < 10 || invalidLogins % 20 == 0)
									System.out.println("Invalid opcode xxx : " + opcode + "," + session.getRemoteAddress());
								String ip = session.getRemoteAddress().toString().split(":")[0];
								ConnectionHandler.blackList.put(ip, new Object());
								BufferedWriter bw = new BufferedWriter(new FileWriter("./data/attack.txt", true));
								bw.write(ip);
								bw.newLine();
								bw.close();
								session.close(true);

								return false;
						}
					} else {
						in.rewind();
						return false;
					}
				case STATE_LOGIN:

                    //System.out.println("herrrr");

					if(in.remaining() >= 1) {
					/*
					 * The name hash is a simple hash of the name which is
					 * suspected to be used to select the appropriate login
					 * server.
					 */
						@SuppressWarnings("unused")
						int nameHash = in.get() & 0xFF;//l >> 16 & 31L- u can decrypt to get the real player name
						if(LoginDebugger.getDebugger().isEnabled()) {
							long possibleNameLong = nameHash >> 16 & 31L;
							String possibleName = NameUtils.longToName(possibleNameLong);
							String possibleName2 = NameUtils.longToName(nameHash);
							LoginDebugger.getDebugger().log("1. Possible name: " + possibleName + "," + possibleName2);
						}
					/*
					 * We generated the server session key using a SecureRandom
					 * class for security.
					 */
						long serverKey = RANDOM.nextLong();
					
					/*
					 * The initial response is just 0s which the client is set
					 * to ignore (probably some sort of modification).
					 */
						session.write(new PacketBuilder().put(INITIAL_RESPONSE).put((byte) 0).putLong(serverKey).toPacket());
						session.setAttribute("state", STATE_PRECRYPTED);
						session.setAttribute("serverKey", serverKey);
						return true;
					}
					break;
				case STATE_PRECRYPTED:
					if(in.remaining() >= 2) {
					/*
					 * We read the type of login.
					 * 
					 * 16 = normal
					 * 18 = reconnection
					 */
						int loginOpcode = in.get() & 0xFF;
						LoginDebugger.getDebugger().log("2. Logincode: " + loginOpcode);
						if(loginOpcode != 16 && loginOpcode != 18) {
							//logger.info("Invalid login opcode : " + loginOpcode);
							System.out.println("Invalid login opcode: " + loginOpcode);
							session.close(false);
							in.rewind();
							return false;
						}
					
					/*
					 * We read the size of the login packet.
					 */
						int loginSize = in.get() & 0xFF;
					/*
					 * And calculated how long the encrypted block will be.
					 */
						int loginEncryptSize = loginSize - (36 + 1 + 1 + 2);
						//System.out.println("loginEncryptSize "+loginEncryptSize+" : "+loginSize);
					/*
					 * This could be invalid so if it is we ignore it.
					 */
						if(loginEncryptSize <= 0) {
							//logger.info("Encrypted packet size zero or negative : " + loginEncryptSize);
							session.close(false);
							in.rewind();
							return false;
						}
						session.setAttribute("state", STATE_CRYPTED);
						session.setAttribute("size", loginSize);
						session.setAttribute("encryptSize", loginEncryptSize);
						return true;
					}
					break;
				case STATE_CRYPTED:
					int size = (Integer) session.getAttribute("size");
					int encryptSize = (Integer) session.getAttribute("encryptSize");
					if(in.remaining() >= size) {
                        String remoteIp = session.getRemoteAddress().toString();
                        String shortIp = TextUtils.shortIp(remoteIp);
                       if (loginAttempts.containsKey(shortIp)) {
                            long lastLogin = loginAttempts.get(shortIp);
                            if(lastLogin != -1) {
                                if(System.currentTimeMillis() - lastLogin < 5000) {
                                	PacketBuilder builder = new PacketBuilder();
                                	builder.put(
                                			(byte)11);
                                    session.write(builder.toPacket()).addListener(new IoFutureListener<IoFuture>() {
        								@Override
        								public void operationComplete(IoFuture future) {
        									future.getSession().close(false);
        								}
        							});;
                                    in.rewind();
                                    return false;
                                }
                            }
                        }
						//System.out.println("Logging in plz");
					/*
					 * We read the magic ID which is 255 (0xFF) which indicates
					 * this is the real login packet.
					 */
						int returnCode = 0;
						int magicId = in.get() & 0xFF;
						if(magicId != 122) {
							returnCode = 6;
						}
					
					/*
					 * We now read a short which is the client version and
					 * check if it equals 317.
					 */
						int version = in.getShort() & 0xFFFF;
						if(version != 490) {
							returnCode = 6;
						}
					
					/*
					 * The following byte indicates if we are using a low
					 * memory version.
					 */
						@SuppressWarnings("unused")
						boolean lowMemoryVersion = false;
						if(in.get() != 5) {
							returnCode = 6;
						}
					/*
					 * We know read the cache indices.
					 */
						for(int i = 0; i < 9; i++) {
							in.getInt();
						}
					
					/*
					 * The encrypted size includes the size byte which we don't
					 * need.
					 */
						encryptSize--;
					
					/*
					 * We check if there is a mismatch in the sizing.
					 */
						int reportedSize = in.get() & 0xFF;
						if(reportedSize != encryptSize) {
							//logger.info("Packet size mismatch (expected : " + encryptSize + ", reported : " + reportedSize + ")");
							session.close(false);
							in.rewind();
							return false;
						}
						//System.out.println("got here");
					/*
					 * We now read the encrypted block opcode (although in most
					 * 317 clients and this server the RSA is disabled) and
					 * check it is equal to 10.
					 */
						int blockOpcode = in.get() & 0xFF;
						if(blockOpcode != 10) {
							//logger.info("Invalid login block opcode : " + blockOpcode);
							returnCode = 6;
						}
	
					/*
					 * We read the client's session key.
					 */
						long clientKey = in.getLong();
					
					/*
					 * And verify it has the correct server session key.
					 */
						long serverKey = (Long) session.getAttribute("serverKey");
						long reportedServerKey = in.getLong();
						if(reportedServerKey != serverKey) {
							//logger.info("Server key mismatch (expected : " + serverKey + ", reported : " + reportedServerKey + ") client key: "+clientKey);
							returnCode = 6;
						}
					
					/*
					 * The UID, found in random.dat in newer clients and
					 * uid.dat in older clients is a way of identifying a
					 * computer.
					 * 
					 * However, some clients send a hardcoded or random UID,
					 * making it useless in the private server scene.
					 */
						int uid = in.getInt();
						 if(uid < 15466) {
							returnCode = 6;
						}

						int macId = in.getInt();
						if(macId == 0) {
							macId = Misc.random(Integer.MAX_VALUE);
						}

                        final int[] specialUid = new int[20];
                        for(int i = 0; i < specialUid.length; i++)
                            specialUid[i] = in.getInt();

					/*
					 * We read and format the name and passwords.
					 */
						String name = NameUtils.formatName(IoBufferUtils.getRS2String(in)).trim();
						if(name.length() == 0 || name.length() > 12) {
							//System.out.println("name error");
							session.close(false);
							in.rewind();

							return false;
						}

						//System.out.println("herrrr");
						//if(banmanager.getStatus(name) == BanManager.BAN || banmanager.getStatus(shortIp) == BanManager.BAN) {
						//	returnCode = 4;
						//}
                        final Punishment punishment = PunishmentManager.getInstance().findBan(name, shortIp, macId, specialUid);
                        if(punishment != null) {
                            returnCode = 4;
                            LoginDebugger.getDebugger().log("Packetbuilder code");
                            PacketBuilder bldr = new PacketBuilder(1 , Packet.Type.VARIABLE);
                            bldr.put((byte) returnCode);
                            bldr.putRS2String(punishment.getCombination().getTarget().name());
                            bldr.putRS2String(punishment.getIssuerName());
                            bldr.putRS2String(punishment.getReason());
                            bldr.putRS2String(punishment.getTime().getExpirationDateStamp());
                            session.write(bldr.toPacket())
                                    .addListener(new IoFutureListener<IoFuture>() {
                                        @Override
                                        public void operationComplete(IoFuture future) {
                                            future.getSession().close(false);
                                        }
                                    });
                            in.rewind();
                            return false;
                        }

						String pass = IoBufferUtils.getRS2String(in);
						LoginDebugger.getDebugger().log("3. Login packet: " + name + "," + pass + "," + remoteIp);
						//System.out.println("username: "+name+" pass: "+pass+" macId "+macId);
						//logger.info("Login request : username=" + name + " password=" + pass);
					
					/*
					 * And setup the ISAAC cipher which is used to encrypt and
					 * decrypt opcodes.
					 * 
					 * However, without RSA, this is rendered useless anyway.
					 */
						int[] sessionKey = new int[4];
						sessionKey[0] = (int) (clientKey >> 32);
						sessionKey[1] = (int) clientKey;
						sessionKey[2] = (int) (serverKey >> 32);
						sessionKey[3] = (int) serverKey;

						session.removeAttribute("state");
						session.removeAttribute("serverKey");
						session.removeAttribute("size");
						session.removeAttribute("encryptSize");

						ISAACCipher inCipher = new ISAACCipher(sessionKey);
						for(int i = 0; i < 4; i++) {
							sessionKey[i] += 50;
						}
						ISAACCipher outCipher = new ISAACCipher(sessionKey);
					
					/*
					 * Now, the login has completed, and we do the appropriate
					 * things to fire off the chain of events which will load
					 * and check the saved games etc.
					 */
						session.getFilterChain().remove("protocol");
						session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.GAME));
						if(! NameUtils.isValidName(name)) {
							returnCode = 3;
							session.close(false);
							in.rewind();
							return false;
						}

                        if(World.getWorld().getPlayer(name) != null) {
                            returnCode = 5;
                        }

						PlayerDetails pd = new PlayerDetails(session, name, pass, macId, inCipher, outCipher, remoteIp, "Id1");
                        pd.specialUid = specialUid;
						//System.out.println("loading guy");
						World.getWorld().load(pd, returnCode);
                        loginAttempts.put(shortIp, System.currentTimeMillis());
					}
					break;
			}
			in.rewind();
			return false;
		}
	}

}
