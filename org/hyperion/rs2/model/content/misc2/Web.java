package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;

public class Web implements ContentTemplate {

	public static boolean slash(final Player player, final Location loc, final int objectId) {
		player.face(loc);
		ContentEntity.startAnimation(player, 451);
		boolean successful = Misc.random(2) == 0 ? true : false;
		if(successful) {
			player.getActionSender().sendCreateObject(734, 10, 0, loc);
	        /*GameObject old = World.getWorld().getObjectMap().getObjectAt(loc);
            GameObject newObj = new GameObject(GameObjectDefinition.forId(734),loc,10,0);
			if(old == null)
				World.getWorld().getObjectMap().addObject(newObj);
			else
				World.getWorld().getObjectMap().replace(old, newObj);*/
			player.getActionSender().sendMessage("You successfully slash the web.");
			refreshWeb(player, loc/*,newObj*/);
		} else {
			player.getActionSender().sendMessage("You fail to slash the web.");
			return false;
		}
		return true;
	}

	public static void refreshWeb(final Player player, final Location loc/*,final GameObject old*/) {
		World.getWorld().submit(new Event(20000) {
			public void execute() {
				//World.getWorld().getObjectMap().replace(old, new GameObject(GameObjectDefinition.forId(733),loc,10,0));
				player.getActionSender().sendCreateObject(733, 10, 0, loc);
				this.stop();
			}
		});

	}

	@Override
	public boolean clickObject(Player player, int type, int objectId, int x, int y,
	                           int d) {
		if(type == 6) {
            if(objectId == 1765) {
                if(player.getLastAttack().timeSinceLastAttack() > 5000) {
                    Magic.teleport(player, Location.create(2272, 4682, 0), true);
                } else {
                    player.sendMessage("You're a bit busy to be climbing down a ladder");
                }
                return true;
            }
            slash(player, Location.create(x, y, 0), objectId);

        }
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	@Override
	public int[] getValues(int type) {
		if(type == 6) {
			int[] webs = {733, 1765,};
			return webs;
		}
		return null;
	}

}
