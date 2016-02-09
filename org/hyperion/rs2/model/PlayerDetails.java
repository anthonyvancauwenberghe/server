package org.hyperion.rs2.model;

import org.apache.mina.core.session.IoSession;
import org.hyperion.rs2.net.ISAACCipher;
import org.hyperion.rs2.net.security.EncryptionStandard;

public final class PlayerDetails {

	private IoSession session;
	private final String name;
	private final String password;
	private final int macAddress;
	private final ISAACCipher inCipher;
	private final ISAACCipher outCipher;
	private final String IpAddress;
	private final int[] specialUid;
	private final int UID;

	public PlayerDetails(IoSession session, String name, String password, int macAddress, int UID, ISAACCipher inCipher, ISAACCipher outCipher, String IpAddress, int[] specialUid) {
		this.session = session;
		this.name = name;
		this.password = EncryptionStandard.encryptPassword(password);
		this.macAddress = macAddress;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
		this.IpAddress = IpAddress;
		this.specialUid = specialUid;
		this.UID = UID;
	}

	public IoSession getSession() {
		return session;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public int getMacAddress() {
		return macAddress;
	}
	public ISAACCipher getInCipher() {
		return inCipher;
	}
	public ISAACCipher getOutCipher() {
		return outCipher;
	}
	public String getIpAddress() {
		return IpAddress;
	}
	public int[] getSpecialUid() {
		return specialUid;
	}
	public int getUID() {
		return UID;
	}
}