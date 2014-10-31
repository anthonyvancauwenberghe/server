package org.hyperion.rs2.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.event.Event;
import org.hyperion.util.Time;

public class PlayerStatsEvent extends Event {

	public static final long DELAY = Time.FIFTEEN_SECONDS;

	public PlayerStatsEvent() {
		super(DELAY);
	}

	@Override
	public void execute() {
		Server.getStats().print();
	}

}
