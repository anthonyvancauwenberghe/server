package org.hyperion.rs2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.savingnew.PlayerLoading;
import org.hyperion.rs2.savingnew.PlayerSaving;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.ObservableArrayList;
import org.hyperion.util.Time;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.hyperion.rs2.LoginResponse.*;
import static org.hyperion.rs2.model.Rank.ADMINISTRATOR;

/**
 * Created by Gilles on 6/02/2016.
 */
public class GenericWorldLoader implements WorldLoader {

	private final static String ALLOWED_IPS_DIR = "./data/json/allowed_ips.json";
	private final static List<String> ALLOWED_IPS = loadList(ALLOWED_IPS_DIR).listen((list) -> saveList(list, ALLOWED_IPS_DIR));
	private final static Map<String, Integer> LOGIN_ATTEMPTS = new HashMap<>();
	private final static Set<String> BLOCKED_PLAYERS = new HashSet<>();

	private final static int MAXIMUM_LOGIN_ATTEMPTS = 5;

	@Override
	public LoginResponse checkLogin(Player player, PlayerDetails playerDetails) {
		if(LOGIN_ATTEMPTS.get(player.getName()) == null)
			LOGIN_ATTEMPTS.put(player.getName(), 0);

		if(BLOCKED_PLAYERS.contains(player.getName())) {
			return LOGIN_ATTEMPTS_EXCEEDED;
		}

		if(LOGIN_ATTEMPTS.get(player.getName()) >= MAXIMUM_LOGIN_ATTEMPTS) {
			System.out.println(LOGIN_ATTEMPTS.remove(player.getName()));
			BLOCKED_PLAYERS.add(player.getName());
			World.getWorld().submit(new Event(Time.ONE_MINUTE, "LoginServer") {
				String playerName = player.getName();

				@Override
				public void execute() throws IOException {
					BLOCKED_PLAYERS.remove(playerName);
					stop();
				}
			});

			return LOGIN_ATTEMPTS_EXCEEDED;
		}

		if(World.getWorld().getPlayers().size() >= Constants.MAX_PLAYERS)
			return WORLD_FULL;

		if(World.getWorld().updateInProgress())
			return UPDATE_IN_PROGRESS;

		if(playerDetails.getUID() < 15483)
			return SERVER_UPDATED;

		if(ConnectionHandler.getIpBlackList().containsKey(player.getFullIP()))
			return ACCOUNT_DISABLED;

		final Punishment punishment = PunishmentManager.getInstance().findBan(playerDetails.getName(), playerDetails.getIpAddress().split(":")[0], playerDetails.getMacAddress(), playerDetails.getSpecialUid());
		if(punishment != null) {
			playerDetails.getSession().write(
					new PacketBuilder()
							.putRS2String(punishment.getCombination().getTarget().name())
							.putRS2String(punishment.getIssuerName())
							.putRS2String(punishment.getReason())
							.putRS2String(punishment.getTime().getRemainingTimeStamp())
							.toPacket()).addListener(future -> future.getSession().close(false));
			return ACCOUNT_DISABLED;
		}

		if(!NameUtils.isValidName(player.getName()) || player.getName().startsWith(" ") || player.getName().split(" ").length - 1 > 1 || player.getName().length() > 12 || player.getName().length() <= 0)
			return INVALID_CREDENTIALS;

		if(World.getWorld().getPlayer(player.getName()) != null)
			return ALREADY_LOGGED_IN;

		/**
		 * If we get this far, we're loading the player his actual details to check.
		 */
		if(!loadPlayer(player))
			return NEW_PLAYER;

		if(!player.getPassword().equals(playerDetails.getPassword())) {
			LOGIN_ATTEMPTS.put(player.getName(), LOGIN_ATTEMPTS.get(player.getName()) + 1);
			return INVALID_CREDENTIALS;
		}

		if(Rank.hasAbility(player, ADMINISTRATOR))
			if(!ALLOWED_IPS.contains(player.getShortIP()))
				return INVALID_CREDENTIALS;

		LOGIN_ATTEMPTS.remove(player.getName());
		return SUCCESSFUL_LOGIN;
	}

	@Override
	public boolean loadPlayer(Player player) {
		return PlayerLoading.loadPlayer(player);
	}

	@Override
	public boolean savePlayer(Player player) {
		PlayerSaving.save(player);
		return true;
	}

	private static ObservableArrayList<String> loadList(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			saveList(fileName);
			return new ObservableArrayList<>();
		}

		try (FileReader fileReader = new FileReader(file)) {
			JsonParser parser = new JsonParser();
			JsonArray object = (JsonArray) parser.parse(fileReader);
			return new Gson().fromJson(object, new TypeToken<ObservableArrayList<String>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
			return new ObservableArrayList<>();
		}
	}

	private static void saveList(String fileName) {
		saveList(new ArrayList<>(), fileName);
	}

	private static void saveList(List<String> list, String fileName) {
		File fileToWrite = new File(fileName);

		if (!fileToWrite.getParentFile().exists()) {
			try {
				if(!fileToWrite.getParentFile().mkdirs())
					return;
			} catch (SecurityException e) {
				System.out.println("Unable to create directory for list file!");
			}
		}

		try (FileWriter writer = new FileWriter(fileToWrite)) {
			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			writer.write(builder.toJson(list, new TypeToken<ObservableArrayList<String>>() {}.getType()));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}