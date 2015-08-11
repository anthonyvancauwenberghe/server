package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class Afk implements ContentTemplate {

	public static final Location LOCATION = Location.create(2147, 5099, 0);

	public static boolean isAfking(Player player) {
		if(player.getLocation().getX() <= 2164 && player.getLocation().getX() >= 2138)
			if(player.getLocation().getY() <= 5106 && player.getLocation().getY() >= 5091)
				return true;
		return false;
	}

	public static void procesPlayer(Player player) {
		if(isAfking(player)) {
			if(Math.random() > 0.5)
				player.getPoints().increasePkPoints(1);
		}
	}

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c,
	                           int d) {
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {

	}

	@Override
	public int[] getValues(int type) {

		return null;
	}


}
