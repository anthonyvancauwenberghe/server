package org.hyperion.rs2.saving.impl;

import java.io.File;
import java.util.Date;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.possiblehacks.IPChange;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.saving.SaveString;
import org.hyperion.rs2.util.TextUtils;

public class SaveIP extends SaveString {

	public SaveIP(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Player player, String value) {
			try {
                String smallIp = player.getFullIP().substring(0, player.getFullIP().indexOf(":"));
                String shortenedValue = value.substring(0, value.indexOf(":"));
                if(!smallIp.equalsIgnoreCase(shortenedValue)) {
                    final File file = new File("./data/possiblehacks.txt");
                    final String date = new Date().toString();
                    TextUtils.writeToFile(file, String.format("Player: %s Old IP: %s New IP: %s Date: %s", player.getName(), shortenedValue,smallIp, date));
                    PossibleHacksHolder.add(new IPChange(player.getName(), shortenedValue, date, smallIp));
                }
			} catch(Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public String getValue(Player player) {
		return player.getFullIP();
	}

}
