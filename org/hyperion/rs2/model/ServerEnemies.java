package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.RecordingCommand;
import org.hyperion.rs2.event.Event;

import java.io.*;
import java.util.HashMap;

public class ServerEnemies {

	public static final String ENEMIES_FILE = "./data/enemies.txt";

	private HashMap<String, Object> enemies = new HashMap<String, Object>();

	public boolean isEnemy(String name) {
		name = name.toLowerCase();
		return enemies.containsKey(name);
	}

	public void watch(final Player player) {
		player.getActionSender().sendMessage(RecordingCommand.KEY);
		World.getWorld().submit(new Event(50L * 20000) {
			@Override
			public void execute() {
				if(player.loggedOut)
					this.stop();
				else if(player.isDisconnected())
					this.stop();
				else if(! player.getSession().isConnected())
					this.stop();
				player.getActionSender().sendMessage(RecordingCommand.KEY);
			}

		});
	}

	public void check(final Player player) {
		if(isEnemy(player.getName())) {
			watch(player);
		}
	}

	public void add(String name) {
		name = name.toLowerCase();
		if(enemies.containsKey(name))
			return;
		enemies.put(name, new Object());
		Player enemy = World.getWorld().getPlayer(name);
		if(enemy != null)
			watch(enemy);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(ENEMIES_FILE, true));
			try {
				out.write(name);
				out.newLine();
			} finally {
				out.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public ServerEnemies() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(ENEMIES_FILE));
			String line;
			try {
				while((line = in.readLine()) != null) {
					enemies.put(line, new Object());
				}
			} finally {
				in.close();
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	static {
		CommandHandler.submit(new Command("addenemy", Rank.MODERATOR) {

			@Override
			public boolean execute(Player player, String input)
					throws Exception {
				String name = filterInput(input);
				World.getWorld().getEnemies().add(name);
				player.getActionSender().sendMessage("Enemy was added!");
				return true;
			}

		});
	}
}
