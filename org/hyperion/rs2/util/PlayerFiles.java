package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.util.Misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PlayerFiles {


	/**
	 * @param playerName
	 * @returns Checks whether a character file for the specified username exists.
	 */
	public static boolean exists(String playerName) {
		return new File("./Data/characters/" + playerName.toLowerCase() + ".txt").exists();
	}

	/**
	 * @param playerName
	 * @returns The password of the player with the specified username.
	 */
	public static String getPassword(String playerName) {
		playerName = playerName.toLowerCase();
		String pass = "";
		if(! exists(playerName))
			return pass;
		try {
			BufferedReader br = new BufferedReader(new FileReader("./Data/characters/" + playerName + ".txt"));
			String line = br.readLine();
			while((line = br.readLine()) != null) {
				if(line.startsWith("Pass=")) {
					pass = line.replace("Pass=", "");
					break;
				}
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return pass;
	}


	/**
	 * Saving
	 */

	private static final char[] validChars = {
			'_', ' ', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9'
	};


	public static boolean saveGame(Player p, String message) {
		char[] charArray = p.getName().toCharArray();
		for(int i = 0; i < charArray.length; i++) {
			if(! Misc.contains(charArray[i], validChars))
				return false;
		}
		PlayerSaving.getSaving().save(p, message);
		return true;
	}


	/**
	 * Saves the player to his character file.
	 *
	 * @param p
	 * @return
	 */
	public static boolean saveGame(Player p) {
		char[] charArray = p.getName().toCharArray();
		for(int i = 0; i < charArray.length; i++) {
			if(! Misc.contains(charArray[i], validChars)) {
				System.out.println("INVALID CHARACTER NAME");
				return false;
			}
		}
		PlayerSaving.getSaving().save(p);
		return true;
	}
}
