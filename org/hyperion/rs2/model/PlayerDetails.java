package org.hyperion.rs2.model;

import org.apache.mina.core.session.IoSession;
import org.hyperion.rs2.net.ISAACCipher;
import org.hyperion.rs2.util.NameUtils;

/**
 * Contains details about a player (but not the actual <code>Player</code>
 * object itself) that has not logged in yet.
 *
 * @author Graham Edgecombe
 */
public class PlayerDetails {

	/**
	 * The session.
	 */
	private IoSession session;

	/**
	 * The player name.
	 */
	private String name;

	/**
	 * The player password.
	 */
	private String pass;

	/**
	 * The player's UID.
	 */
	private int uid;

	/**
	 * The incoming ISAAC cipher.
	 */
	private ISAACCipher inCipher;

	/**
	 * The outgoing ISAAC cipher.
	 */
	private ISAACCipher outCipher;

	public String IP;

    public int[] specialUid;

	/**
	 * Creates the player details class.
	 *
	 * @param session   The session.
	 * @param name      The name.
	 * @param pass      The password.
	 * @param uid       The unique id.
	 * @param inCipher  The incoming cipher.
	 * @param outCipher The outgoing cipher.
	 */
	public PlayerDetails(IoSession session, String name, String pass, int uid, ISAACCipher inCipher, ISAACCipher outCipher, String IP, String message) {
		this.session = session;
		this.name = name;
		this.pass = pass;
		this.uid = uid;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
		this.IP = IP;
		if(! NameUtils.isValidName(name)) {
			System.out.println("Initialiting invalid name: " + name + ", " + message);
		}
	}

	/**
	 * Gets the <code>IoSession</code>.
	 *
	 * @return The <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}

	/**
	 * Gets the name.
	 *
	 * @return The name.
	 */
	public String getName() {
		if(! NameUtils.isValidName(name)) {
			System.out.println("Invalid name in pd!" + name);
		}
		return name;
	}

	/**
	 * Gets the password.
	 *
	 * @return The password.
	 */
	public String getPassword() {
		return pass;
	}

	/**
	 * Gets the unique id.
	 *
	 * @return The unique id.
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 *
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 *
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	public void setPass(String password) {
		this.pass = password;
	}

}
