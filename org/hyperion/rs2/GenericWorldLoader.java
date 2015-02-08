package org.hyperion.rs2;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.hyperion.rs2.model.BanManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.rs2.util.TextUtils;

/**
 * An implementation of the <code>WorldLoader</code> class that saves players
 * in binary, gzip-compressed files in the <code>data/players/</code>
 * directory.
 *
 * @author Graham Edgecombe
 */
public class GenericWorldLoader implements WorldLoader {

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		Player player = null;
		int code = 2;
		boolean newcharacter = true;
		if(World.getWorld().updateInProgress()) {
			code = LoginResult.UPDATE_IN_PROGRESS;
			LoginDebugger.getDebugger().log("Update progress in  Genericworldloader");
		} else if(PunishmentManager.getInstance().isBanned(pd.getName(), pd.IP.split(":")[0], pd.getUID(), pd.specialUid)) {
			code = 4;
			LoginDebugger.getDebugger().log("Code 4 ban in Genericworldloader");
		} else if(PlayerFiles.exists(pd.getName())) {
			newcharacter = false;
			try {
				String pass = PlayerFiles.getPassword(pd.getName());
				if(! pass.equalsIgnoreCase(pd.getPassword()))
					code = 3;
			} catch(Exception ex) {
				code = 11;
				ex.printStackTrace();
			}
		}
		LoginDebugger.getDebugger().log("Checking more in genericworldloader");
		if(code == 2) {
			//System.out.println("Creating new player");
			LoginDebugger.getDebugger().log("About to make new player in GWL");
			player = new Player(pd, newcharacter);
			LoginDebugger.getDebugger().log("Made new player in GWL");
		}
		LoginDebugger.getDebugger().log("Pre checking enabled disabled login debugger");
		if(LoginDebugger.getDebugger().isEnabled()) {
			String name = "unset";
			if(player != null)
				name = player.getName();
			LoginDebugger.getDebugger().log("5. Login Result: " + code + ": " + name);
		}
		return new LoginResult(code, player);
	}

	@Override
	public boolean savePlayer(Player player, String info) {
		if(! NameUtils.isValidName(player.getName())) {
			System.out.println("Trying to save for wrong player: " + player.getName() + "," + info);
		}
		final File file = new File("./data/logSaves.txt");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextUtils.writeToFile(file, String.format("[%s]%s has logged out info: %s", 
				new Date(System.currentTimeMillis()).toString(), player.getName(), info));
		PlayerSaving.getSaving().save(player);
		return true;
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			if(PlayerFiles.exists(player.getName())) {
				PlayerSaving.getSaving().load(player);
			}
			LoginDebugger.getDebugger().log("6. Generic worldloader loaded Player file " + player.getName());
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
