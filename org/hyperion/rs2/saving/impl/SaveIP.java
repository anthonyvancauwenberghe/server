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
	}

	@Override
	public void setValue(Player player, String value) {
			try {
                player.getExtraData().put("oldfullip", value);
                String smallIp = player.getFullIP().substring(0, player.getFullIP().indexOf(":"));
                String shortenedValue = value.substring(0, value.indexOf(":"));
                player.lastIp = shortenedValue.replace("/", "");
                if(!smallIp.equalsIgnoreCase(shortenedValue)) {
                    final File file = new File("./data/possiblehacks.txt");
                    final String date = new Date().toString();
                    TextUtils.writeToFile(file, String.format("Player: %s Old IP: %s New IP: %s Date: %s", player.getName(), shortenedValue,smallIp, date));
                    PossibleHacksHolder.add(new IPChange(player.getName(), shortenedValue, date, smallIp));
                    shortenedValue = shortenedValue.substring(0, shortenedValue.indexOf(".", shortenedValue.indexOf(".") + 1));
                    smallIp = smallIp.substring(0, smallIp.indexOf(".", smallIp.indexOf(".") + 1));
                    if(!shortenedValue.trim().equalsIgnoreCase(smallIp.trim()))
                        player.getExtraData().put("isdrasticallydiff", true);
                }
			} catch(Exception e) {
                System.out.println(value);
				e.printStackTrace();
			}
	}

	@Override
	public String getValue(Player player) {
        /*
        if(!player.getExtraData().getBoolean("cantdoshit"))
		    return player.getFullIP();
        else
        */
            return player.getExtraData().getString("oldfullip");
	}

}
