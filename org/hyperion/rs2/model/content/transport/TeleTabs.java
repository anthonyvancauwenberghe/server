package org.hyperion.rs2.model.content.transport;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Jail;

import java.io.FileNotFoundException;


public class TeleTabs implements ContentTemplate {

	public static void TeleTab(final Player player, int x, int y, int z, int id) {
		x += Combat.random(1);
		y += Combat.random(1);

		if(player.isTeleBlocked()) {
			player.getActionSender().sendMessage("You are currently teleblocked.");
			return;
		}
		if(Jail.inJail(player) && !Rank.isStaffMember(player)) {
			player.getActionSender().sendMessage("You cannot teleport out of jail.");
			return;
		}
		if(player.isDead())
			return;
		if(Combat.getWildLevel(player.getLocation().getX(), player.getLocation().getY()) > 20) {
			player.getActionSender().sendMessage("You cannot teleport above level 20 wilderness.");
			return;
		}
		if(player.duelAttackable > 0 || Duel.inDuelLocation(player)) {
            if(Duel.inDuelLocation(player) && player.duelAttackable < 1)
                Duel.finishFullyDuel(player);
			player.getActionSender().sendMessage("You cannot teleport from duel arena.");
			return;
		}
		if(ContentManager.handlePacket(6, player, 30000, - 1, - 1, - 1) || ContentManager.handlePacket(6, player, 30001, - 1, - 1, - 1)) {
			player.getActionSender().sendMessage("You cannot teleport from fight pits.");
			return;
		}
		if(player.getTimeSinceLastTeleport() < 1600)
			return;
		player.updateTeleportTimer();
		if(player.cE.getOpponent() != null) {
			player.getActionSender().sendMessage("You have lost EP because you have teleported during combat.");
			player.removeEP();
		}

        if(player.getExtraData().getBoolean("cantteleport")) {
            player.sendMessage("You can't teleport in this event");
            return;
        }

		ContentEntity.deleteItem(player, id);
		final int x1 = x;
		final int y1 = y;
		final int z1 = z;
		long delay = 1200;

		player.inAction = false;
		if((player.getLocation().getX() >= 2814 && player.getLocation().getX() <= 2942 && player.getLocation().getY() >= 5250 && player.getLocation().getY() <= 5373)
				&& (x < 2814 || x > 2942 || y < 5250 || y > 5373)) {
			player.getActionSender().showInterfaceWalkable(- 1);
		}
		player.playAnimation(Animation.create(4069, 0));
		player.playGraphics(Graphic.create(678, 0));//perfect !

        //player.getExtraData().put("combatimmunity", System.currentTimeMillis() + Long.valueOf(delay) - 100L + 2400L);
        Combat.resetAttack(player.cE);

		World.submit(new Task(delay, "teletabs") {
			@Override
			public void execute() {
				player.playAnimation(Animation.create(4071, 0));
				this.stop();
			}
		});
		World.submit(new Task(2400, "teletabs2") {
			@Override
			public void execute() {
				player.setTeleportTarget(Location.create(x1, y1, z1));
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
				//case 8011: TeleTab(p,2661,3306,0,a); break;
				case 8012:
					TeleTab(p, 2549, 3113, 0, a);
					break;
                case 18806:
                    final Player opp = p.getBountyHunter().getTarget();
                    if(opp != null) {
                        final int x = opp.getLocation().getX();
                        final int y = opp.getLocation().getY();
                        final int wildLevel = Combat.getWildLevel(x, y);
                        final boolean inMulti = Combat.isInMulti(opp.cE);
                        if(opp.getLocation().inPvPArea()) {
                            if(wildLevel <= 20 && !inMulti) {
                                TeleTab(p, opp.getLocation().getX(), opp.getLocation().getY(), opp.getLocation().getZ(), a);
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