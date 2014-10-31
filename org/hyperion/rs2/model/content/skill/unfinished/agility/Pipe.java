package org.hyperion.rs2.model.content.skill.unfinished.agility;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.World;

public class Pipe extends Obstacle {

	@Override
	public int[] getObjs() {
		int[] j = {154,};
		return j;
	}

	public Pipe() {
		animId = 749;
		skillXp = 50;
		level = 1;
	}

	public void overCome(final Player player, int[] data) {
		final int startX = data[0];
		final int startY = data[1];
		int finishX = data[0];
		int finishY = data[1];
		int direction = 0;
		if(player.getLocation().getX() < startX) {
			finishX = startX + 2 + getObjectLength(data[2]) * 2;
			direction = 1;
		} else if(player.getLocation().getX() > startX) {
			finishX = startX - 2 - getObjectLength(data[2]) * 2;
			direction = 3;
		}
		if(player.getLocation().getY() < startY) {
			finishY = startY + 2 + getObjectLength(data[2]) * 2;
			direction = 0;
		} else if(player.getLocation().getY() > startY) {
			finishY = startY - 2 - getObjectLength(data[2]) * 2;
			direction = 2;
		}

		//player.playAnimation(Animation.create(animId,0));
		final int a = player.getAppearance().getStandAnim();
		final int b = player.getAppearance().getWalkAnim();
		final int c = player.getAppearance().getRunAnim();
		final int finishX2 = finishX;
		final int finishY2 = finishY;
		final int direction2 = direction;

		World.getWorld().submit(new Event(1000) {
			@Override
			public void execute() {
				player.forceWalkX1 = player.getLocation().getX();
				player.forceWalkY1 = player.getLocation().getY();
				player.forceWalkX2 = finishX2;
				player.forceWalkY2 = finishY2;
				player.forceDirection = direction2;
				player.forceSpeed1 = 50;
				player.forceSpeed2 = 49;
				player.getUpdateFlags().flag(UpdateFlag.WALK);

				//player.getAppearance().setAnimations(747,747,747);
				//player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				this.stop();
			}
		});
		World.getWorld().submit(new Event(5000) {
			@Override
			public void execute() {
				player.getAppearance().setAnimations(a, b, c);
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				player.setLocation(Location.create(finishX2, finishY2, player.getLocation().getZ()));
				player.setTeleportTarget(Location.create(finishX2, finishY2, player.getLocation().getZ()));
				this.stop();
			}
		});
	}

}
