package org.hyperion.rs2.model.combat;

public class LastAttacker {

	private String clientName;
	private String userName;
	private long lastAttack;

	public LastAttacker(String clientName) {
		this.userName = "";
		this.clientName = clientName;
		lastAttack = System.currentTimeMillis() - 9000;
	}

	public void updateLastAttacker(String name) {
		updateLastAttacker(name, true);
	}

	public void updateLastAttacker(String name, boolean first) {
		if(name.equals(clientName))
			return;
		this.userName = name;
		lastAttack = System.currentTimeMillis();
	}

	public String getName() {
		return userName;
	}

	public long timeSinceLastAttack() {
		return (System.currentTimeMillis() - lastAttack);
	}

}
