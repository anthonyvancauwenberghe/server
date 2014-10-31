package org.hyperion;

import org.hyperion.rs2.model.World;
import org.hyperion.util.Time;

public class ServerStatistics {

	public static final String TABLE_NAME = "playerstatistics";

	private int maxPlayercount;

	public int getMaxPlayercount() {
		return maxPlayercount;
	}

	public void print() {
		int players = World.getWorld().getPlayers().size();
		if(players > maxPlayercount)
			maxPlayercount = players;
		String message = "Players online: " + players + ", Max: " + maxPlayercount
				+ ", Uptime: " + Server.getUptime();
		System.out.println(message);
	}

	public String getQuery() {
		int players = World.getWorld().getPlayers().size();
		long minutes = Time.currentTimeMinutes();
		String base = "INSERT INTO " + TABLE_NAME + "(players,time) VALUES (";
		return base + players + "," + minutes + ");";
	}


}
