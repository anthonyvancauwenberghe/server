package org.hyperion.rs2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.savingnew.PlayerLoading;
import org.hyperion.rs2.savingnew.PlayerSaving;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.ObservableCollection;
import org.hyperion.util.Time;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static org.hyperion.rs2.LoginResponse.*;

/**
 * Created by Gilles on 6/02/2016.
 */
public class GenericWorldLoader implements WorldLoader {

	/**
	 * TEMP
	 */
	private final static Set<String> unlockedPlayers = new HashSet<>();

	private final static Set<String> unlockedRichPlayers = new HashSet<>();

	public static Set<String> getUnlockedPlayers() {
		return unlockedPlayers;
	}

	public static Set<String> getUnlockedRichPlayers() {
		return unlockedRichPlayers;
	}
	/**
	 * END OF TEMP
	 */

	private final static String ALLOWED_IPS_DIR = "./data/json/allowed_ips.json";
	private final static ObservableCollection<String> ALLOWED_IPS = loadList(ALLOWED_IPS_DIR);
	private final static Map<String, Integer> LOGIN_ATTEMPTS = new HashMap<>();
	private final static Set<String> BLOCKED_PLAYERS = new HashSet<>();

	private final static int MAXIMUM_LOGIN_ATTEMPTS = 5;

	static {
		ALLOWED_IPS.addListener(new AbstractNotifyListener() {
			@Override
			public void invalidated(javafx.beans.Observable observable) {
				saveList(ALLOWED_IPS, ALLOWED_IPS_DIR);
			}
		});
	}

	@Override
	public LoginResponse checkLogin(Player player, PlayerDetails playerDetails) {
		if(LOGIN_ATTEMPTS.get(player.getName()) == null)
			LOGIN_ATTEMPTS.put(player.getName(), 0);

		if(BLOCKED_PLAYERS.contains(player.getName())) {
			return LOGIN_ATTEMPTS_EXCEEDED;
		}

		if(LOGIN_ATTEMPTS.get(player.getName()) >= MAXIMUM_LOGIN_ATTEMPTS) {
			BLOCKED_PLAYERS.add(player.getName());
			World.submit(new Task(Time.ONE_MINUTE) {
				String playerName = player.getName();

				@Override
				public void execute() {
					BLOCKED_PLAYERS.remove(playerName);
					stop();
				}
			});

			return LOGIN_ATTEMPTS_EXCEEDED;
		}

		if(World.getPlayers().size() >= Constants.MAX_PLAYERS)
			return WORLD_FULL;

		if(Server.isUpdating())
			return UPDATE_IN_PROGRESS;

		if(playerDetails.getUID() != Configuration.getInt(Configuration.ConfigurationObject.CLIENT_VERSION))
			return SERVER_UPDATED;

		final Punishment punishment = PunishmentManager.getInstance().findBan(playerDetails.getName(), playerDetails.getIpAddress().split(":")[0], playerDetails.getMacAddress(), playerDetails.getSpecialUid());
		if(punishment != null) {
			playerDetails.getSession().write(
					new PacketBuilder()
							.put((byte)ACCOUNT_DISABLED.getReturnCode())
							.putRS2String(punishment.getCombination().getTarget().name())
							.putRS2String(punishment.getIssuerName())
							.putRS2String(punishment.getReason())
							.putRS2String(punishment.getTime().getRemainingTimeStamp())
							.toPacket()).addListener(future -> future.getSession().close(false));
			return ACCOUNT_DISABLED;
		}

		if(!NameUtils.isValidName(player.getName()) || player.getName().startsWith(" ") || player.getName().length() > 12 || player.getName().length() <= 0)
			return INVALID_CREDENTIALS;

		if(World.getPlayerByName(player.getName()) != null)
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

        /*if(player.getGoogleAuthenticatorKey() != null) {
			VerifyResponse verifyResponse = PlayerAuthenticatorVerification.verifyPlayer(player, playerDetails.getAuthenticationCode());
			if(verifyResponse == VerifyResponse.PIN_ENTERED_TWICE)
				return AUTHENTICATION_USED_TWICE;
			if(verifyResponse == VerifyResponse.INCORRECT_PIN) {
				LOGIN_ATTEMPTS.put(player.getName(), LOGIN_ATTEMPTS.get(player.getName()) + 1);
				return AUTHENTICATION_WRONG;
			}
		}*/

		if(Rank.hasAbility(player, Rank.ADMINISTRATOR))
			if(!ALLOWED_IPS.contains(player.getShortIP()))
				return INVALID_CREDENTIALS;

		/**
		 * TEMP
		 */

		if (!ALLOWED_IPS.contains(player.getShortIP())) {
			if(Configuration.getString(Configuration.ConfigurationObject.NAME).equalsIgnoreCase("ArteroPk") && !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
				if (player.getPermExtraData().getLong("passchange") < EntityHandler.getLastPassReset().getTime() && !getUnlockedPlayers().contains(player.getName().toLowerCase())) {
					try {
						String currentCutIp = player.getShortIP().substring(0, player.getShortIP().substring(0, player.getShortIP().lastIndexOf(".")).lastIndexOf("."));
						String previousCutIp = player.lastIp.substring(0, player.lastIp.substring(0, player.lastIp.lastIndexOf(".")).lastIndexOf("."));
						if (!currentCutIp.equals(previousCutIp)) {
							return MEMBERS_ONLY;
						}
					} catch (Exception e) {
						return MEMBERS_ONLY;
					}
				}
				if (player.isNew())
					player.getPermExtraData().put("passchange", System.currentTimeMillis());
			}
		}

		/**
		 * END OF TEMP
		 */

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

	private static ObservableCollection<String> loadList(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			saveList(fileName);
			return new ObservableCollection<>(new ArrayList<>());
		}

		try (FileReader fileReader = new FileReader(file)) {
			JsonParser parser = new JsonParser();
			JsonArray object = (JsonArray) parser.parse(fileReader);
			return new ObservableCollection<>(new Gson().fromJson(object, new TypeToken<ArrayList<String>>() {}.getType()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ObservableCollection<>(new ArrayList<>());
		}
	}

	private static void saveList(String fileName) {
		saveList(new ObservableCollection<>(new ArrayList<>()), fileName);
	}

	private static void saveList(ObservableCollection<String> list, String fileName) {
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
			writer.write(builder.toJson(list, new TypeToken<ObservableCollection<String>>() {}.getType()));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	static {
        CommandHandler.submit(new Command("addip", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception{
                String ipAddress = filterInput(input);
                if(ipAddress == null)
                    throw new Exception();
                ALLOWED_IPS.add(ipAddress.toLowerCase().replaceAll("_", " "));
                player.getActionSender().sendMessage("The IP address '" + ipAddress + "' has been added to the list.");
                return true;
            }
        });
		CommandHandler.submit(new Command("unlock", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) throws Exception{
				String playerName = filterInput(input);
				if(playerName == null)
					throw new Exception();
				getUnlockedPlayers().add(playerName.toLowerCase().replaceAll("_", " "));
				player.getActionSender().sendMessage(Misc.formatPlayerName(playerName) + " has been unlocked and can now login.");
				return true;
			}
		});
		CommandHandler.submit(new Command("unlockrich", Rank.HEAD_MODERATOR) {
			@Override
			public boolean execute(Player player, String input) throws Exception{
				String playerName = filterInput(input);
				if(playerName == null)
					throw new Exception();
				getUnlockedRichPlayers().add(playerName.toLowerCase().replaceAll("_", " "));
				player.getActionSender().sendMessage(Misc.formatPlayerName(playerName) + " has been unlocked and can now login.");
				return true;
			}
		});
		CommandHandler.submit(new Command("changeip", Rank.ADMINISTRATOR) {
			@Override
			public boolean execute(Player player, String input) throws Exception{
				String[] parts = filterInput(input).split(",");
				if(parts.length < 2)
					throw new Exception();
				if(org.hyperion.rs2.savingnew.PlayerSaving.replaceProperty(parts[0], "IP", parts[1] + ":55222"))
					player.getActionSender().sendMessage(Misc.formatPlayerName(parts[0]) + "'s IP has been changed to " + parts[1]);
				else
					player.getActionSender().sendMessage("IP could not be changed.");
				return true;
			}
		});
	}
}