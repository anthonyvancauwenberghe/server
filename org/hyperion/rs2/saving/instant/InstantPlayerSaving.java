package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.instant.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds all the PlayerSaving logic and settings.
 *
 * @author Arsen Maxyutov
 */
public abstract class InstantPlayerSaving {

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
	private final static InstantPlayerSaving singleton = new TextFilePlayerSaving();

	/**
	 * Constructs a new PlayerSaving.
	 */
	public InstantPlayerSaving() {
		saveList = new ArrayList<SaveObject>();
		initSaveObjects();
		((ArrayList<SaveObject>) saveList).trimToSize();
		saveData = new HashMap<String, SaveObject>(); 
		for (SaveObject so : saveList) {
			saveData.put(so.getName(), so);
		}	
	}

	/**
	 * Initializes all SaveObjects in a specific order.
	 */
	private void initSaveObjects() {
		saveList.add(new SaveName("Name"));
		saveList.add(new SavePass("Pass"));
		saveList.add(new SaveStatus("Status"));
		saveList.add(new SaveAccValue("AccValue"));
		saveList.add(new SaveIP("IP"));
		saveList.add(new SaveCreatedString("CreatedStr"));
		saveList.add(new SaveLocation("Location"));

		/*Other values*/
		saveList.add(new SaveElo("Elo"));
		saveList.add(new SaveDiced("Diced"));
		//saveList.add(new SavePreviousSessionTime("PreviousTime"));
		saveList.add(new SaveCreatedLong("CreatedLong"));
		saveList.add(new SaveLastHonorPointsReward("LastHonors"));
		saveList.add(new SaveAtkType("AtkType"));
		saveList.add(new SaveMagicbook("MagicBook"));
		saveList.add(new SavePrayerBook("Altar"));
		saveList.add(new SaveDonatorPointsBought("DonatorsBought"));
		//saveList.add(new SaveDonatorPoints("DonatorPoints"));
		saveList.add(new SavePkPoints("PkPoints"));
		saveList.add(new SaveVotePoints("VotePoints"));
		saveList.add(new SaveHonorPoints("HonourPoints"));
		saveList.add(new SaveSkullTimer("Skull"));
		saveList.add(new SaveEP("EP"));
		saveList.add(new SaveArmaKills("Arma-KC"));
		saveList.add(new SaveBandosKills("Band-KC"));
		saveList.add(new SaveZamorakKills("Zammy-KC"));
		saveList.add(new SaveSaraKills("Sara-KC"));
		saveList.add(new SaveKillcount("KillCount"));
		saveList.add(new SaveDeathcount("DeathCount"));
		saveList.add(new SaveFightCavesWave("FightCavesWave"));
		//saveList.add(new SaveMaxCape("MaxCape"));
		//saveList.add(new SaveCompCape("CompCape"));
		// Containers, skills etc
		saveList.add(new SaveSkills("Skills"));
		saveList.add(new SaveInventory("Inventory"));
		saveList.add(new SaveBank("Bank"));
		saveList.add(new SaveEquipment("Equip"));
		saveList.add(new SaveLook("Look"));
		saveList.add(new SaveFriends("Friends"));
		
	}

	

	/**
	 * The save method.
	 * @param player
	 * @return
	 */
	public abstract boolean save(Player player);
	
	/**
	 * The load method.
	 * @param player
	 * @return
	 */
	public abstract boolean load(Player player);

	
	/**
	 * @param player
	 * @param message
	 */
	public void save(Player player, String message) {
		save(player);
	}

	/**
	 * Gets the PlayerSaving singleton.
	 *
	 * @return
	 */
	public static InstantPlayerSaving getSaving() {
		return singleton;
	}

	
}
