package org.hyperion.rs2;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;

public interface WorldLoader {
	LoginResponse checkLogin(Player player, PlayerDetails playerDetails);
	boolean loadPlayer(Player player);
	boolean savePlayer(Player player);

}
