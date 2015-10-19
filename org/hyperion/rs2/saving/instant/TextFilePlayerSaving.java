package org.hyperion.rs2.saving.instant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.hyperion.Server;
import org.hyperion.rs2.model.Password;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.util.PasswordEncryption;

public class TextFilePlayerSaving extends InstantPlayerSaving {
	
	/**
	 * The saving directory.
	 */
	public static final File SAVE_DIR = new File(MergedSaving.INSTANT_DIR);

	/**
	 * The buffer size used for saving and loading.
	 */
	public static final int BUFFER_SIZE = 1024;

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
	 * Saves the player's data to his character file.
	 *
	 * @param player
	 * @return true if successful, false if not
	 */
	public boolean save(Player player) {
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter(
					getFileName(player)), BUFFER_SIZE);
			for (SaveObject so : saveList) {
				if(so.getName().contains("custom-inv") || so.getName().contains("custom-equip"))
					continue;
				boolean saved = so.save(player, file);
				if (saved) {
					file.newLine();
				}
			}
			file.close();
			//World.getWorld().getSQLSaving().save(player);
			return true;
		} catch (IOException e) {
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
	public boolean load(Player player) {
		//loadSQL(player);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					getFileName(player)), BUFFER_SIZE);
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() <= 1)
					continue;
				String[] parts = line.split("=");
				String name = parts[0].trim();
				String values = null;
				if (parts.length > 1)
					values = parts[1].trim();
				SaveObject so = saveData.get(name);
				if (so != null) {
					so.load(player, values, in);
				} else {
					//System.out.println("Nulled saveobject for " + player.getName() + " line: " + line);
				}
			}
			in.close();
			World.getWorld().getSQLSaving().load(player);
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.init();
		return true;
	}
	
	

	/**
	 * 
	 * @param name
	 */
	public static void copyFile(String name) {
		copyFile(name,"./data/bugchars/");
	}
	
	/**
	 * 
	 * @param name
	 * @param directory
	 */
	public static boolean copyFile(String name, String directory) {
		try {
			File file = new File(getFileName(name));
			if(!file.exists())
				return false;
			BufferedReader in = new BufferedReader(new FileReader(file));
			BufferedWriter out = new BufferedWriter(new FileWriter(
					directory + name + ".txt"));
			String line;
			while ((line = in.readLine()) != null) {
				out.write(line);
				out.newLine();
			}
			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	static {
		if(!SAVE_DIR.exists()) {
			SAVE_DIR.mkdir();
		}
	}
}
