package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class OtherDonatorsPlace implements ContentTemplate {

	public final static Location LOCATION = Location.create(2313, 9809, 0);

    public static NPC DICER = World.getWorld().getNPCManager().addNPC(2320, 9806, 0, 2999, - 1);
    public static NPC DICER1 = World.getWorld().getNPCManager().addNPC(2847, 5218, 0, 2999, - 1);
    public static NPC DICER2 = World.getWorld().getNPCManager().addNPC(3496, 3573, 0, 2999, - 1);

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c,
	                           int d) {
		if(type == 10 || type == 11) {
	        /*switch(a){
            case 3199:
				Bank.open(player, false);
				break;
			case 6167:
				ShopManager.open(player, 74);
				break;
			}*/
		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
		addNPC(3199, 3170, 9756, 0);
		addNPC(6167, 3163, 9757, 0);
	}

	private void addNPC(int id, int x, int y, int z) {
		World.getWorld().getNPCManager().addNPC(x, y, z, id, - 1);
	}

	@Override
	public int[] getValues(int type) {
		if(type == 10 || type == 11) {
			int[] npcs = {3199, 6167};
			return npcs;
		}
		return null;
	}

}
