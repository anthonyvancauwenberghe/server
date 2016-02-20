package org.hyperion.engine.task.impl;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.ContentEntity;

/**
 * @author Arsen Maxyutov.
 */
public class OverloadDrinkingTask extends Task {

	public static final int HIT_TIMES = 5;

	public static final int DAMAGE = 10;

	private Player player;

	private int counter = HIT_TIMES;

	public OverloadDrinkingTask(Player player) {
		super(1100L);
		this.player = player;
	}

	@Override
	public void execute() {
		ContentEntity.startAnimation(player, 3170);
		player.cE.hit(DAMAGE, null, false, Constants.EMPTY);
		counter--;
		if(counter <= 0) {
			this.stop();
		}
	}

}
