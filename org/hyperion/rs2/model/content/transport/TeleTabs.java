package org.hyperion.rs2.model.content.transport;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;


public class TeleTabs implements ContentTemplate {

	public static void TeleTab(final Player player, int x, int y, int z, int id) {
		x += Combat.random(1);
		y += Combat.random(1);

		if(player.isTeleBlocked()) {
			player.getActionSender().sendMessage("You are currently teleblocked.");
			return;
		}

		if(player.isDead())
			return;

		if(player.getTimeSinceLastTeleport() < 1600)
			return;

		if(!player.getLocation().canTeleport(player))
			return;

		ContentEntity.deleteItem(player, id);
		final int x1 = x;
		final int y1 = y;
		final int z1 = z;
		long delay = 1200;

		player.inAction = false;
		if((player.getPosition().getX() >= 2814 && player.getPosition().getX() <= 2942 && player.getPosition().getY() >= 5250 && player.getPosition().getY() <= 5373)
				&& (x < 2814 || x > 2942 || y < 5250 || y > 5373)) {
			player.getActionSender().showInterfaceWalkable(- 1);
		}
		player.playAnimation(Animation.create(4069, 0));
		player.playGraphics(Graphic.create(678, 0));//perfect !

        //player.getExtraData().put("combatimmunity", System.currentTimeMillis() + Long.valueOf(delay) - 100L + 2400L);
        Combat.resetAttack(player.cE);

		World.submit(new Task(delay,"teletabs1") {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(4071, 0));
				this.stop();
			}
		});
		World.submit(new Task(2400,"teletabs2") {
			@Override
			public void execute() {
				player.setTeleportTarget(Position.create(x1, y1, z1));
				player.playAnimation(Animation.create(- 1, 0));
				this.stop();
			}
		});
	}


	@Override
	public boolean clickObject(Player p, int type, int a, int b,
	                           int c, int d) {
		if(type == 1) {
			switch(a) {
				case 8007:
					TeleTab(p, 3216, 3424, 0, a);
					break;
				case 8008:
					TeleTab(p, 3221, 3217, 0, a);
					break;
				case 8009:
					TeleTab(p, 2964, 3370, 0, a);
					break;
				case 8010:
					TeleTab(p, 2756, 3479, 0, a);
					break;
				case 8011:
					TeleTab(p, 2661, 3306, 0, a);
					break;
				case 8012:
					TeleTab(p, 2549, 3113, 0, a);
					break;
                case 18806:
                    final Player opp = p.getBountyHunter().getTarget();
                    if(opp != null) {
                        final int x = opp.getPosition().getX();
                        final int y = opp.getPosition().getY();
                        final int wildLevel = Combat.getWildLevel(x, y);
                        final boolean inMulti = Combat.isInMulti(opp.cE);
                        if(opp.getPosition().inPvPArea()) {
                            if(wildLevel <= 20 && !inMulti) {
                                TeleTab(p, opp.getPosition().getX(), opp.getPosition().getY(), opp.getPosition().getZ(), a);
                            } else {
                                DialogueManager.openDialogue(p, 171);
                            }
                        }
                    }
                    break;
			}

		}
		return false;
	}

	@Override
	public void init() throws FileNotFoundException {

	}

	@Override
	public int[] getValues(int type) {
		if(type == 1) {
			int[] j = {8007, 8008, 8009, 8010, 8011, 8012,18806,};
			return j;
		}
		return null;
	}
}