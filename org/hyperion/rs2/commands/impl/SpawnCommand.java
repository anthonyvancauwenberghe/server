package org.hyperion.rs2.commands.impl;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.misc.ItemSpawning;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack Daniels.
 */
public class SpawnCommand extends Command {

	/**
	 * A Map to hold the keywords.
	 */
	private static final Map<String, Integer> keywords = new HashMap<String, Integer>();

	/**
	 * A map holding the ids.
	 */
	private static final Map<Integer, String> ids = new HashMap<Integer, String>();


	public static void setKeyword(String keyword, int id) {
		keywords.put(keyword, id);
		ids.put(id, keyword);
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

	public static Map giveSpawnableKeywords() {
		return keywords;
	}

	public static Map giveSpawnables() {
		return ids;
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
			String key = ids.get(id);
			if(key != null) {
				player.getActionSender().sendMessage("You could also have used the command ::item " + key + " " + amount);
			}
		}
		return true;
	}

	public static void init() {
		if (!Server.getConfig().getBoolean("logssql"))
			return;
		try {
			long start = System.currentTimeMillis();
			ResultSet rs = World.getWorld().getLogsConnection().query("SELECT * FROM keywords WHERE 1");
			if(rs == null)
				return;
			while(rs.next()) {
				String keyword = rs.getString("keyword");
				int id = Integer.parseInt(rs.getString("id"));
				setKeyword(keyword, id);
			}
			long delta = System.currentTimeMillis() - start;
			System.out.println("Loaded Spawn Command in: " + delta + " ms.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
