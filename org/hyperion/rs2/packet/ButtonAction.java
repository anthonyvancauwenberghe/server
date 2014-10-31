package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;

public interface ButtonAction {

	public abstract void handle(final Player player, int id);

}
