package org.hyperion.rs2.commands.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack Daniels.
 */
public class SpawnCommand extends Command {

	private static final Map<String, Integer> keywords = new HashMap<>();


	public static void setKeyword(String keyword, int id) {
		keywords.put(keyword, id);
		saveKeywords();
	}

	public static Integer getId(String keyword) {
		return keywords.get(keyword);
	}

	/**
	 * Constructs a new spawn command.
	 *
	 * @param name
	 */
	public SpawnCommand(String name) {
		super(name, Rank.PLAYER);
	}

	@Override
	public boolean execute(Player player, String input) {
		if(! Server.SPAWN)
			return false;
		String keywordInput = filterInput(input);
		String[] parts = keywordInput.split(" ");
		String keyword = parts[0];
		int amount = 1;
		if(keywords.get(keyword) != null) {
			try {
				if(parts.length > 1)
					amount = Integer.parseInt(parts[1]);
				int id = keywords.get(keyword);
				ItemSpawning.spawnItem(player, id, amount);
			} catch(Exception e) {
				player.getActionSender().sendMessage("Your command could not be parsed.");
			}
		} else {
			int[] params = getIntArray(input);
			int id = params[0];
			if(params.length > 1)
				amount = params[1];
			ItemSpawning.spawnItem(player, id, amount);
			if(keywords.containsValue(id)) {
				String possibleKeyword = keywords.entrySet().stream().filter(value -> value.getValue() == id).map(Map.Entry::getKey).findAny().orElse(null);
				if(possibleKeyword != null)
					player.getActionSender().sendMessage("You could also have used the command ::item " + possibleKeyword + " " + amount);
			}
		}
		return true;
	}

	private static Map<String, Integer> loadKeywords() {
		File file = new File("./data/json/keywords.json");
		try (FileReader fileReader = new FileReader(file)) {
			JsonParser parser = new JsonParser();
			JsonObject object = (JsonObject)parser.parse(fileReader);
			return new Gson().fromJson(object, new TypeToken<Map<String, Integer>>() {}.getType());
		} catch (FileNotFoundException e) {
			return saveKeywords();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}

	private static Map<String, Integer> saveKeywords() {
		Map<String, Integer> mapToSave = keywords == null ? new HashMap<>() : keywords;

		File fileToWrite = new File("./data/json/keywords.json");

		if (!fileToWrite.getParentFile().exists()) {
			try {
				if(!fileToWrite.getParentFile().mkdirs())
					return mapToSave;
			} catch (SecurityException e) {
				System.out.println("Unable to create directory for keywords saving");
			}
		}
		try (FileWriter writer = new FileWriter(fileToWrite)) {
			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			writer.write(builder.toJson(mapToSave, new TypeToken<Map<String, Integer>>() {}.getType()));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapToSave;
	}
}
