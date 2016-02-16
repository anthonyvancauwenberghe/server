package org.hyperion.rs2.saving;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.saving.impl.*;
import org.hyperion.rs2.util.TextUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class holds all the PlayerSaving logic and settings.
 *
 * @author Arsen Maxyutov
 */
public class PlayerSaving {


	/**
	 * The buffer size used for saving and loading.
	 */
	public static final int BUFFER_SIZE = 1024;

	/**
	 * The table holding all player properties.
	 */
	public static final String PLAYERS_TABLE = "player_data";

	/**
	 * The table holding all inventory items.
	 */
	public static final String INVENTORY_TABLE = "player_inventory";

	/**
	 * The table holding all bank items.
	 */
	public static final String BANK_TABLE = "player_bank";

	/**
	 * The table holding all equipment.
	 */
	public static final String EQUIPMENT_TABLE = "player_equipment";

	/**
	 * The table holding all friendlists.
	 */
	public static final String FRIENDS_TABLE = "player_friends";

	/**
	 * The table holding all appearances.
	 */
	public static final String LOOK_TABLE = "player_look";

	/**
	 * The table holding all skills data.
	 */
	public static final String SKILLS_TABLE = "player_skills";

	/**
	 * The single thread work service used for queued saving.
	 */
	private final ExecutorService saveThread = Executors.newSingleThreadExecutor();

	/**
	 * Holds all the SaveObjects meant for loading.
	 */
	protected final Map<String, SaveObject> saveData;

	/**
	 * The collection of SaveObjects meant for iteration.
	 */
	protected final List<SaveObject> saveList;

	/**
	 * The PlayerSaving singleton.
	 */
	private final static PlayerSaving singleton = new PlayerSaving();

	/**
	 * Constructs a new PlayerSaving.
	 */
	public PlayerSaving() {
		saveList = new ArrayList<SaveObject>();
		initSaveObjects();
		((ArrayList<SaveObject>) saveList).trimToSize();
		saveData = new HashMap<String, SaveObject>();
		for(SaveObject so : saveList) {
			saveData.put(so.getName(), so);
		}
	}

	/**
	 * Initializes all SaveObjects in a specific order.
	 */
	private void initSaveObjects() {
		saveList.add(new SaveName("Name"));
		saveList.add(new SavePass("Pass"));
		saveList.add(new SaveAccValue("AccValue"));
		saveList.add(new SaveIP("IP"));
        saveList.add(new SaveBankPin("BankPin"));
        saveList.add(new SaveTabAmount());
        saveList.add(new SaveTurkeyKills("TurkeyKills"));
		saveList.add(new SaveRank("Rank"));
		saveList.add(new SaveCreatedString("CreatedStr"));
		saveList.add(new SaveLocation("Location"));
		saveList.add(new SaveElo("Elo"));
		saveList.add(new SaveDiced("Diced"));
		saveList.add(new SavePreviousSessionTime("PreviousTime"));
		saveList.add(new SaveCreatedLong("CreatedLong"));
		saveList.add(new SaveLastHonorPointsReward("LastHonors"));
		saveList.add(new SaveSpec("Spec"));
		saveList.add(new SaveAtkType("AtkType"));
		saveList.add(new SaveMagicbook("MagicBook"));
		saveList.add(new SavePrayerBook("Altar"));
		saveList.add(new SaveClan("Clan"));
        saveList.add(new SaveYellTag("YellTag"));
		saveList.add(new SaveDonatorPointsBought("DonatorsBought"));
		saveList.add(new SaveDonatorPoints("DonatorPoints"));
		saveList.add(new SavePkPoints("PkPoints"));
		saveList.add(new SaveVotePoints("VotePoints"));
		saveList.add(new SaveHonorPoints("HonorPoints"));
		saveList.add(new SaveSkullTimer("Skull"));
		saveList.add(new SaveEP("EP"));
		saveList.add(new SaveArmaKills("Arma-KC"));
		saveList.add(new SaveBandosKills("Band-KC"));
		saveList.add(new SaveZamorakKills("Zammy-KC"));
		saveList.add(new SaveSaraKills("Sara-KC"));
		saveList.add(new SaveSlayerTask("slayerTask"));
		saveList.add(new SaveSlayerAmount("taskAmount"));
        saveList.add(new SaveKillStreak("KillStreak"));
        saveList.add(new SaveKillcount("KillCount"));
		saveList.add(new SaveDeathcount("DeathCount"));
		saveList.add(new SaveCleaned("Cleaned"));
		saveList.add(new SaveFightCavesWave("FightCavesWave"));
		saveList.add(new SaveLastVoted("LastVoted"));
		saveList.add(new SaveMaxCape("maxcape"));
		saveList.add(new SaveCompCape("compcape"));
		saveList.add(new SavePvPTask("pvptask"));
		saveList.add(new SavePvPTaskAmount("pvptaskamount"));
		saveList.add(new SaveBlackMarks("blackmarks"));
		saveList.add(new SaveEloPeak("elopeak"));
		saveList.add(new SaveMail("mail"));
		saveList.add(new SavePvPArmour("pvparmourdata"));
		saveList.add(new SaveBHPoints("bhpoints"));
		saveList.add(new SaveBHPerks("bhperks"));
		saveList.add(new SaveNPCKills("npclogs"));
		saveList.add(new SaveAccountValue("accountValue"));
		// Containers, skills etc
		saveList.add(new SaveSkills("Skills"));
		saveList.add(new SaveInventory("Inventory"));
		saveList.add(new SaveBank("Bank"));
		saveList.add(new SaveEquipment("Equip"));
		saveList.add(new SaveLook("Look"));
		saveList.add(new SaveFriends("Friends"));

        saveList.add(new SaveMaxCapePrimaryColor());
        saveList.add(new SaveMaxCapeSecondaryColor());
        saveList.add(new SaveCompCapePrimaryColor());
        saveList.add(new SaveCompCapeSecondaryColor());

        saveList.add(new SaveMac());
        saveList.add(new SaveSlayer("slayerdata"));
        saveList.add(new SaveEmblemPoints("emblemPoints"));

        saveList.add(new SaveCustomSet());
        saveList.add(new SavePermExtraData());
        saveList.add(new SaveGameMode());
        saveList.add(new SaveBonusXP());
        saveList.add(new SavePin());
        saveList.add(new SavePid());
        saveList.add(new SaveDungeoneering());
        saveList.add(new SaveRunePouch("Rune Pouch"));
        saveList.add(new SaveTutorialProgress("tutorial"));
        //saveList.add(new SaveAchievements("Achievements"));
		saveList.add(new SaveVerifyCode());
    }

	/**
	 * @param player
	 * @param message
	 */
	public void save(Player player, String message) {
		if(Rank.hasAbility(player, Rank.OWNER)) {
			player.getActionSender().sendMessage(message);
		}
		save(player);
	}

	/**
	 * Saves the player's data to his character file.
	 *
	 * @param player
	 * @return true if successful, false if not
	 */
	public boolean save(Player player) {
		//saveSQL(player);
		//return true;
		/*
		if(Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
			player.sendServerMessage("Saving your account");
		}
		*/
		if(player.needsNameChange() || player.doubleChar()) {
			return false;
		}
		try (BufferedWriter file = new BufferedWriter(new FileWriter(
                getFileName(player)), BUFFER_SIZE)){

			for(SaveObject so : saveList) {
				boolean saved = so instanceof SaveBank ? ((SaveBank) so).saveBank(player, file) : so.save(player, file);
				if(saved) {
					file.newLine();
				}
			}
            player.serialize();
            return true;
		} catch(IOException e) {
			System.out.println("Player's name: " + player.getName());
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Loads the player's data from his character file.
	 *
	 * @param player
	 */
	public void load(Player player, String dir) {
		//loadSQL(player);

		try {
			BufferedReader in = new BufferedReader(new FileReader(dir + player.getName().toLowerCase() + ".txt"), BUFFER_SIZE);
			String line;
			while((line = in.readLine()) != null) {
				if(line.length() <= 1)
					continue;
				String[] parts = line.split("=");
				String name = parts[0].trim();
				String values = null;
				if(parts.length > 1)
					values = parts[1].trim();
				SaveObject so = saveData.get(name);
				if(so == null) {
					if(name.contains("Rights")) {
						int value = Integer.parseInt(values);
						boolean primary = Rank.getPrimaryRank(player) == Rank.PLAYER;
						Rank rank = null;
						if(value == 1)
							rank = Rank.MODERATOR;
						if(value == 2)
							rank = Rank.DEVELOPER;
						if(value == 3)
							rank = Rank.SUPER_DONATOR;
						if(value == 4)
							rank = Rank.ADMINISTRATOR;
						if(rank != null) {
							if(primary)
								player.setPlayerRank(Rank.setPrimaryRank(player, rank));
							else
								player.setPlayerRank(Rank.addAbility(player, rank));
						}
						continue;
					}
					if(name.contains("Status")) {
						int value = Integer.parseInt(values);
						boolean primary = Rank.getPrimaryRank(player) == Rank.PLAYER;
						Rank rank = null;
						if(value == 1)
							rank = Rank.DONATOR;
						if(value == 2)
							rank = Rank.SUPER_DONATOR;
						if(value == 3)
							rank = Rank.HELPER;
						if(value == 4)
							rank = Rank.HERO;
						if(value == 5)
							rank = Rank.LEGEND;
						if(rank != null) {
							if(primary)
								player.setPlayerRank(Rank.setPrimaryRank(player, rank));
							else
								player.setPlayerRank(Rank.addAbility(player, rank));
						}
						continue;
					}
					continue;
				}
                try {
                    if(so instanceof SaveBank) {
                        ((SaveBank) so).loadBank(player, values, in);
                    } else {
                        so.load(player, values, in);
                    }
                }catch(Exception ex) {
					ex.printStackTrace();
                    copyFile(player.getName());
                    return;
                }
			}
			//World.getSQLSaving().load(player);
			in.close();
			player.getHighscores();
		} catch(IOException e) {
			e.printStackTrace();
		}
		player.init();
	}

	public void saveLog(final String file, LinkedList<String> lines) {
		saveLog(new File(file), lines);
	}

	/**
	 * Saves a line to the specified log file.
	 *
	 * @param file
	 */
	public void saveLog(final File file, List<String> lines) {
		String[] stringArray = new String[lines.size()];
		int idx = 0;
		for(String line : lines) {
			stringArray[idx++] = line;
		}
		saveLog(file, stringArray);
	}

	/**
	 * Saves a line to the specified log file.
	 *
	 * @param file
	 */
	public void saveLog(final String file, final String... lines) {
		saveLog(new File(file), lines);
	}

	public void saveLog(final File file, final String... lines) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				TextUtils.writeToFile(file, lines);
			}
		};
		saveThread.submit(runnable);
	}

	/**
	 * Executes the runnable on the save thread.
	 *
	 * @param runnable
	 */
	public void submit(Runnable runnable) {
		saveThread.submit(runnable);
	}

	public void copyFile(String name) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"./data/characters/mergedchars/" + name + ".txt"));
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"./data/bugchars/" + name + ".txt", true));
			String line;
			while((line = in.readLine()) != null) {
				out.write(line);
				out.newLine();
			}
			in.close();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the filename of character file for the player.
	 *
	 * @param name
	 * @returns The players save file.
	 */
	public static String getFileName(String name) {
		return MergedSaving.MERGED_DIR + "/" + name.toLowerCase() + ".txt";
	}

	/**
	 * Gets the filename of character file for the player.
	 *
	 * @param player
	 * @returns The players save file.
	 */
	public static String getFileName(Player player) {
		return getFileName(player.getName());
	}

	/**
	 * Gets the PlayerSaving singleton.
	 *
	 * @return
	 */
	public static PlayerSaving getSaving() {
		return singleton;
	}

	static {
		CommandHandler.submit(new Command("create", Rank.OWNER) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				System.out.println("Creating!");
				//PlayerSaving.getSaving().createSQL(player);
				return false;
			}

		});
	}
}
