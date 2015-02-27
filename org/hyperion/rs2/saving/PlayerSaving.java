package org.hyperion.rs2.saving;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Appearance;
import org.hyperion.rs2.model.FriendList;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.recolor.save.SaveRecolorManager;
import org.hyperion.rs2.saving.impl.*;
import org.hyperion.rs2.sql.SQLConnection;
import org.hyperion.rs2.util.TextUtils;

/**
 * This class holds all the PlayerSaving logic and settings.
 *
 * @author Arsen Maxyutov
 */
public class PlayerSaving {

	/**
	 * The saving directory.
	 */
	public static final File SAVE_DIR = new File("./data/characters/");

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
	private final ExecutorService saveThread = Executors
			.newSingleThreadExecutor();

	/**
	 * The sql connection.
	 */
	private SQLConnection sql = null;

	/**
	 * Holds all the SaveObjects meant for loading.
	 */
	private final Map<String, SaveObject> saveData;

	/**
	 * The collection of SaveObjects meant for iteration.
	 */
	private final List<SaveObject> saveList;

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
		saveList.add(new SaveExpLock("XpLock"));
		saveList.add(new SaveTrivia("Trivia"));
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
		saveList.add(new SaveLastVoted("LastVoted")); //
        saveList.add(new SaveFirstVoteTime("FirstVoteTime"));
        saveList.add(new SaveVoteCount("VoteCount"));
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
		saveList.add(new SaveSSHCharges("sshcharges"));
		saveList.add(new SaveNPCKills("npclogs"));
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

        saveList.add(new SaveRecolorManager());
        saveList.add(new SaveCustomSet());
        saveList.add(new SavePermExtraData());
        saveList.add(new SaveGameMode());
        saveList.add(new SaveBonusXP());
        saveList.add(new SavePin());
        saveList.add(new SavePid());
        saveList.add(new SaveDungoneering());
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
		if(Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("Saving your account");
		}
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(
					getFileName(player)), BUFFER_SIZE);
			for(SaveObject so : saveList) {
				boolean saved = so.save(player, file);
				if(saved) {
					file.newLine();
				}
			}
			file.close();
			return true;
		} catch(IOException e) {
			System.out.println("Player's name: " + player.getName());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves player properties, player items and player skills to a SQL database.
	 *
	 * @param player
	 * @return
	 */
	public boolean saveSQL(Player player) {
		long start = System.currentTimeMillis();
		saveSingleValues(player);
		saveBank(player);
		saveEquipment(player);
		saveInventory(player);
		saveFriends(player);
		saveSkills(player);
		saveLook(player);
		long delta = System.currentTimeMillis() - start;
		System.out.println("Delta: " + delta + " ms.");
		return true;
	}

	/**
	 * @param player
	 * @return
	 */
	public void saveSingleValues(Player player) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + PLAYERS_TABLE + " SET ");
		boolean first = true;
		for(SaveObject so : saveList) {
			if(so.getName().equalsIgnoreCase("name"))
				continue;
			if(so instanceof SaveSingleValue) {
				if(! first)
					sb.append(",");
				sb.append(so.getName().toLowerCase());
				sb.append("=");
				if(so instanceof SaveString) {
					sb.append("'" + ((SaveSingleValue) so).getValue(player) + "'");
				} else {
					sb.append(((SaveSingleValue) so).getValue(player));
				}
				first = false;
			}
		}
		sb.append(" WHERE name='" + player.getName().toLowerCase() + "'");
		sql.offer(sb.toString());
	}

	/**
	 * @param tableName
	 * @param playerName
	 * @param container
	 * @throws SQLException
	 */
	private void saveContainer(String tableName, String playerName, Container container) {
		for(int i : container.getChangedSlots()) {
			StringBuilder sb = new StringBuilder();
			Item item = container.get(i);
			sb = new StringBuilder();
			sb.append("UPDATE " + tableName + " SET id = ");
			if(item != null) {
				sb.append(item.getId() + ", amount = " + item.getCount());
			} else {
				sb.append("-1, amount = -1");
			}
			sb.append(" WHERE slot = " + i + " AND username = '" + playerName + "'");
			sql.offer(sb.toString());
		}
		container.updatePreviousItems();
	}

	/**
	 * @param player
	 */
	public void saveBank(Player player) {
		saveContainer(BANK_TABLE, player.getName().toLowerCase(), player.getBank());
	}

	/**
	 * @param player
	 */
	public void saveEquipment(Player player) {
		saveContainer(EQUIPMENT_TABLE, player.getName().toLowerCase(), player.getEquipment());
	}

	/**
	 * @param player
	 */
	public void saveInventory(Player player) {
		saveContainer(INVENTORY_TABLE, player.getName().toLowerCase(), player.getInventory());
	}

	/**
	 * @throws SQLException
	 */
	public void saveFriends(Player player) {
		FriendList list = player.getFriends();
		long[] friends = list.toArray();
		for(int i : list.getChangedSlots()) {
			String query = "UPDATE player_friends SET friend = " + friends[i] + " WHERE slot = " + i + " AND username = '" + player.getName().toLowerCase() + "'";
			sql.offer(query);
		}
		list.updatePreviousFriends();
	}

	/**
	 * @throws SQLException
	 */
	public void saveSkills(Player player) {
		Skills skills = player.getSkills();
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			if(skills.hasChanged(i)) {
				String query = "UPDATE player_skills SET level = " + skills.getLevel(i) + ", exp = " + skills.getExperience(i) + " WHERE skill = " + i + " AND username = '" + player.getName().toLowerCase() + "'";
				sql.offer(query);
			}
		}
	}

	/**
	 * @throws SQLException
	 */
	public void saveLook(Player player) {
		int[] look = player.getAppearance().getLook();
		for(int i : player.getAppearance().getChangedSlots()) {
			String query = "UPDATE player_look SET value = " + look[i] + " WHERE slot = " + i + " AND username = '" + player.getName().toLowerCase() + "'";
			//System.out.println(query);
			sql.offer(query);
		}
	}


	public boolean loadSQL(Player player) {
		String playerName = player.getName().toLowerCase();
		try {
			ResultSet rs = sql.query("SELECT * FROM player_data WHERE name = '" + playerName + "'");
			while(rs.next()) {
				for(SaveObject so : saveList) {
					String columnName = so.getName().toLowerCase();
					if(so instanceof SaveInteger) {
						((SaveInteger) so).setValue(player, rs.getInt(columnName));
					} else if(so instanceof SaveLong) {
						((SaveLong) so).setValue(player, rs.getLong(columnName));
					} else if(so instanceof SaveString) {
						((SaveString) so).setValue(player, rs.getString(columnName));
					} else if(so instanceof SaveBoolean) {
						((SaveBoolean) so).setValue(player, rs.getBoolean(columnName));
					}
				}
			}
			loadContainer(player.getBank(), sql, "player_bank", playerName);
			loadContainer(player.getInventory(), sql, "player_inventory", playerName);
			loadContainer(player.getEquipment(), sql, "player_equipment", playerName);
			loadLook(playerName, sql, player.getAppearance());
			loadFriends(playerName, sql, player.getFriends());
			loadSkills(playerName, sql, player.getSkills());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean loadContainer(Container container, SQLConnection sql, String tableName, String playerName) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM " + tableName + " WHERE id > 0 AND amount > 0 AND name = '" + playerName + "'");
		if(rs == null)
			return true;
		while(rs.next()) {
			int slot = rs.getInt("slot");
			int id = rs.getInt("id");
			int amount = rs.getInt("amount");
			container.set(slot, new Item(id, amount));
		}
		rs.close();
		return true;
	}


	private void loadLook(String playerName, SQLConnection sql, Appearance appearance) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM player_look WHERE name = '" + playerName + "'");
		int[] look = new int[13];
		if(rs == null)
			return;
		while(rs.next()) {
			int slot = rs.getInt("slot");
			int value = rs.getInt("value");
			look[slot] = value;
		}
		appearance.setLook(look);
		rs.close();
	}

	private void loadFriends(String playerName, SQLConnection sql, FriendList list) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM player_friends WHERE name = '" + playerName + "'");
		if(rs == null)
			return;
		while(rs.next()) {
			int slot = rs.getInt("slot");
			int friend = rs.getInt("friend");
			list.set(friend, slot);
		}
		rs.close();
	}


	private void loadSkills(String playerName, SQLConnection sql, Skills skills) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM player_skills WHERE name = '" + playerName + "'");
		if(rs == null)
			return;
		while(rs.next()) {
			int skill = rs.getInt("skill");
			int exp = rs.getInt("exp");
			int level = rs.getInt("level");
			skills.setSkill(skill, level, exp);
		}
		rs.close();
	}

	/**
	 * Inserts the player into the SQL database.
	 *
	 * @param player
	 * @return
	 */
	public void createSQL(Player player) {
		String playerName = player.getName().toLowerCase();
		createPlayer(player);
		createContainer(playerName, Inventory.SIZE, INVENTORY_TABLE);
		createContainer(playerName, Equipment.SIZE, EQUIPMENT_TABLE);
		createContainer(playerName, Bank.SIZE, BANK_TABLE);
		createFriendlist(playerName, FriendList.SIZE);
		createSkills(playerName, Skills.SKILL_COUNT);
		createLook(playerName, player.getAppearance());
	}

	/**
	 */
	private void createPlayer(Player player) {
		StringBuilder sb = new StringBuilder();
		LinkedList<SaveSingleValue> savingFields = new LinkedList<SaveSingleValue>();
		sb.append("INSERT INTO player_data (");
		boolean first = true;
		for(SaveObject so : saveList) {
			if(so instanceof SaveSingleValue) {
				if(! first)
					sb.append(",");
				sb.append(so.getName().toLowerCase());
				savingFields.add((SaveSingleValue) so);
				first = false;
			}
		}
		first = true;
		sb.append(")VALUES(");
		for(SaveSingleValue ssv : savingFields) {
			if(! first)
				sb.append(",");
			if(ssv instanceof SaveString) {
				sb.append("'" + ssv.getValue(player) + "'");
			} else if(ssv instanceof SaveBoolean) {
				if((Boolean) ssv.getValue(player)) {
					sb.append(1);
				} else {
					sb.append(0);
				}
			} else {
				sb.append(ssv.getValue(player));
			}
			first = false;
		}
		sb.append(")");
		String query = sb.toString();
		sql.offer(query);
	}

	/**
	 * @param playerName
	 * @param size
	 * @param tableName
	 * @throws SQLException
	 */
	private void createContainer(String playerName, int size, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + tableName + " (id,amount,slot,username) VALUES ");
		for(int i = 0; i < 3/*size*/; i++) {
			if(i > 0)
				sb.append(",");
			sb.append("(0,0," + i + ",'" + playerName + "')");
		}
		sql.offer(sb.toString());
	}

	/**
	 * @param playerName
	 * @param size
	 * @throws SQLException
	 */
	private void createFriendlist(String playerName, int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + FRIENDS_TABLE + " (friend,slot,username) VALUES ");
		for(int i = 0; i < size; i++) {
			if(i > 0)
				sb.append(",");
			sb.append("(0," + i + ",'" + playerName + "')");
		}
		sql.offer(sb.toString());
	}

	/**
	 * @param playerName
	 * @param size
	 * @throws SQLException
	 */
	private void createSkills(String playerName, int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + SKILLS_TABLE + " (skill,level,exp,username) VALUES ");
		for(int i = 0; i < size; i++) {
			if(i > 0)
				sb.append(",");
			if(i != Skills.HITPOINTS)
				sb.append("(" + i + ",1,0,'" + playerName + "')");
			else
				sb.append("(" + i + ",10,1184,'" + playerName + "')");
		}
		sql.offer(sb.toString());
	}

	/**
	 * @param playerName
	 * @param app
	 * @throws SQLException
	 */
	private void createLook(String playerName, Appearance app) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + LOOK_TABLE + " (slot,value,username) VALUES ");
		for(int i = 0; i < app.getLook().length; i++) {
			if(i > 0)
				sb.append(",");
			sb.append("(" + i + "," + app.getLook()[i] + ",'" + playerName + "')");
		}
		sql.offer(sb.toString());
	}


	/**
	 * Loads the player's data from his character file.
	 *
	 * @param player
	 */
	public void load(Player player) {
		//loadSQL(player);

		try {
			BufferedReader in = new BufferedReader(new FileReader(
					getFileName(player)), BUFFER_SIZE);
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
					System.out.println("Nulled so for " + player.getName()
							+ " line: " + line);
					System.out.println("Name:" + name);
					player.getSession().close(true);
					copyFile(player.getName());
					return;
				}
				so.load(player, values, in);
			}
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
					"./data/characters/" + name + ".txt"));
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
		return SAVE_DIR + "/" + name.toLowerCase() + ".txt";
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
