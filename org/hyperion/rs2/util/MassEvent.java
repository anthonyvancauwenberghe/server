package org.hyperion.rs2.util;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public enum MassEvent {
	INSTANCE;

	public static final MassEvent getSingleton() {
		return INSTANCE;
	}

	public final void executeEvent(final EventBuilder e) {
		World.submit(new Event(e.getDelay()) {
			public void execute() {
				if(e.checkStop())
					this.stop();
				for(Player p : World.getPlayers())
					e.execute(p);
				if(e.getDelay() == 0)
					EventBuilder.stopEvent(e);
			}
		});
	}

	MassEvent() {
	}
}


