package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class Jail implements ContentTemplate {

	public static final Location LOCATION = Location.create(2097, 4428, 0);

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c,
	                           int d) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getValues(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean inJail(Player player) {
		if(player.getLocation().getX() >= 2050 && player.getLocation().getX() <= 2130)
			if(player.getLocation().getY() >= 4400 && player.getLocation().getY() <= 4460)
				return true;
		return false;
	}
	//4439,2089,2108,4419
}
