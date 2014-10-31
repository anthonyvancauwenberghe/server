package org.hyperion.rs2.model.content.minigame;
// Yay

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class MageArena implements ContentTemplate {

	public void startMinigame(Player player) {
		int height = 0;
		ContentEntity.teleport(player, 3104, 3934, height);
		//teleport to arena,
		World.getWorld().getNPCManager().addNPC(3104 + 3, 3934 + 3, height, 907, - 1);
		//spawn kolodian

	}

	public void wonMinigame(Player player) {
		ContentEntity.sendMessage(player, "Congratz you beat mage arena.");
		ContentEntity.teleport(player, 2541, 4716, 0);
	}

	@Override
	public void init() throws FileNotFoundException {
	}

	@Override
	public int[] getValues(int type) {
		if(type == 16) {
			int[] j = {905, 907, 908, 909, 910, 911,};
			return j;
		}
		if(type == 6) {
			int[] j = {2878, 2879};
			return j;
		}
		return null;
	}

	@Override
	public boolean clickObject(final Player client, final int type, final int oId, final int oX, final int oY, final int a) {
		if(type == 16) {
			if(oId == 905) {
				startMinigame(client);
			} else if(oId != 911)
				World.getWorld().getNPCManager().addNPC(client.getLocation().getX() + 3, client.getLocation().getY() + 3, 0, (oId + 1), - 1);
			else
				wonMinigame(client);
		} else if(type == 6) {
			if(oId == 2878 || oId == 2879) {
				client.getWalkingQueue().reset();
				if(oId == 2878) {
					client.getWalkingQueue().addStep(2542, client.getLocation().getY() + 1);
					client.getWalkingQueue().addStep(2542, client.getLocation().getY() + 2);
				} else {
					client.getWalkingQueue().addStep(2509, client.getLocation().getY() - 1);
					client.getWalkingQueue().addStep(2509, client.getLocation().getY() - 2);
				}
				client.getWalkingQueue().finish();
				World.getWorld().submit(new Event(2000) {
					@Override
					public void execute() {
						ContentEntity.startAnimation(client, 804);
						this.stop();
					}
				});
				World.getWorld().submit(new Event(3000) {
					@Override
					public void execute() {
						ContentEntity.startAnimation(client, - 1);
						if(oId == 2878)
							ContentEntity.teleport(client, 2509, 4689, 0);
						else
							ContentEntity.teleport(client, 2542, 4718, 0);
						this.stop();
					}
				});
			}
		}
		return false;
	}
}