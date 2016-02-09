package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveFriends extends SaveObject {

	public SaveFriends(String name) {
		super(name);// TODO Auto-generated constructor stub
	}

	@Override
	public boolean save(Player player, BufferedWriter writer)
			throws IOException {
		writer.write(getName());
		writer.newLine();
		for(long friend : player.getFriends().toArray()) {
			writer.write(friend + "");
			writer.newLine();
		}
		return true;
	}

	@Override
	public void load(Player player, String values, BufferedReader reader)
			throws IOException {
		String line;
		while((line = reader.readLine()).length() > 0) {
			long friend = Long.parseLong(line);
			player.getFriends().add(friend);
		}
	}

}
